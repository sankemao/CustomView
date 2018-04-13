package com.pronetway.customview.ui.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

/**
 * Description:TODO
 * Create Time: 2018/4/10.17:47
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyTestView extends View {
    public MyTestView(Context context) {
        this(context, null);
    }

    public MyTestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.e("========ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.e("========ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.e("========ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.e("========ACTION_CANCEL");
                break;
        }
        return true;
    }
}
