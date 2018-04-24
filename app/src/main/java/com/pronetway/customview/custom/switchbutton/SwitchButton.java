package com.pronetway.customview.custom.switchbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pronetway.customview.R;


/**
 * Description:TODO
 * Create Time: 2018/4/24.11:30
 * Author:jin
 * Email:210980059@qq.com
 */
public class SwitchButton extends View {

    private int mOpenColor;
    private int mCloseColor;
    private int mMiddleColor;
    //绘制背景的path
    Path mPath = new Path();
    private Paint mPaint;

    private float mAnimeSpeed = 0.1f;//动画速度
    private float mAnimeInit = 1.0f;//动画初始状态
    private boolean mSwitchState;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initTa(context, attrs);
    }

    private void initTa(Context context, AttributeSet attrs) {
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        mOpenColor = ta.getColor(R.styleable.SwitchButton_openColor, 0xFF4BD763);
        mCloseColor = ta.getColor(R.styleable.SwitchButton_closeColor, 0xFFE3E3E3);
        mMiddleColor = ta.getColor(R.styleable.SwitchButton_middleColor, Color.WHITE);
        //1为打开，2为关闭
        int mState = ta.getInt(R.styleable.SwitchButton_state, 2);
        mSwitchState = mState == 1;
        ta.recycle();
        //初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCloseColor);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int DEFAULT_WIDTH = (int) (56 * getResources().getDisplayMetrics().density + 0.5f) + getPaddingLeft() + getPaddingRight();//默认56dp
        //如果mode是exactly,按widthMeasureSpec中给定的suggest width,不是exactly，看是否为at_most, 此种模式下，width需要在suggest width与DEFAULT_WITH间取小的。
        int finalWidth = resolveSize(DEFAULT_WIDTH, widthMeasureSpec);
        //处理高度，需参考宽度
        int DEFAULT_HEIGHT = (int) ((finalWidth - getPaddingLeft() - getPaddingRight()) * 0.6) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(finalWidth, DEFAULT_HEIGHT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            initPath();
        }
    }

    private void initPath() {
        float sLeft = getPaddingLeft();
        float sTop = getPaddingTop();
        float sRight = getMeasuredWidth() - getPaddingRight();
        float sBottom = getMeasuredHeight() - getPaddingBottom();
        float sCenterX = (sRight + sBottom) / 2;
        float sCenterY = (sBottom + sTop) / 2;

        RectF rectF = new RectF(sLeft, sTop, (sBottom - sTop) + sLeft, sBottom);
        mPath.reset();
        mPath.arcTo(rectF, 90, 180);
        rectF.left = sRight - (sBottom - sTop);
        rectF.right = sRight;
        mPath.arcTo(rectF, 270, 180);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mAnimeInit = 1.0f;
                mSwitchState = !mSwitchState;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
}
