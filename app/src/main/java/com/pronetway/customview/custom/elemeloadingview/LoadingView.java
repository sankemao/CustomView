package com.pronetway.customview.custom.elemeloadingview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.pronetway.customview.R;


/**
 * Description: 饿了么加载数据动画
 * Create Time:2017/12/31.16:48
 * Author:jin
 * Email:210980059@qq.com
 */
public class LoadingView extends LinearLayout {

    private View mShadowView;
    private ShapeView mShapeView;
    private int mShapeViewDistance;
    private final long ANIMATOR_DURATION = 350;
    private boolean isStopAnimator = false;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initLayout();
    }

    /**
     * 初始化加载布局
     */
    private void initLayout() {
        mShapeViewDistance = dp2px(80);

        inflate(getContext(), R.layout.eleme_ui_loading_view, this);
        mShadowView = findViewById(R.id.shadow_view);
        mShapeView = (ShapeView) findViewById(R.id.shape_view);

        post(new Runnable() {
            @Override
            public void run() {
                startFailAnimator();
            }
        });
    }

    private int dp2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    /**
     * 开始下落
     */
    private void startFailAnimator() {
        if (isStopAnimator) {
            return;
        }
        ObjectAnimator shapeViewAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", 0, mShapeViewDistance);
        ObjectAnimator shadowViewAnimator = ObjectAnimator.ofFloat(mShadowView, "scaleX", 1, 0.3f);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATOR_DURATION);
        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(shadowViewAnimator, shapeViewAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //改变形状
                mShapeView.exChange();
                //下落玩上抛, 监听动画执行完毕
                startUpAnimator();
            }
        });
        set.start();
    }

    /**
     * 上抛动画
     */
    private void startUpAnimator() {
        if (isStopAnimator) {
            return;
        }
        ObjectAnimator shapeViewAnimator = ObjectAnimator.ofFloat(mShapeView, "translationY", mShapeViewDistance, 0);
        ObjectAnimator shadowViewAnimator = ObjectAnimator.ofFloat(mShadowView, "scaleX", 0.3f, 1f);
        ObjectAnimator rotationAnimator = getRotationAnimator();

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATOR_DURATION);
        set.playTogether(shadowViewAnimator, shapeViewAnimator, rotationAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //下落玩上抛, 监听动画执行完毕
                startFailAnimator();
            }
        });
        set.start();
    }

    /**
     * shapeView的旋转动画
     */
    private ObjectAnimator getRotationAnimator() {
        ObjectAnimator rotationAnimator = null;
        switch (mShapeView.getCurrentShape()) {
            case Circle:
            case Square:
                rotationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, 180);
                break;
            case Triangle:
                rotationAnimator = ObjectAnimator.ofFloat(mShapeView, "rotation", 0, -120);
                break;
            default:
                break;
        }
        rotationAnimator.setDuration(ANIMATOR_DURATION);
        rotationAnimator.setInterpolator(new DecelerateInterpolator());
        return rotationAnimator;
    }

    /**
     * 性能优化
     */
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(View.GONE);//不会再走测量摆放等方法
        //清除动画
        mShadowView.clearAnimation();
        mShapeView.clearAnimation();

        //从父布局移除
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);//从父布局移除
            removeAllViews();//移除自己所有的View.
        }

        isStopAnimator = true;
    }
}
