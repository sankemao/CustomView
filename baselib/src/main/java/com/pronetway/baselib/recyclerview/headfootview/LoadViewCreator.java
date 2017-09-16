package com.pronetway.baselib.recyclerview.headfootview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class LoadViewCreator {

    /**
     * 获取上拉加载更多的View
     *
     * @param context 上下文
     * @param parent  RecyclerView
     */
    public abstract View getLoadView(Context context, ViewGroup parent);

    /**
     * 正在上拉
     *
     * @param currentDragHeight    当前拖动的高度
     * @param loadViewHeight    总的加载高度
     * @param currentLoadStatus 当前状态
     */
    public abstract void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus);

    /**
     * 正在加载中
     */
    public abstract void onLoading();

    /**
     * 停止加载
     */
    public abstract void onStopLoad();

    /**
     * 没有更多内容了
     */
    public abstract void onEndLoad();

    /**
     * 重置, 从没有更多内容状态恢复到加载更多状态
     */
    public abstract void reset();

    public abstract void onError();
}