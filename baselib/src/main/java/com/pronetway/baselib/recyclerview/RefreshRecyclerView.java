package com.pronetway.baselib.recyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pronetway.baselib.recyclerview.headfootview.RefreshViewCreator;

/**
 * Created by jin on 2017/5/10.
 */
public class RefreshRecyclerView extends WrapRecyclerView {
    // 下拉刷新的辅助类
    private RefreshViewCreator mRefreshCreator;
    // 下拉刷新头部的高度
    private int mRefreshViewHeight = 0;
    // 下拉刷新的头部View  --->  (RefreshViewCreator带来)
    private View mRefreshView;
    // 手指按下的Y位置
    private int mFingerDownY;
    // 手指拖拽的阻力指数
    protected float mDragIndex = 0.35f;
    // 当前是否正在拖动
    private boolean mCurrentDrag = false;
    // 当前的状态
    private int mCurrentRefreshStatus;
    // 默认状态
    public int REFRESH_STATUS_NORMAL = 0x0011;
    // 松开刷新状态
    public int REFRESH_STATUS_LOOSEN_REFRESHING = 0x0022;
    // 正在刷新状态
    public int REFRESH_STATUS_REFRESHING = 0x0033;
    // 下拉刷新状态
    public int REFRESH_STATUS_PULL_DOWN_REFRESH = 0x0044;

    public int getRefreashHeaderState() {
        return mCurrentRefreshStatus;
    }


    public RefreshRecyclerView(Context context) {
        super(context);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 先处理下拉刷新，同时考虑刷新列表的不同风格样式，确保这个项目还是下一个项目都能用
    // 所以我们不能直接添加View，需要利用辅助类
    public void addRefreshViewCreator(RefreshViewCreator refreshCreator) {
        this.mRefreshCreator = refreshCreator;
        addRefreshView();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        addRefreshView();
    }

    /**
     * 当列表到顶部, 并继续上拉时
     * 1. 改变refreshview的margin值.
     * 2. 改变刷新状态
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 如果是在最顶部才处理，否则不需要处理(如果可以向上滑动|正在刷新不处理)
                if (canScrollUp() || mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING) {
                    // 如果没有到达最顶端，也就是说还可以向上滚动就什么都不处理
                    return super.onTouchEvent(e);
                }

                // 解决下拉刷新自动滚动问题
                if (mCurrentDrag) {
                    scrollToPosition(0);
                }

                // 获取手指触摸拖拽的距离(乘上滑动系数)
                int distanceY = (int) ((e.getRawY() - mFingerDownY) * mDragIndex);
                // 如果是已经到达头部，并且不断的向下拉，那么不断的改变refreshView的marginTop的值
                if (distanceY > 0) {
                    int marginTop = distanceY - mRefreshViewHeight;
                    setRefreshViewMarginTop(marginTop);
                    updateRefreshStatus(marginTop);
                    mCurrentDrag = true;
                    return false;
                }
                break;
        }

        return super.onTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的位置 ,之所以写在dispatchTouchEvent那是因为如果我们处理了条目点击事件，
                // 那么就不会进入onTouchEvent里面，所以只能在这里获取
                mFingerDownY = (int) ev.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                if (mCurrentDrag) {
                    restoreRefreshView();//还原刷新头 -> 整个头部都可见.
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 复原刷新头部高度.
     */
    private void restoreRefreshView() {
        if (mRefreshView == null) {
            return;
        }
        int currentTopMargin = ((MarginLayoutParams) mRefreshView.getLayoutParams()).topMargin;
        int finalTopMargin = -mRefreshViewHeight + 1;
        if (mCurrentRefreshStatus == REFRESH_STATUS_LOOSEN_REFRESHING) {//如果之前处于margin>0状态.(松开以刷新状态)
            finalTopMargin = 0;
            mCurrentRefreshStatus = REFRESH_STATUS_REFRESHING;
            if (mRefreshCreator != null) {
                mRefreshCreator.onRefreshing();
            }
            if (mListener != null) {
                mListener.onRefresh();//刷新事件回调
            }

        }

        int distance = currentTopMargin - finalTopMargin;

        // 回弹到指定位置
        ValueAnimator animator = ObjectAnimator.ofFloat(currentTopMargin, finalTopMargin).setDuration(distance < 0 ? 0 : distance);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentTopMargin = (float) animation.getAnimatedValue();
                setRefreshViewMarginTop((int) currentTopMargin);
            }
        });
        animator.start();
        mCurrentDrag = false;
    }

    /**
     * fixme: 重新 开启动画.
     */
