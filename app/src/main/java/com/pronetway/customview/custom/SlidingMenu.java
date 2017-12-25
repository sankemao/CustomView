package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.pronetway.customview.R;

/**
 * Description:TODO
 * Create Time: 2017/11/1.14:23
 * Author:jin
 * Email:210980059@qq.com
 */
public class SlidingMenu extends HorizontalScrollView {
    private boolean mMenuIsOpen = false;

    private int mMenuWidth;
    private View mMenuView;
    private View mContentView;
    private GestureDetector mGestureDetector;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new GestureDetectorListener());
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
        float rightMargin = a.getDimension(R.styleable.SlidingMenu_menuRightMargin, ConvertUtils.dp2px(50));
        mMenuWidth = (int) (ScreenUtils.getScreenWidth() - rightMargin);
        a.recycle();
    }

    private class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //竖直方向比水平方向速度快, 不处理.
            if (Math.abs(velocityY) > Math.abs(velocityX)) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            if (mMenuIsOpen) {
                if (velocityX < 0) {
                    toggleMenu();
                    return true;
                }
            } else {
                if (velocityX > 0) {
                    toggleMenu();
                    return true;
                }
            }
            return false;
        }
    }

    private void toggleMenu() {
        if (mMenuIsOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //当解析完布局后
        ViewGroup containerView = (ViewGroup) getChildAt(0);
        mMenuView = containerView.getChildAt(0);
        mContentView = containerView.getChildAt(1);
        mMenuView.getLayoutParams().width = mMenuWidth;
        mContentView.getLayoutParams().width = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        scrollTo(mMenuWidth, 0);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        LogUtils.d("onScrollChanged" + l);
        //目录关闭状态 -> 打开状态
        //1 - 0
        float scale = l * 1f / mMenuWidth;
        //1 - 0.7
        float rightScale = 0.7f + 0.3f * scale;
        ViewCompat.setPivotX(mContentView, 0);
        ViewCompat.setPivotY(mContentView, getMeasuredHeight() / 2);
        ViewCompat.setScaleX(mContentView, rightScale);
        ViewCompat.setScaleY(mContentView, rightScale);

        ViewCompat.setTranslationX(mMenuView, l * 0.7f);
        float leftScale = 0.7f + 0.3f * (1 - scale);
        ViewCompat.setScaleX(mMenuView, leftScale);
        ViewCompat.setScaleY(mMenuView, leftScale);

        float leftAlpha = 0.6f + 0.4f * (1 - scale);
        ViewCompat.setAlpha(mMenuView, leftAlpha);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (mGestureDetector.onTouchEvent(ev)) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                int currentX = getScrollX();
                if (currentX < mMenuWidth / 2) {
                    openMenu();
                } else {
                    closeMenu();
                }
                return false;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 关闭目录
     */
    private void closeMenu() {
        smoothScrollTo(mMenuWidth, 0);
        mMenuIsOpen = false;
    }

    /**
     * 打开目录
     */
    private void openMenu() {
        smoothScrollTo(0, 0);
        mMenuIsOpen = true;
    }
}
