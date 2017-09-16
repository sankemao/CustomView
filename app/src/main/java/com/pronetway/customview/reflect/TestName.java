package com.pronetway.customview.reflect;

import android.util.Log;

/**
 * Created by jin on 2017/9/4.
 */
public class TestName {

    private static final String TAG = "TestName";

    private String name;

    private TestName(String name) {
        this.name = name;
    }

    public void sysName() {
        Log.d(TAG, "sysName: " + name);
    }
}
