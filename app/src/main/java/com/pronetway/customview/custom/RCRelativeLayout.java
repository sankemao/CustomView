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
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.pronetway.customview.R;

/**
 * Description: 圆角或者圆形的RelativeLayout
 * Create Time: 2018/1/25.16:32
 * Author:jin
 * Email:210980059@qq.com
 */
public class RCRelativeLayout extends RelativeLayout {

    //是否为圆形
    private boolean mAsCircle;
    //描边颜色
    private int mStrokeColor = Color.WHITE;
    //描边半径
    private int mStrokeWidth;

    private int mRoundCorner;
    private int mTopLeft;
    private int mTopRight;
    private int mBottomLeft;
    private int mBottomRight;

    //圆角path规则
    private float radii[] = new float[8]; //top-left, top-right, bottom-right, bottom-left
    //去除padding了
    private Path mClipPath;
    //响应事件区域，去除padding了
    private Region mAreaRegion;
    private Paint mPaint;
    //布局图层
    private RectF mLayer;
    //边缘修复
    private int mEdgeFix = 10;

    public RCRelativeLayout(Context context) {
        this(context, null);
    }

    public RCRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RCRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);

        radii[0] = mTopLeft;
        radii[1] = mTopLeft;
        radii[2] = mTopRight;
        radii[3] = mTopRight;
        radii[4] = mBottomRight;
        radii[5] = mBottomRight;
        radii[6] = mBottomLeft;
        radii[7] = mBottomLeft;

        mClipPath = new Path();
        mAreaRegion = new Region();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mPaint.setColor(Color.WHITE);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RCRelativeLayout);
        mAsCircle = ta.getBoolean(R.styleable.RCRelativeLayout_round_as_circle, mAsCircle);
        mStrokeColor = ta.getColor(R.styleable.RCRelativeLayout_stroke_color, mStrokeColor);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.RCRelativeLayout_stroke_width, mStrokeWidth);

        mRoundCorner = ta.getDimensionPixelSize(R.styleable.RCRelativeLayout_round_corner, mRoundCorner);
        mTopLeft = ta.getDimensionPixelSize(R.styleable.RCRelativeLayout_round_top_left, mRoundCorner);
        mTopRight = ta.getDimensionPixelSize(R.styleable.RCRelativeLayout_round_top_left, mRoundCorner);
        mBottomLeft = ta.getDimensionPixelSize(R.styleable.RCRelativeLayout_round_top_left, mRoundCorner);
        mBottomRight = ta.getDimensionPixelSize(R.styleable.RCRelativeLayout_round_top_left, mRoundCorner);

        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLayer = new RectF(0, 0, w, h);
        RectF areas = new RectF();
        areas.left = getPaddingLeft();
        areas.top = getPaddingTop();
        areas.right = w - getPaddingRight();
        areas.bottom = h - getPaddingBottom();

        //make path empty
        mClipPath.reset();
        if (mAsCircle) {
            //在支持圆形的时候有一个坑需要注意一下，就是控件长宽比不一致的情况下，
            //由于是按照最短的边计算的，所以在长宽比不一致的情况下，直接向 Path 添加圆形，
            //Path 是无法填充满画布的，在绘制的时候可能会出现圆形之外依旧有内容被绘制出来，
            //所以这里使用了两个 moveTo 操作来让 Path 填充满画布。
            float d = Math.min(areas.width(), areas.height());
            float r = d / 2;
            PointF centerPonit = new PointF(w / 2, h / 2);
            mClipPath.addCircle(centerPonit.x, centerPonit.y, r, Path.Direction.CW);
            mClipPath.moveTo(-mEdgeFix, -mEdgeFix);
            mClipPath.moveTo(w + mEdgeFix, h + mEdgeFix);
        } else {
            //圆角布局
            mClipPath.addRoundRect(areas, radii, Path.Direction.CW);//除去padding的实际区域， 圆角设定， 方向
        }

        Region clip = new Region((int)areas.left, (int)areas.top, (int)areas.right, (int)areas.bottom);
        mAreaRegion.setPath(mClipPath, clip);//将path和clip的两个区域取交集 即裁剪区域以及去除padding区域的交集。
    }

    /**
     * canvas的save和saveLayer方法参考
     * http://blog.csdn.net/chen930724/article/details/50176661
     *
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        //canvas.save();//保存当前的matrix和clip到私有的栈中（Skia内部实现）。任何matrix变换和clip操作都会在调用restore的时候还原。不会生成新图层
        //生成一个独立layer入栈，后续drawxxx都发生在这个layer上
        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        if (mStrokeWidth > 0) {
            //支持半透明描边， 将与描边区域重叠的内容裁减掉
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(mStrokeWidth * 2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
            //绘制描边
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setColor(mStrokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
        }

        //图层取交集，显示dst部分， 即原本layout中的内容和path取交集后显示原有内容
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        //path部分画到画布上
        canvas.drawPath(mClipPath, mPaint);
//        layer退栈，退栈时，将本层绘制的图像“绘制”到上层或是canvas上
        canvas.restore();
    }

    //解决设置background也能设置圆角的问题
    //super.draw(canvas);方法中会绘制背景，自身以及child
    @Override
    public void draw(Canvas canvas) {
        //保存画布原始状态
        canvas.save();
        canvas.clipPath(mClipPath);
        super.draw(canvas);
        canvas.restore();
    }

    //解决圆角以外触摸问题
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mAreaRegion.contains((int) event.getX(), (int) event.getY())) {
            //不再region以内的直接不处理。
            return false;
        }
        return super.onTouchEvent(event);
    }
}
