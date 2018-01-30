package com.pronetway.customview.custom.indicators;

import android.view.View;
import android.view.ViewGroup;

/**
 * Description: adapter目标接口
 * Create Time: 2018/1/17.14:01
 * Author:jin
 * Email:210980059@qq.com
 */
public interface BaseIndicatorAdapter {
    /**
     * 获取indicator个数
     * @return  indicator个数
     */
    int getCount();

    /**
     * 获取indicator子view
     * @param position  indicator的角标
     * @param parent    indicator的父布局
     * @return          子view
     */
    View getView(int position, ViewGroup parent);
}
