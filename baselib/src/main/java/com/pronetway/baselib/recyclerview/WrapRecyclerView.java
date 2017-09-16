package com.pronetway.baselib.recyclerview;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jin on 2017/5/10.
 *
 */
public class WrapRecyclerView extends RecyclerView {

    private View mEmptyView, mLoadingView;

    // 包裹了一层的头部底部Adapter
    protected WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private RecyclerView.Adapter mAdapter;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 仿照listview添加头布局.
     * @param adapter
     */
    @Override
    @CallSuper
    public void setAdapter(Adapter adapter) {
        //防止多次添加adapter
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
            mAdapter = null;
        }

        this.mAdapter = adapter;

        if (adapter instanceof WrapRecyclerAdapter) {
            mWrapRecyclerAdapter = (WrapRecyclerAdapter) adapter;
        } else {
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(adapter);
        }

        super.setAdapter(mWrapRecyclerAdapter);

        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);

        // 解决GridLayout添加头部和底部也要占据一行
        mWrapRecyclerAdapter.adjustSpanSize(this);
    }

    // 添加头部
    public void addHeaderView(View view) {
        // 如果没有Adapter那么就不添加，也可以选择抛异常提示
        // 让他必须先设置Adapter然后才能添加，这里是仿照ListView的处理方式
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.addHeaderView(view);
        }
    }

    // 添加底部
    public void addFooterView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.addFooterView(view);
        }
    }

    // 移除头部
    public void removeHeaderView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.removeHeaderView(view);
        }
    }

    // 移除底部
    public void removeFooterView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.removeFooterView(view);
        }
    }

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyDataSetChanged();
            dataChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemRemoved(positionStart + mWrapRecyclerAdapter.getHeaderViewsCount());
            dataChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemMoved没效果
            if (mWrapRecyclerAdapter != mAdapter) {
                int headerViewsCount = mWrapRecyclerAdapter.getHeaderViewsCount();
                mWrapRecyclerAdapter.notifyItemMoved(fromPosition + headerViewsCount, toPosition + headerViewsCount);
            }
            dataChanged();
        }

//        @Override
//        public void onItemRangeChanged(int positionStart, int itemCount) {
//            if (mAdapter == null) return;
//            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
//            if (mWrapRecyclerAdapter != mAdapter) {
//                int headerViewsCount = mWrapRecyclerAdapter.getHeaderViewsCount();
//                mWrapRecyclerAdapter.notifyItemRangeChanged(positionStart + headerViewsCount, itemCount);
//            }
//            dataChanged();
//        }

        /**
         * 坑的一比         ---jin
         * notifyItemRangeChanged(position, getItemCount() - position)刚方法执行时会触发此观察者方法, 且payload=null.
         * 需要考虑头布局的数量带来的偏移量.
         */
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter) {
                int headerViewsCount = mWrapRecyclerAdapter.getHeaderViewsCount();
                mWrapRecyclerAdapter.notifyItemRangeChanged(positionStart + headerViewsCount, itemCount, payload);
            }
            dataChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemInserted没效果
            if (mWrapRecyclerAdapter != mAdapter) {
                int headerViewsCount = mWrapRecyclerAdapter.getHeaderViewsCount();
                mWrapRecyclerAdapter.notifyItemInserted(positionStart + headerViewsCount);
            }
            dataChanged();
        }

    };

    /**
     * 空数据页面.
     * @param emptyView
     */
    public void addEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    public void addLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
    }

    public void dataChanged() {
        if (mEmptyView != null) {
            if (mAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(VISIBLE);
            } else {
                mEmptyView.setVisibility(GONE);
            }
        }
    }

}
