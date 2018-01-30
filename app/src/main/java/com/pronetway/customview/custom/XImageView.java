package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.pronetway.customview.R;

/**
 * Description: 圆形ImageView
 * Create Time: 2018/1/29.15:00
 * Author:jin
 * Email:210980059@qq.com
 */
public class XImageView extends AppCompatImageView {

    //资源id
    private @IdRes int mImageId;
    //是否为圆形头像
    private boolean mAsCircle;
    //圆角半径
    private int mRoundCorner;
    //描边宽度
    private int mStrokeWidth;
    //描边颜色
    private int mStrokeColor;
    //描边画笔
    private Paint mStrokePaint;

    public XImageView(Context context) {
        this(context, null);
    }

    public XImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);

        initPaint();

        setImage();
    }

    /**
     * 初始化属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XImageView);
        mImageId = ta.getResourceId(R.styleable.XImageView_android_src, 0);
        mAsCircle = ta.getBoolean(R.styleable.XImageView_x_as_circle, false);
        mRoundCorner = ta.getDimensionPixelSize(R.styleable.XImageView_x_round_corner, 0);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.XImageView_x_stroke_width, 0);
        mStrokeColor = ta.getColor(R.styleable.XImageView_x_stroke_color, Color.WHITE);
        ta.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //描边画笔
        if (mStrokeWidth > 0) {
            mStrokePaint = new Paint();
            mStrokePaint.setAntiAlias(true);
            mStrokePaint.setDither(true);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(mStrokeWidth);
            mStrokePaint.setColor(mStrokeColor);
        }
    }

    private void setImage() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mAsCircle) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int finalWidth = Math.min(width, height);
            setMeasuredDimension(finalWidth, finalWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
