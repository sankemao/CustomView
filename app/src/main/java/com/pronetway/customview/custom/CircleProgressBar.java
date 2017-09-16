package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.pronetway.customview.R;

/**
 * Created by jin on 2017/9/7.
 * 圆形的进度圈
 */
public class CircleProgressBar extends View {

    private int mInnerBackground = Color.RED;
    private int mOuterBackground = Color.RED;
    private int mRoundWidth = 10;
    private float mProgressTextSize = 20;
    private int mProgressTextColor = Color.RED;
    private Paint mInnerPaint;
    private Paint mOuterPaint;
    private Paint mTextPaint;

    private int mMax = 100;
    private int mProgress = 40;

    public CircleProgressBar(Context context) {
        this(context, null);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint(context, attrs);
    }

    /**
     * 设置最大值
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max <= 0) {
            throw new IllegalStateException("进度最大值必须大于0");
        }
        this.mMax = max;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalStateException("进度最大值必须大于0");
        }
        this.mProgress = progress;
        // TODO: 2017/9/8  看一下invalidate的源码.
        invalidate();
    }

    /**
     * 初始化画笔
     */
    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);
        mInnerBackground = a.getColor(R.styleable.CircleProgressBar_innerBackground, mInnerBackground);
        mOuterBackground = a.getColor(R.styleable.CircleProgressBar_outerBackground, mOuterBackground);
        mProgressTextColor = a.getColor(R.styleable.CircleProgressBar_progressTextColor, mProgressTextColor);

        mRoundWidth = (int) a.getDimension(R.styleable.CircleProgressBar_roundWidth, dp2px(mRoundWidth));
        mProgressTextSize = a.getDimension(R.styleable.CircleProgressBar_progressTextSize, sp2px(mProgressTextSize));
        a.recycle();

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(mInnerBackground);
        mInnerPaint.setStrokeWidth(mRoundWidth);
        mInnerPaint.setStyle(Paint.Style.STROKE);

        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(mOuterBackground);
        mOuterPaint.setStrokeWidth(mRoundWidth);
        mOuterPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextSize(mProgressTextSize);
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private float dp2px(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //只需要保证正方形即可
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(Math.min(width, height), Math.min(width, height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int center = getWidth() / 2;
        //先画内圆
        canvas.drawCircle(center, center, center - mRoundWidth / 2, mInnerPaint);
        //画外圆弧(进度)
        RectF rect = new RectF(mRoundWidth / 2, mRoundWidth / 2, getWidth() - mRoundWidth / 2, getHeight() - mRoundWidth / 2);
        if (mProgress == 0) {
            return;
        }
        float percent = (float)mProgress / mMax;
        canvas.drawArc(rect, 0, percent * 360, false, mOuterPaint);

        //画文字
        String text = ((int)(percent * 100)) + "%";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.width() / 2;
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        int baseline = getHeight() / 2 + dy;
        canvas.drawText(text, x, baseline, mTextPaint);
    }
}
