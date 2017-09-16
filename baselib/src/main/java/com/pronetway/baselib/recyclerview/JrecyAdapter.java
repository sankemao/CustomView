package com.pronetway.baselib.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jin on 2017/5/10.
 *
 */
public abstract class JrecyAdapter<T> extends RecyclerView.Adapter<JViewHolder>{

    private MultiTypeSupport<T> multiTypeSupport;
    private int mLayoutId;
    protected Context mContext;
    public List<T> mShowItems;
    private final LayoutInflater mInflater;

    public JrecyAdapter(Context context, List<T> showItems, int layoutId) {
        this.mContext = context;
        mShowItems = showItems;
        this.mLayoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    public JrecyAdapter(Context context, List<T> showItems, MultiTypeSupport<T> multiTypeSupport) {
        this(context, showItems, -1);
        this.multiTypeSupport = multiTypeSupport;
    }


    /**
     * @param position
     * @return multitypeSupport的返回值, 即多条目的布局layoutId.
     */
    @Override
    public int getItemViewType(int position) {
        if (multiTypeSupport != null) {
            return multiTypeSupport.getLayoutId(mShowItems.get(position), position);
        }
        return super.getItemViewType(position);
    }

    /**
     *
     * @param parent
     * @param viewType getItemViewType的返回值.
     * @return
     */
    @Override
    public JViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (multiTypeSupport != null) {
            this.mLayoutId = viewType;
        }
        return new JViewHolder(mInflater.inflate(mLayoutId, parent, false));
    }

    /**
     * TODO:
     * RECYCLERVIEW的条目数据有偏移
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final JViewHolder holder, final int position) {
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v, mShowItems.get(position), position);
                }
            });
        }

        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mLongClickListener.onLongClick(position);
                }
            });
        }
        convert(holder, mShowItems.get(position), position);
    }

    protected abstract void convert(JViewHolder holder, T itemData, int position);

    @Override
    public int getItemCount() {
        return mShowItems == null ? 0 : mShowItems.size();
    }

    public List<T> getShowItems() {
        return mShowItems;
    }

    /**
     * 刷新所有条目
     * @param showItems
     */
    public void refreshAllData(List<T> showItems) {
        this.mShowItems = showItems;
        this.notifyDataSetChanged();
    }

    /**
     * 添加条目
     * @param showItems
     */
    public void addAllData(List<T> showItems) {
        if (mShowItems == null) {
            mShowItems = new ArrayList<>();
        }
        mShowItems.addAll(showItems);
        this.notifyDataSetChanged();
    }

    /**
     * 删除指定条目
     * @param position
     */
    public void removeItem(int position) {
        mShowItems.remove(position);
        this.notifyItemRemoved(position);//删除条目并带有动画效果
        this.notifyItemRangeChanged(position, getItemCount() - position);//刷新列表的position.
    }
    /****************
     * 条目点击事件
     ***************/
    public OnItemClickListener mItemClickListener;
    public OnLongClickListener mLongClickListener;

    public<M> void setOnItemClickListener(OnItemClickListener<M> itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }


    public interface OnItemClickListener<K>{
        void onItemClick(View v, K itemData, int position);
    }

    public interface OnLongClickListener {
        boolean onLongClick(int position);
    }
}
