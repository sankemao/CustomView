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
    private float mInitDownX;
    private float mInitDownY;

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



        @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mInitDownX = ev.getX();
                mInitDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();
                if (Math.abs(x - mInitDownX) < 8 * Math.abs(y - mInitDownY)) {
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void toggleMenu() {
        if (mMenuIsOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * 写在布局中的view或viewGroup解析后会触发此方法，直接new出来的不会。
     * 在解析完成后，必然会调用addView方法，该操作就会触发requestLayout和invalidate。
     * performTraversalse performMeasure view.measure onMeasure
     * performLayout view.layout onLayout
     * performDraw draw drawSoftWare view.draw onDraw dispatchDraw drawChild
     *
     */
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

    /**
     * 一般发生在视图大小发生变化时调用
     * layout->setFrame->sizeChange->onSizeChanged->onLayout
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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
//        LogUtils.d("onScrollChanged" + l);
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

        //如果手势处理了，就return true, 代表消费了此事件，不再向下分发。
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
