package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
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

    //是否为圆形头像
    private boolean mAsCircle;
    //圆角半径
    private int mRoundCorner;
    //描边宽度
    private int mStrokeWidth;
    //描边颜色
    private int mStrokeColor = Color.WHITE;
    //绘制描边或者形状的画笔
    private Paint mSrcPaint;
    //图层区域
    private RectF mLayer;
    private Path mClipPath;
    private int mTopLeft;
    private int mTopRight;
    private int mBottomLeft;
    private int mBottomRight;

    //圆角path规则
    private float radii[] = new float[8]; //top-left, top-right, bottom-right, bottom-left

    public XImageView(Context context) {
        this(context, null);
    }

    public XImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);

        mClipPath = new Path();
        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);

        radii[0] = mTopLeft;
        radii[1] = mTopLeft;
        radii[2] = mTopRight;
        radii[3] = mTopRight;
        radii[4] = mBottomRight;
        radii[5] = mBottomRight;
        radii[6] = mBottomLeft;
        radii[7] = mBottomLeft;
    }

    /**
     * 初始化属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XImageView);
        mAsCircle = ta.getBoolean(R.styleable.XImageView_x_as_circle, mAsCircle);
        mRoundCorner = ta.getDimensionPixelSize(R.styleable.XImageView_x_round_corner, mRoundCorner);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.XImageView_x_stroke_width, mStrokeWidth);
        mStrokeColor = ta.getColor(R.styleable.XImageView_x_stroke_color, mStrokeColor);

        mTopLeft = ta.getDimensionPixelSize(R.styleable.XImageView_x_round_top_left, mRoundCorner);
        mTopRight = ta.getDimensionPixelSize(R.styleable.XImageView_x_round_top_right, mRoundCorner);
        mBottomLeft = ta.getDimensionPixelSize(R.styleable.XImageView_x_round_bottom_left, mRoundCorner);
        mBottomRight = ta.getDimensionPixelSize(R.styleable.XImageView_x_round_bottom_right, mRoundCorner);
        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLayer = new RectF(0, 0, w, h);
        mClipPath.reset();
        if (mAsCircle) {
            //在支持圆形的时候有一个坑需要注意一下，就是控件长宽比不一致的情况下，
            //由于是按照最短的边计算的，所以在长宽比不一致的情况下，直接向 Path 添加圆形，
            //Path 是无法填充满画布的，在绘制的时候可能会出现圆形之外依旧有内容被绘制出来，
            //所以这里使用了两个 moveTo 操作来让 Path 填充满画布。
            float d = Math.min(w, h);
            float r = d / 2;
            PointF centerPoint = new PointF(w / 2, h / 2);
            mClipPath.addCircle(centerPoint.x, centerPoint.y, r, Path.Direction.CW);
            mClipPath.moveTo(-10, -10);
            mClipPath.moveTo(w + 10, h + 10);
        } else {
            mClipPath.addRoundRect(mLayer, radii, Path.Direction.CW);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //新建图层，以下所有canvas操作都在新图层上，防止Xfermode模式下原图层的污染
        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        if (mStrokeWidth > 0) {
            mSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mSrcPaint.setStrokeWidth(mStrokeWidth * 2);
            mSrcPaint.setColor(mStrokeColor);
            mSrcPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mSrcPaint);
        }
        //取交集，显示dst层
        mSrcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mSrcPaint.setColor(Color.WHITE);
        mSrcPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mClipPath, mSrcPaint);
        canvas.restore();
    }
}
