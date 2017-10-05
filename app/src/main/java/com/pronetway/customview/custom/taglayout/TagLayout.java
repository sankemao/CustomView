package com.pronetway.customview.custom.taglayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:流式布局
 * Create Time:2017/10/4.19:20
 * Author:jin
 * Email:210980059@qq.com
 */
public class TagLayout extends ViewGroup {
    private List<List<View>> mChildViews = new ArrayList<>();

    private TagBaseAdapter mAdapter;

    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(@NonNull TagBaseAdapter adapter) {
        //清空所有的子View.
        removeAllViews();

        mAdapter = adapter;

        //获取数量
        int childCount = mAdapter.getCount();
        for (int i = 0; i < childCount; i++) {
            View childView = mAdapter.getView(i, this);
            addView(childView);
        }
    }

    //1. 指定宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //清空集合, onMeasure会走两次.
        mChildViews.clear();

        int childCount = getChildCount();

        //获取自身的宽度.
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //高度需要计算.
        int height = getPaddingTop() + getPaddingBottom();
        //一行的宽度.
        int lineWidth = getPaddingLeft();

        ArrayList<View> childViews = new ArrayList<>();
        mChildViews.add(childViews);

        //子view高度不一致的情况下.
        int maxHeight = 0;

        for (int i = 0; i < childCount; i++) {
            //1.1 for循环测量子View.
            View childView = getChildAt(i);
            //这句话执行了后, 就可以获取view的宽高了.
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();

            LogUtils.d("宽度: " + childView.getMeasuredWidth() + " 总宽度: " + lineWidth);
            //一行不够的情况下换行
            if (lineWidth + (childView.getMeasuredWidth() + params.leftMargin + params.rightMargin) > width) {
                //换行, 累加上一行的最大高度.
                height += maxHeight;
                //换行后第一个子view的宽度.
                lineWidth = childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;

                childViews = new ArrayList<>();
                mChildViews.add(childViews);
            } else {
                lineWidth += childView.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            }
            maxHeight = Math.max(childView.getMeasuredHeight() + params.topMargin + params.bottomMargin, maxHeight);
            childViews.add(childView);
        }

        //最后一行的高度
        height += maxHeight;

        //1.2 根据子View测量和计算自己的布局.
        setMeasuredDimension(width, height);
    }

    /**
     * 2. 摆放子View.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left, top = getPaddingTop();
        for (List<View> childViews: mChildViews) {
            left = getPaddingLeft();
            int maxHeight = 0;
            for (View childView : childViews) {
                MarginLayoutParams params = (MarginLayoutParams) childView.getLayoutParams();
                left += params.leftMargin;
                int childTop = top + params.topMargin;
                childView.layout(left, childTop, left + childView.getMeasuredWidth(), childTop + childView.getMeasuredHeight());
                left += childView.getMeasuredWidth();
                //计算每一行的最大高度.
                int childHeight = childView.getMeasuredHeight() + params.topMargin + params.bottomMargin;
                maxHeight = Math.max(childHeight, maxHeight);
            }

            top += maxHeight;
        }
    }

    /**
     * viewGroup的layoutParams没有margin值
     * 重写该方法.
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
