package com.pronetway.customview.custom.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Description:TODO
 * Create Time:2017/12/21.22:39
 * Author:jin
 * Email:210980059@qq.com
 */
public class TranslationBehavior extends FloatingActionButton.Behavior {

    public TranslationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //关注垂直滚动
    /**
     * 相当于开关, 返回true, 下面的方法才会调.
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    private boolean isOut = false;

    //正在滚动
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        int translationY = ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).bottomMargin + child.getMeasuredHeight();
        //向上的时候隐藏, 向下出来.
        if (dyConsumed > 0) {
            if (!isOut) {
                //向上滑动
                child.animate().translationY(translationY).setDuration(1000).start();
                isOut = true;
            }
        } else {
            if (isOut) {
                child.animate().translationY(0).setDuration(500).start();
                isOut = false;
            }
        }
    }

    //开始滚动
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }
}
