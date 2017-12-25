package com.pronetway.customview.custom.datascreen;

import android.view.View;
import android.view.ViewGroup;

/**
 * Description:TODO
 * Create Time:2017/12/25.23:25
 * Author:jin
 * Email:210980059@qq.com
 */
public abstract class BaseMenuAdapter {
    //获取总共多少条
    public abstract int getCount();

    //获取当前的TabView.
    public abstract View getTabView(int position, ViewGroup parent);

    //获取当前的菜单内容.
    public abstract View getMenuView(int position, ViewGroup parent);
}
