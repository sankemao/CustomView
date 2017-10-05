package com.pronetway.customview.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Description:TODO
 * Create Time:2017/9/29.14:02
 * Author:jin
 * Email:210980059@qq.com
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
