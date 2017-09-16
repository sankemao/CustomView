package com.pronetway.baselib.navigationbar;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jin on 2017/7/15.
 * 头部的Builder基类.
 */
public abstract class AbsNavigationBar<P extends AbsNavigationBar.Builder.AbsNavigationParams> implements INavigationBar {
    private P mParams;
    private View mNavigationView;

    public AbsNavigationBar(P params) {
        mParams = params;
        createAndBindView();
    }

    /**
     * 绑定和创建view.
     */
    private void createAndBindView() {
        if (mParams.mParent == null) {
            //获取activity的根布局.
            ViewGroup activityRoot = (ViewGroup) ((Activity) mParams.mContext).findViewById(android.R.id.content);
            mParams.mParent = (ViewGroup) activityRoot.getChildAt(0);
//            ViewGroup decorView = (ViewGroup) ((Activity) mParams.mContext).getWindow().getDecorView();
//            mParams.mParent = (ViewGroup) decorView.getChildAt(0);
        }
        //1. 创建view.
        mNavigationView = LayoutInflater.from(mParams.mContext)
                .inflate(bindLayoutId(), mParams.mParent, false);
        //2. 添加
        mParams.mParent.addView(mNavigationView, 0);

        applyView();

    }

    public P getParams() {
        return mParams;
    }

    protected void setText(int viewId, String text) {
        TextView tv = getViewById(viewId);
        if (!TextUtils.isEmpty(text)) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
        }
    }

    protected void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getViewById(viewId);
        if (view.getVisibility() == View.VISIBLE && listener != null) {
            view.setOnClickListener(listener);
        }
    }

    public void setImageResource(int viewId, int resourceId) {
        ImageView imageView = getViewById(viewId);
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(resourceId);
        }
    }

    public <T extends View> T getViewById(int viewId) {
        return (T) mNavigationView.findViewById(viewId);
    }

    public abstract static class Builder {

        public abstract AbsNavigationBar build();

        public static class AbsNavigationParams {
            public Context mContext;
            public ViewGroup mParent;
            public AbsNavigationParams(Context cxt, ViewGroup parent) {
                this.mContext = cxt;
                this.mParent = parent;
            }
        }

    }

}
