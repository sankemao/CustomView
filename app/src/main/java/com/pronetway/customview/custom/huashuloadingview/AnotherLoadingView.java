package com.pronetway.customview.custom.huashuloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Description:TODO
 * Create Time: 2018/1/24.17:17
 * Author:jin
 * Email:210980059@qq.com
 */
public class AnotherLoadingView extends RelativeLayout{
    
    private CircleView mLeftCircle, mMiddleCircle, mRightCircle;
    //向圆心运动距离
    private int mTranslationDistance;
    //每次动画执行时间
    private final long ANIMATION_TIME = 1000;

    public AnotherLoadingView(Context context) {
        this(context, null);
    }

    public AnotherLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnotherLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTranslationDistance = dp2px(15);

        mLeftCircle = getCircle(context, RelativeLayout.ALIGN_PARENT_LEFT);
        mLeftCircle.exChangeColor(Color.RED);
        mMiddleCircle = getCircle(context, RelativeLayout.CENTER_IN_PARENT);
        mMiddleCircle.exChangeColor(Color.BLUE);
        mRightCircle = getCircle(context, RelativeLayout.ALIGN_PARENT_RIGHT);
        mRightCircle.exChangeColor(Color.RED);

        post(new Runnable() {
            @Override
            public void run() {
                innerAnimation();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(width, height), Math.min(width, height));
    }

    private int dp2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    public CircleView getCircle(Context context, int layoutRule) {
        CircleView circleView = new CircleView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp2px(10), dp2px(10));
        params.addRule(layoutRule);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(circleView, params);
        return circleView;
    }


    /**
     * 执行动画
     */
    private void innerAnimation() {
        //收缩
        ObjectAnimator leftAnimator = ObjectAnimator.ofFloat(mLeftCircle, "translationX", 0, mTranslationDistance);
        ObjectAnimator rightAnimator = ObjectAnimator.ofFloat(mRightCircle, "translationX", 0, -mTranslationDistance);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 180f);
        rotationAnimator.setDuration(900);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.setDuration(ANIMATION_TIME);
        animatorSet.playTogether(leftAnimator, rightAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                outterAnimation();
            }
        });
        animatorSet.start();
        rotationAnimator.start();
    }

    //展开
    private void outterAnimation() {
        //收缩
        ObjectAnimator leftAnimator = ObjectAnimator.ofFloat(mLeftCircle, "translationX", mTranslationDistance, 0);
        ObjectAnimator rightAnimator = ObjectAnimator.ofFloat(mRightCircle, "translationX", -mTranslationDistance, 0);
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", 180f, 360f);
        rotationAnimator.setDuration(800);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.setDuration(800);
        animatorSet.playTogether(leftAnimator, rightAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                innerAnimation();
            }
        });
        animatorSet.start();
        rotationAnimator.start();
    }


}
