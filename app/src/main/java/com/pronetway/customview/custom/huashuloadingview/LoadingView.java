package com.pronetway.customview.custom.huashuloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Description: 仿花束直播loading动画, 内存回收未完成.
 * Create Time: 2017/11/30.10:57
 * Author:jin
 * Email:210980059@qq.com
 */
public class LoadingView extends RelativeLayout {
    private CircleView mLeft, mMiddle, mRight;
    private int mTranslationDistance;
    private final long ANIMATION_TIME = 350;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * layoutInflate中执行
     */
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTranslationDistance = dp2px(30);

        setBackgroundColor(Color.WHITE);
        mLeft = getCircleView(context);
        mLeft.exChangeColor(Color.BLUE);

        mMiddle = getCircleView(context);
        mMiddle.exChangeColor(Color.RED);

        mRight = getCircleView(context);
        mRight.exChangeColor(Color.GREEN);

        addView(mLeft);
        addView(mRight);
        //中间的加到最上面
        addView(mMiddle);

        post(new Runnable() {
            @Override
            public void run() {
                //布局实例化好之后再去开启动画.
                expendAnimation();
            }
        });
    }

    /**
     * 展开动画
     */
    private void expendAnimation() {
        //两边跑
        ObjectAnimator leftTranslationAnimator = ObjectAnimator.ofFloat(mLeft, "translationX", 0, -mTranslationDistance);
        ObjectAnimator rightTranslationAnimator = ObjectAnimator.ofFloat(mRight, "translationX", 0, mTranslationDistance);

        AnimatorSet animatorSet = new AnimatorSet();
        //弹性效果, 刚开始快, 越来越慢
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(ANIMATION_TIME);
        animatorSet.playTogether(leftTranslationAnimator, rightTranslationAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                innerAnimation();
            }
        });
        animatorSet.start();
    }

    /**
     * 聚拢
     */
    private void innerAnimation() {
        ObjectAnimator leftTranslationAnimator = ObjectAnimator.ofFloat(mLeft, "translationX", -mTranslationDistance, 0);
        ObjectAnimator rightTranslationAnimator = ObjectAnimator.ofFloat(mRight, "translationX", mTranslationDistance, 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.setDuration(ANIMATION_TIME);
        animatorSet.playTogether(leftTranslationAnimator, rightTranslationAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int leftColor = mLeft.getColor();
                int rightColor = mRight.getColor();
                int middleColor = mMiddle.getColor();

                mMiddle.exChangeColor(leftColor);
                mRight.exChangeColor(middleColor);
                mLeft.exChangeColor(rightColor);

                expendAnimation();
            }
        });
        animatorSet.start();
    }

    public CircleView getCircleView(Context context) {
        CircleView circleView = new CircleView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp2px(8), dp2px(8));
        params.addRule(CENTER_IN_PARENT);

        circleView.setLayoutParams(params);
        addView(circleView);
        return circleView;
    }

    private int dp2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(View.GONE);

    }
}
