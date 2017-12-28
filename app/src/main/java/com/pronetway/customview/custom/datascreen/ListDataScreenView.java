package com.pronetway.customview.custom.datascreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;

/**
 * Description:TODO
 * Create Time:2017/12/24.23:03
 * Author:jin
 * Email:210980059@qq.com
 */
public class ListDataScreenView extends LinearLayout implements View.OnClickListener {
    //头部tab.
    private LinearLayout mMenuTabView;
    private Context mContext;
    //阴影+布局内容.
    private FrameLayout mMenuMiddleView;
    private View mShadowView;
    private int mShadowColor = 0x88888888;
    //菜单内容的容器.
    private FrameLayout mMenuContainerView;

    //筛选菜单的adapter.
    private BaseMenuAdapter mAdapter;
    private int mMenuContainerHeight;

    //当前打开的tab的position
    private int mCurrentPosition = -1;
    private long DURATION_TIME = 350;

    //动画是否执行
    private boolean mAnimatorExecute;

    public ListDataScreenView(Context context) {
        this(context, null);
    }

    public ListDataScreenView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListDataScreenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtils.d("onMeasure");

        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mMenuContainerHeight == 0 && height > 0) {
            //内容高度为总高度的3/4.
            mMenuContainerHeight = (int) (height * 75f / 100);
            ViewGroup.LayoutParams params = mMenuContainerView.getLayoutParams();
            params.height = mMenuContainerHeight;
            mMenuContainerView.setLayoutParams(params);

            //先隐藏内容.
            ViewCompat.setTranslationY(mMenuContainerView, -mMenuContainerHeight);
        }
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//    }

    /**
     * 实例化好布局
     */
    private void initLayout() {
        setOrientation(VERTICAL);
        //创建头部存放tab
        mMenuTabView = new LinearLayout(mContext);
        mMenuTabView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mMenuTabView);

        //创建FrameLayout用来存放 = 阴影 + 内容布局(FramLayout).
        mMenuMiddleView = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        mMenuMiddleView.setLayoutParams(params);
        addView(mMenuMiddleView);

        //创建阴影, 可以不用设置LayoutParams, 默认就是match_parent.
        mShadowView = new View(mContext);
        mShadowView.setBackgroundColor(mShadowColor);
        mShadowView.setAlpha(0f);
        mShadowView.setVisibility(GONE);
        mShadowView.setOnClickListener(this);
        mMenuMiddleView.addView(mShadowView);

        //创建菜单 存放菜单内容
        mMenuContainerView = new FrameLayout(mContext);
        mMenuContainerView.setBackgroundColor(Color.RED);
        mMenuMiddleView.addView(mMenuContainerView);
        mMenuContainerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 设置adapter
     */
    public void setAdapter(BaseMenuAdapter adapter) {
        if (mAdapter != null && mAdapterMenuObserver != null) {
            mAdapter.unregisterDataObserver(mAdapterMenuObserver);
        }
        this.mAdapter = adapter;
        //注册观察者 具体的观察者实例对象
        mAdapterMenuObserver = new AdapterMenuObserver();
        mAdapter.registerDataObserver(mAdapterMenuObserver);
        //获取用多少条
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            //获取tab.
            View tabView = mAdapter.getTabView(i, mMenuTabView);
            mMenuTabView.addView(tabView);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) tabView.getLayoutParams();
            layoutParams.weight = 1;
            tabView.setLayoutParams(layoutParams);
            //设置tab点击事件
            setTabClick(tabView, i);

            //获取菜单的内容
            View menuView = mAdapter.getMenuView(i, mMenuContainerView);
            menuView.setVisibility(GONE);
            mMenuContainerView.addView(menuView);
        }
    }

    private AdapterMenuObserver mAdapterMenuObserver;

    private class AdapterMenuObserver extends MenuObserver {

        @Override
        public void observerCloseMenu() {
            closeMenu(mMenuTabView.getChildAt(mCurrentPosition));
        }
    }

    /**
     * 设置tab的点击事件
     */
    private void setTabClick(final View tabView, final int position) {
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPosition == -1) {
                    //没打开
                    openMenu(position, tabView);
                } else {
                    if (mCurrentPosition == position) {
                        //打开了
                        closeMenu(tabView);
                    } else {
                        //切换menu
                        View currentMenu = mMenuContainerView.getChildAt(mCurrentPosition);
                        currentMenu.setVisibility(GONE);
                        mAdapter.menuClose(mMenuTabView.getChildAt(mCurrentPosition));

                        mCurrentPosition = position;
                        currentMenu = mMenuContainerView.getChildAt(mCurrentPosition);
                        currentMenu.setVisibility(VISIBLE);
                        mAdapter.menuOpen(mMenuTabView.getChildAt(mCurrentPosition));
                    }
                }
            }
        });
    }

    private void openMenu(final int position, final View tabView) {
        if (mAnimatorExecute) {
            return;
        }
        mShadowView.setVisibility(VISIBLE);

        View menuView = mMenuContainerView.getChildAt(position);
        menuView.setVisibility(VISIBLE);

        //打开开启动画
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mMenuContainerView, "translationY", -mMenuContainerHeight, 0);
        translationAnimator.setDuration(DURATION_TIME);
        translationAnimator.start();

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 0f, 1f);
        alphaAnimator.setDuration(DURATION_TIME);

        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorExecute = false;
                mCurrentPosition = position;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimatorExecute = true;
                mAdapter.menuOpen(tabView);
            }
        });
        alphaAnimator.start();
    }

    private void closeMenu(final View tabView) {
        if (mAnimatorExecute) {
            return;
        }
        ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mMenuContainerView, "translationY", 0, -mMenuContainerHeight);
        translationAnimator.setDuration(DURATION_TIME);
        translationAnimator.start();

        mShadowView.setVisibility(VISIBLE);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 1f, 0f);
        alphaAnimator.setDuration(DURATION_TIME);
        //等动画完全执行完隐藏
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorExecute = false;
                mShadowView.setVisibility(GONE);
                View menuView = mMenuContainerView.getChildAt(mCurrentPosition);
                menuView.setVisibility(GONE);
                mCurrentPosition = -1;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimatorExecute = true;

                //当前的tab传到adapter中
                mAdapter.menuClose(tabView);
            }
        });
        alphaAnimator.start();
    }

    /**
     * 阴影点击事件
     */
    @Override
    public void onClick(View v) {
        closeMenu(mMenuTabView.getChildAt(mCurrentPosition));
    }
}