//    @Override
//    public void onScrollStateChanged(int state) {
//        super.onScrollStateChanged(state);
//        if (mCurrentRefreshStatus == REFRESH_STATUS_REFRESHING &&
//                this.getLayoutManager() instanceof LinearLayoutManager &&
//                ((LinearLayoutManager) this.getLayoutManager()).findFirstVisibleItemPosition() == 0) {
//            mRefreshCreator.onRefreshing();
//        }
//    }

    /**
     * 更新刷新的状态
     * 完成根据margin值来判断状态的.
     */
    private void updateRefreshStatus(int marginTop) {
        if (marginTop <= -mRefreshViewHeight) {//此时margintop小于负的头布局view的高度, 及刷新的头布局刚好看不见.
            mCurrentRefreshStatus = REFRESH_STATUS_NORMAL; //默认状态
        } else if (marginTop < 0) {//此时,头布局部分
            mCurrentRefreshStatus = REFRESH_STATUS_PULL_DOWN_REFRESH;//下拉刷新状态
        } else {//头布局不仅全部可见, margin部分也可见
            mCurrentRefreshStatus = REFRESH_STATUS_LOOSEN_REFRESHING;//松开刷新状态
        }

        if (mRefreshCreator != null) {
            mRefreshCreator.onPull(marginTop, mRefreshViewHeight, mCurrentRefreshStatus);
        }
    }

    /**
     * 添加头部的刷新View
     */
    private void addRefreshView() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null && mRefreshCreator != null) {
            // 添加头部的刷新View
            View refreshView = mRefreshCreator.getRefreshView(getContext(), this);
            if (refreshView != null) {
                addHeaderView(refreshView);//添加头布局
                this.mRefreshView = refreshView;
            }
        }
    }

    /**
     * 摆放位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            if (mRefreshView != null && mRefreshViewHeight <= 0) {
                // 获取头部刷新View的高度
                mRefreshViewHeight = mRefreshView.getMeasuredHeight();
                if (mRefreshViewHeight > 0) {
                    // 隐藏头部刷新的View  marginTop  多留出1px防止无法判断是不是滚动到头部问题
                    setRefreshViewMarginTop(-mRefreshViewHeight + 1);
                }
            }
        }
    }

    /**
     * 设置刷新View的marginTop
     */
    public void setRefreshViewMarginTop(int marginTop) {
        if (mRefreshView == null) {
            return;
        }
        MarginLayoutParams params = (MarginLayoutParams) mRefreshView.getLayoutParams();
        if (marginTop < -mRefreshViewHeight + 1) {
            marginTop = -mRefreshViewHeight + 1;
        }
        params.topMargin = marginTop;
        mRefreshView.setLayoutParams(params);
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断是不是滚动到了最顶部，这个是从SwipeRefreshLayout里面copy过来的源代码
     */
    public boolean canScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(this, -1) || this.getScrollY() > 0;//getScrollY: view的顶部还有距离, 可以向上滑动.
        } else {
            return ViewCompat.canScrollVertically(this, -1);
        }
    }

    /**
     * 停止刷新
     * 不涉及对footview的处理
     */
    public void stopRefresh() {
        if (mCurrentRefreshStatus != REFRESH_STATUS_REFRESHING) {
            return;
        }
        mCurrentRefreshStatus = REFRESH_STATUS_NORMAL;
        restoreRefreshView();//还原刷新头高度 -> 只留1px可见.
        if (mRefreshCreator != null) {
            mRefreshCreator.onStopRefresh();
        }
    }

    // 处理刷新回调监听
    private OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}
