package com.pronetway.customview.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;

/**
 * Description:TODO
 * Create Time: 2018/4/20.15:45
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyTouchViewGroup extends FrameLayout {

    private View mChildView;

    public MyTouchViewGroup(Context context) {
        this(context, null);
    }

    public MyTouchViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTouchViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("++++++ViewGroup ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d("++++++ViewGroup ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d("++++++ViewGroup ACTION_UP");
                break;
        }
        return super.onTouchEvent(event);
    }
}
