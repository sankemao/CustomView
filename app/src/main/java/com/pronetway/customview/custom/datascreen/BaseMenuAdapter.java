package com.pronetway.customview.custom.datascreen;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:TODO
 * Create Time:2017/12/25.23:25
 * Author:jin
 * Email:210980059@qq.com
 */
public abstract class BaseMenuAdapter {

    private List<MenuObserver> mMenuObservers = new ArrayList<>();

    public void registerDataObserver(MenuObserver observer) {
        mMenuObservers.add(observer);
    }

    public void unregisterDataObserver(MenuObserver observer) {
        mMenuObservers.remove(observer);
    }

    public void closeMenu() {
        if (mMenuObservers != null && mMenuObservers.size() > 0) {
            for (MenuObserver menuObserver : mMenuObservers) {
                menuObserver.observerCloseMenu();
            }
        }
    }
    //获取总共多少条
    public abstract int getCount();

    //获取当前的TabView.
    public abstract View getTabView(int position, ViewGroup parent);

    //获取当前的菜单内容.
    public abstract View getMenuView(int position, ViewGroup parent);

    /**
     * 关闭tab时调用
     * @param tabView   所选的tab
     */
    public abstract void menuClose(View tabView);

    /**
     * 关打开tab时调用
     * @param tabView   所选的tab
     */
    public abstract void menuOpen(View tabView);
}
