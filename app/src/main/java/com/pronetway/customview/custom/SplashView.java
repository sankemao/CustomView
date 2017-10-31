package com.pronetway.customview.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;


/**
 * Description: 云点餐闪屏动画效果
 * Create Time: 2017/10/31.9:29
 * Author:jin
 * Email:210980059@qq.com
 */
public class SplashView extends View {

    private boolean isInitParams;
    private Paint mCirclePaint;

    private float R = 80f;
    private float mDiagonalDist;

    private SplashState mSplashState;
    private int mCenterX;
    private int mCenterY;
    private Path mLinePath;


    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInitParams) {
            initParams();
            isInitParams = true;
        }

        if (mSplashState == null) {
            mSplashState = new PathState();
        }
        mSplashState.draw(canvas);
    }


    /**
     * 初始化参数.
     */
    private void initParams() {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        mCenterX = width / 2;
        mCenterY = height / 2;

        //小球路径
        mLinePath = new Path();
        mLinePath.moveTo(R, (4f / 5f) * height);
        mLinePath.lineTo(R + (1f / 7f) * width, (6f / 7f) * height);
        mLinePath.lineTo(R + (1f / 4f) * width, (3f / 5f) * height);
        mLinePath.lineTo((3f / 8f) * width, (5f / 6f) * height);
        mLinePath.lineTo(mCenterX - R, mCenterY - R);

        //小球画笔
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);

        //小圆扩散半径
        mDiagonalDist = (float) Math.sqrt((mCenterX * mCenterX + mCenterY * mCenterY));
    }

    /**
     * 小球不同状态绘制
     */
    private abstract class SplashState {
        public abstract void draw(Canvas canvas);
    }

    /**
     * 小球折线运动状态
     */
    private class PathState extends SplashState {
        private PathMeasure mPathMeasure;
        private float mCurrentValue;
        private float[] mPos;
        private float[] mTan;

        public PathState() {
            //不闭合
            mPathMeasure = new PathMeasure(mLinePath, false);
            mPos = new float[2];
            mTan = new float[2];

            final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1400);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentValue = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mSplashState = new ExpandAlphaState();
                }
            });
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            //延迟0.3s执行动画
            SplashView.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    valueAnimator.start();
                }
            }, 300);
        }

        @Override
        public void draw(Canvas canvas) {
            mPathMeasure.getPosTan(mPathMeasure.getLength() * mCurrentValue, mPos, mTan);
            //坐标(x, y) 半径r, 画笔.
            canvas.drawCircle(mPos[0], mPos[1], R, mCirclePaint);
        }
    }

    private class ExpandAlphaState extends SplashState {
        /**
         * 小球透明度
         */
        private int mPaintAlpha = 255;
        /**
         * 小球扩大的半径
         */
        private float mExpandCircleR;

        private float alphaPoint = 8 * R;

        private boolean hasShowLogo;

        public ExpandAlphaState() {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(R, mDiagonalDist);
            valueAnimator.setDuration(1000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mExpandCircleR = (float) animation.getAnimatedValue();
                    if ((mExpandCircleR) >= alphaPoint) {
                        mPaintAlpha = (int) ((mDiagonalDist - mExpandCircleR) / (mDiagonalDist - alphaPoint) * 255);
                        if (!hasShowLogo && mCallBack != null) {
                            mCallBack.showLogo();
                            hasShowLogo = true;
                        }
                    }
                    invalidate();
                }
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    SplashView.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallBack != null) {
                                mCallBack.end();
                            }
                        }
                    }, 300);
                }
            });
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
        }

        @Override
        public void draw(Canvas canvas) {
            mCirclePaint.setAlpha(mPaintAlpha);
            canvas.drawCircle(mCenterX, mCenterY, mExpandCircleR, mCirclePaint);
        }
    }

    private CallBack mCallBack;

    public interface CallBack {
        /**
         * 展示logo的回调.
         */
        void showLogo();

        /**
         * 动画执行结束.
         */
        void end();
    }

    public void setLogoShowCallBack(CallBack showCallBack) {
        this.mCallBack = showCallBack;
    }

}
