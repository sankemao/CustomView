package com.pronetway.customview.custom;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.pronetway.customview.R;

/**
 * Created by jin on 2017/9/8.
 *
 */
public class AliLoadingView extends View {

    private int mLoadingColor = Color.BLUE;
    private float mLoadingWidth;
    private Paint mPaint;
    private Status mStatus = Status.LOADING;

    private int mStartAngle = 0;        //起始的角度
    private int mTempAngle = 0;         //上一次的起始角度
    private int mSweepAngle = 0;        //扫过的角度
    private int mCurAngle = 0;          //当前角度
    private Path mPathCircle;
    private PathMeasure mPathMeasure;   //path所绘制的点的坐标计算器.
    private Path mSuccessPath;
    private Path mPathCircleDst;
    private ValueAnimator mCircleAnimator;
    private float mCircleValue;
    private ValueAnimator mSuccessAnimator;
    private float mSuccessValue;


    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public void startCircleAnim() {
        mStatus = Status.SUCCESS;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(mSuccessAnimator).after(mCircleAnimator);
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    public AliLoadingView(Context context) {
        this(context, null);
    }

    public AliLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AliLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);
        initPath();
        initAnim();
    }

    private void initAnim() {
        mCircleAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        mCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircleValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mSuccessAnimator = ValueAnimator.ofFloat(0f, 1.0f);
        mSuccessAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSuccessValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    private void initPath() {
        //画圆的Path
        mPathCircle = new Path();
        //追踪Path的坐标
        mPathMeasure = new PathMeasure();
        //截取PathMeasure中的path
        mPathCircleDst = new Path();


        mSuccessPath = new Path();
    }

    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AliLoadingView);
        mLoadingColor = a.getColor(R.styleable.AliLoadingView_loadingColor, mLoadingColor);
        mLoadingWidth = a.getDimension(R.styleable.AliLoadingView_loadingWidth, dp2px(10));
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mLoadingWidth);
        mPaint.setColor(mLoadingColor);
    }

    private float dp2px(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(Math.min(width, height), Math.min(width, height));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将padding考虑进去, 直接画布平移.
        canvas.translate(getPaddingLeft(), getPaddingTop());
        switch (mStatus) {
            case LOADING:
                drawLoading(canvas);
                break;
            case SUCCESS:
                drawSuccess(canvas);
                break;
            case FAILURE:
                drawFailure(canvas);
                break;
            default:
                break;
        }
    }

    /**
     * 失败
     * @param canvas
     */
    private void drawFailure(Canvas canvas) {

    }

    /**
     * 成功
     * @param canvas
     */
    private void drawSuccess(Canvas canvas) {
        if (mPathCircle.isEmpty()) {
            mPathCircleDst.reset();
            mPathCircleDst.lineTo(0,0);

            mPathCircle.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - mLoadingWidth / 2, Path.Direction.CW);
            mPathCircle.moveTo(getWidth() / 8 * 3, getWidth() / 2);
            mPathCircle.lineTo(getWidth() / 2, getWidth() / 5 * 3);
            mPathCircle.lineTo(getWidth() / 3 * 2, getWidth() / 5 * 2);

            mPathMeasure.setPath(mPathCircle, false);
        }

        //画圆
        if (mCircleValue < 1) {
            //将追踪的path添加给mPathCircleDst,(注意这里是添加, 而不是替换)
            mPathMeasure.getSegment(0, mCircleValue * mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, mPaint);
        } else {
            //画勾
            if (mCircleValue == 1) {
                mPathMeasure.nextContour();
                mCircleValue += 1;
            }
            mPathMeasure.getSegment(0, mSuccessValue * mPathMeasure.getLength(), mPathCircleDst, true);
            canvas.drawPath(mPathCircleDst, mPaint);
        }
    }

    /***
     * 绘制进度
     * @param canvas
     */
    private void drawLoading(Canvas canvas) {
        //首先扫过角度不断增加.
        if (mStartAngle == mTempAngle) {
            mSweepAngle += 6;
        }
        //一直到扫过的角度大于300
        if (mSweepAngle >= 300 || mStartAngle > mTempAngle) {
            mStartAngle += 6; //起始角度不断增加.(当第一次执行这行后, mStartAngle > mTempAngle则符合条件)
            if (mSweepAngle > 20) {
                mSweepAngle -= 6;//起始角度继续增加, 扫过角度不断减少, 直到扫过角度小于20跳出来, 走下面判断.
            }
        }

        //如果这时起始角度大于上一次记录的起始角度 300°,
        if (mStartAngle > mTempAngle + 300) {
            mStartAngle %= 360;//降低起始角度的维度.
            mTempAngle = mStartAngle; //为了进入第一个判断条件
            mSweepAngle = 20;//扫过角度变20.
        }

       LogUtils.d(mStartAngle + "    " + mSweepAngle + "   " + mTempAngle);

        canvas.rotate(mCurAngle += 4, getWidth() / 2, getHeight() / 2);
        RectF rectF = new RectF(mLoadingWidth / 2, mLoadingWidth / 2, getWidth() - mLoadingWidth / 2, getHeight() - mLoadingWidth / 2);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mPaint);
        invalidate();
    }

    public enum Status {
        LOADING,
        SUCCESS,
        FAILURE
    }

}
