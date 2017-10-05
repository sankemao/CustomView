package com.pronetway.customview.custom.taglayout;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description:流式布局的adapter
 * Create Time:2017/10/5.17:59
 * Author:jin
 * Email:210980059@qq.com
 */
public abstract class TagBaseAdapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    //有多少个条目
    public abstract int getCount();

    //通过positon获取View.
    public abstract View getView(int position, ViewGroup parent);

    //观察者模式及时通知更新.


    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
}
