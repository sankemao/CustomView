package com.pronetway.baselib.recyclerview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.pronetway.baselib.recyclerview.headfootview.LoadViewCreator;

public class LoadRefreshRecyclerView extends RefreshRecyclerView {
    // 上拉加载更多的辅助类
    private LoadViewCreator mLoadCreator;
    // 当前的状态
    private int mCurrentLoadStatus;
    // 默认状态
    public int LOAD_STATUS_NORMAL = 0x0099;

    // 正在加载更多状态
    public int LOAD_STATUS_LOADING = 0x0066;
    // 数据加载完全, 没有更多的数据了.
    public boolean isNomore;
    // 前一次加载完成后所有条目的数量.
    public int previousTotal = 0;

    public LoadRefreshRecyclerView(Context context) {
        super(context);
    }

    public LoadRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 先处理上拉加载更多，同时考虑加载列表的不同风格样式，确保这个项目还是下一个项目都能用
    // 所以我们不能直接添加View，需要利用辅助类
    public void addLoadViewCreator(LoadViewCreator loadCreator) {
        this.mLoadCreator = loadCreator;
        addLoadView();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        addLoadView();
    }

    /**
     * 添加底部加载更多View
     */
    private void addLoadView() {
        Adapter adapter = getAdapter();
        if (adapter != null && mLoadCreator != null) {
            // 添加底部加载更多View
            View loadView = mLoadCreator.getLoadView(getContext(), this);
            if (loadView != null) {
                addFooterView(loadView);
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        //停止/有listener/现在状态不是加载更多/下面还有条目.
        if (state == RecyclerView.SCROLL_STATE_IDLE && mListener != null && mCurrentLoadStatus != LOAD_STATUS_LOADING && !isNomore) {
            if (!canScrollDown()
                    && getRefreashHeaderState() != REFRESH_STATUS_REFRESHING) {
                mCurrentLoadStatus = LOAD_STATUS_LOADING;
                mLoadCreator.onLoading();

                if (isNetWorkConnected(getContext())) {
                    mListener.onLoad();
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onLoad();
                        }
                    }, 1000);
                }
            }
        }
    }

    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     * 判断是不是滚动到了最顶部，这个是从SwipeRefreshLayout里面copy过来的源代码
     */
    public boolean canScrollDown() {
        return ViewCompat.canScrollVertically(this, 1);
    }


    //*******************************处理recyclerview的刷新头部和尾部******************************

    /**
     * 刷新错误, 还原头部和尾部.
     */
    public void stopRefreshLoad() {
        stopRefreshLoad(-1);
    }

    /**
     * 刷新成功, 处理头部和尾部.
     * @param limit
     */
    public void stopRefreshLoad(int limit) {
        //如果当前是在刷新.
        if (mCurrentLoadStatus != LOAD_STATUS_LOADING) {
            stopRefresh(limit);
        }
        //如果是加载更多.
        else {
            stopLoadMore(limit);
        }
    }

    /**
     * 当前操作为刷新
     * 在处理头部基础上, 还要考虑到尾部.
     */
    private void stopRefresh(int limit) {
        super.stopRefresh();
        //以下为处理尾部
        mCurrentLoadStatus = LOAD_STATUS_NORMAL;
        //limit<0, 即刷新错误时不处理尾部.
        if (limit > 0) {
            resetLoadMore();
            //判断数据是否结束, 改变尾部.
            if (getRealItemCount() < limit) {
                endLoadMore();
            }
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    /**
     * 当前操作为加载更多
     * 只需考虑尾部.
     */
    private void stopLoadMore(int limit) {
        mCurrentLoadStatus = LOAD_STATUS_NORMAL;
        if (mLoadCreator != null) {
            if (limit > 0) {//加载成功
                //新增数据和limit数据比较
                if (getLayoutManager().getItemCount() - previousTotal < limit) {
                    endLoadMore();
                } else {
                    stopLoadMore();
                }
            } else {//加载失败
                errorLoadMore();
            }
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    /**
     * 加载更多失败
     */
    private void errorLoadMore() {
        if (mLoadCreator != null) {
            mLoadCreator.onError();
        }
    }

    /**
     * 停止加载更多.
     */
    private void stopLoadMore() {
        if (mLoadCreator != null) {
            mLoadCreator.onStopLoad();
        }
    }

    /**
     * 结束加载更多
     */
    private void endLoadMore() {
        if (mLoadCreator != null) {
            mLoadCreator.onEndLoad();
        }
        isNomore = true;
    }

    /**
     * 还原加载更多
     */
    private void resetLoadMore() {
        previousTotal = 0;//所有条目计数归零
        if (mLoadCreator != null) {
            mLoadCreator.reset();
        }
        isNomore = false;
    }

    //*******************************处理recyclerview的刷新头部和尾部******************************

    // 处理加载更多回调监听
    private OnLoadMoreListener mListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mListener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }

    /**
     * 除去头部和尾部的条目数量.
     */
    public int getRealItemCount() {
        return getLayoutManager().getItemCount()
                - mWrapRecyclerAdapter.getHeaderViewsCount()
                - mWrapRecyclerAdapter.getFooterViewsCount();
    }
}