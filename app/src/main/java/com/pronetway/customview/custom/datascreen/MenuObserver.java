package com.pronetway.customview.custom.datascreen;

/**
 * Description: 观察者
 * Create Time:2017/12/28.22:33
 * Author:jin
 * Email:210980059@qq.com
 */
public abstract class MenuObserver {

    //参考listView源码
    //被观察者发生变化后, 会通知观察者做出相应的动作.
    public abstract void observerCloseMenu();
}
