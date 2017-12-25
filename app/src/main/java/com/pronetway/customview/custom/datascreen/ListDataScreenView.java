package com.pronetway.customview.custom.datascreen;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;

/**
 * Description:TODO
 * Create Time:2017/12/24.23:03
 * Author:jin
 * Email:210980059@qq.com
 */
public class ListDataScreenView extends LinearLayout{
    //头部tab.
    private LinearLayout mMenuTabView;
    private Context mContext;
    //阴影+布局内容.
    private FrameLayout mMenuMiddleView;
    private View mShadowView;
    private int mShadowColor = Color.parseColor("#2b2b2b");
    //菜单内容.
    private FrameLayout mMenuContainerView;

    //筛选菜单的adapter.
    private BaseMenuAdapter mAdapter;
    private int mMenuContainerHeight;

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
        mMenuContainerHeight = (int) (height * 75f / 100);
        ViewGroup.LayoutParams params = mMenuContainerView.getLayoutParams();
        params.height = mMenuContainerHeight;
        mMenuContainerView.setLayoutParams(params);

//        mMenuContainerView.setTranslationY(-mMenuContainerHeight);
        ViewCompat.setTranslationY(mMenuContainerView, -mMenuContainerHeight);
    }

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
        mMenuMiddleView.addView(mShadowView);

        //创建菜单 存放菜单内容
        mMenuContainerView = new FrameLayout(mContext);
        mMenuContainerView.setBackgroundColor(Color.WHITE);
        mMenuMiddleView.addView(mMenuContainerView);
    }

    /**
     * 设置adapter
     */
    public void setAdapter(BaseMenuAdapter adapter) {
        this.mAdapter = adapter;

        //获取用多少条
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            //获取tab.
            View tabView = mAdapter.getTabView(i, mMenuTabView);
            mMenuTabView.addView(tabView);
            LinearLayout.LayoutParams layoutParams = (LayoutParams) tabView.getLayoutParams();
            layoutParams.weight = 1;
            tabView.setLayoutParams(layoutParams);
            //获取菜单的内容
            View menuView = mAdapter.getMenuView(i, mMenuContainerView);
            menuView.setVisibility(GONE);
            mMenuContainerView.addView(menuView);
        }
    }

    //内容和阴影进来的时候不显示.
}
