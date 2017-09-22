package com.pronetway.customview.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by jin on 2017/9/19.
 * thanks to https://github.com/zFxiang/LikeView
 */
public class LikeView extends View {

    private int mTextColor;
    private TextPaint mTextPaint;

    private long mNumber = 0;
    private String mPreNumStr = "";//前半截数字字符串.
    private String mOldNumStr = "";//老的数字字符串.
    private String mNewNumStr = "";//新的数字字符串.
    private String mPostOldNumStr = "";//老的后半截数字字符串.
    private String mPostNewNumStr = "";//新的后半截数字字符串.

    private boolean mIsNumAnimateRunning = false;

    private float mTextHeight;
    private float mBaseLine;
    private float mTextWidth;
    private float mPreNumWidth;

    private int defWidth;
    private int defHeight;
    private float textStartX;
    private float textStartY;

    private int direction;//滚动方向.
    private float numAnimateScale = 0;//滚动位置
    private ObjectAnimator mNumAnimator;

    public void add(long num) {
        if (num == 0) {
            return;
        }

        long newNum = mNumber + num;            //新的数字.temp.
        mOldNumStr = String.valueOf(mNumber);   //老的字符串.
        mNewNumStr = String.valueOf(newNum);    //新的字符串.
        mNumber = newNum;                       //新的数字.

        handleNewString();
        roll(num > 0);
    }

    /**
     * 文字滚动.
     * @param rollUp
     */
    private void roll(boolean rollUp) {
        if (mNumAnimator != null && mNumAnimator.isRunning()) {
            mNumAnimator.cancel();
        }

        direction = rollUp ? 1 : -1;
        mNumAnimator = ObjectAnimator.ofFloat(this, "numAnimateScale", 0f, 1f);
        mNumAnimator.setDuration(300);
        mIsNumAnimateRunning = true;
        mNumAnimator.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsNumAnimateRunning = false;
            }
        });
        mNumAnimator.start();
    }

    public LikeView(Context context) {
        this(context, null);
    }

    public LikeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTextColor = Color.parseColor("#888888");

        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(sp2px(14));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        measureTextWidth();
        measureTextHeightAndBaseLineHeight();
        measureDefWidthAndDefHeight();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                add(1);
            }
        });

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureMeasureSpec(widthMeasureSpec, defWidth), measureMeasureSpec(heightMeasureSpec, defHeight));
    }

    /**
     * 测量View的宽高
     *
     * @param measureSpec MeasureSpec实例
     * @param defaultVal  默认值,当mode为UNSPECIFIED和AT_MOST时使用默认值
     * @return 测量后的宽高
     */
    private int measureMeasureSpec(int measureSpec, int defaultVal) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = defaultVal;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(textStartX, textStartY);//将画布挪到文字绘制起点.
        if (!mIsNumAnimateRunning) {
            canvas.drawText(String.valueOf(mNumber), 0, mBaseLine, mTextPaint);
        } else {
            numAnimateScale *= -direction; // -0.6
            LogUtils.d("---------" + numAnimateScale);
            canvas.clipRect(0, 0, 0 + mTextWidth, 0 + mTextHeight);//裁剪.限制显示区域
            //绘制前半截文字.
            canvas.drawText(mPreNumStr, 0, mBaseLine, mTextPaint);
            //绘制旧文字
            canvas.drawText(mPostOldNumStr, mPreNumWidth, mBaseLine + mTextHeight * numAnimateScale, mTextPaint);
            //绘制新文字.
            canvas.drawText(mPostNewNumStr, mPreNumWidth, mBaseLine + mTextHeight * (numAnimateScale + direction), mTextPaint);
        }
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureStartPoint();
        LogUtils.d("onSizeChanged");
    }

    private void measureTextHeightAndBaseLineHeight() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom - fontMetrics.top;
        mBaseLine = -fontMetrics.top;
    }

    /**
     * 对比字符串,并测量文本宽度
     * 将要绘制的文字拆分成两部分.
     */
    private void handleNewString() {
        if (mOldNumStr == null || mNewNumStr == null || mOldNumStr.equals(mNewNumStr)) {
            return;
        }

        if (mOldNumStr.length() != mNewNumStr.length()) {
            mPreNumStr = "";
            mPostOldNumStr = mOldNumStr;
            mPostNewNumStr = mNewNumStr;
        } else {
            int length = mOldNumStr.length();
            int i;
            for (i = 0; i < length; i++) {
                if (mNewNumStr.charAt(i) != mOldNumStr.charAt(i)) {
                    break;
                }
            }

            mPreNumStr = mOldNumStr.substring(0, i);
            mPostOldNumStr = mOldNumStr.substring(i);
            mPostNewNumStr = mNewNumStr.substring(i);
        }

        measureTextWidthChanged();
    }

    private void measureTextWidthChanged() {
        float oldTextWidth = mTextWidth;
        measureTextWidth();
        //如果宽度改变, 要重新测量默认宽高.
        if (oldTextWidth != mTextWidth) {
            int oldDefWidth = defWidth;
            int oldDefHeight = defHeight;
            measureDefWidthAndDefHeight();
            //如果新的默认宽高不等于原来的值,就需要请求重新布局
            if ((defWidth != oldDefWidth || defHeight != oldDefHeight)) {
                requestLayout();
            }
        }
    }

    private void measureDefWidthAndDefHeight() {
        int oldDefHeight = defHeight;
        defHeight = (int) (getPaddingBottom() + getPaddingTop() + mTextHeight);
        defWidth = (int) (getPaddingLeft() + getPaddingRight() + mTextWidth);
        if (defHeight != oldDefHeight) {
            measureStartPoint();
        }
    }

    /**
     * 文字绘制起点.
     */
    private void measureStartPoint() {
        int w = getWidth();
        int h = getHeight();
        //对齐显示偏移量
        int dx;
        int dy;

        dx = (w - defWidth) / 2;
        dy = (h - defHeight) / 2;

        textStartX = getPaddingLeft() + dx;
        textStartY = getPaddingTop() + dy;
    }

    /**
     * 测量文字的宽度
     * mPreNumWidth     前部分文字宽度
     * mTextWidth
     */
    private void measureTextWidth() {
        mPreNumWidth = mTextPaint.measureText(mPreNumStr);

        float width = maxTextWidth(mOldNumStr, mNewNumStr);
        int offset = 1;

        //TODO:老数字, 新数字, 以及新数字+1后宽度的比较, 取最大值.
        mTextWidth = Math.max(width, mTextPaint.measureText(String.valueOf(mNumber + offset)));
    }

    private float maxTextWidth(String oldNumStr, String newNumStr) {
        return Math.max(mTextPaint.measureText(oldNumStr), mTextPaint.measureText(newNumStr));
    }


    private float sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 动画
     */
    public void setNumAnimateScale(float numAnimateScale) {
        this.numAnimateScale = numAnimateScale;
        postInvalidate();
    }

}
