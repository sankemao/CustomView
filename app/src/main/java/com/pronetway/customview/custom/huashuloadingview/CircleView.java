package com.pronetway.customview.custom.huashuloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description:TODO
 * Create Time: 2017/11/30.10:57
 * Author:jin
 * Email:210980059@qq.com
 */
public class CircleView extends View {

    private Paint mPaint;
    private int mColor;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画背景圆形
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        canvas.drawCircle(x, y, x, mPaint);
    }

    /**
     * 改变圆球的颜色
     * @param color 将要改变的颜色
     */
    public void exChangeColor(int color) {
        this.mColor = color;
        mPaint.setColor(mColor);
        postInvalidate();
    }

    public int getColor() {
        return mColor;
    }
}
