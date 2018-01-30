package com.pronetway.customview.custom.indicators;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Description:TODO
 * Create Time: 2018/1/17.14:05
 * Author:jin
 * Email:210980059@qq.com
 */
public class TrackIndicatorView extends HorizontalScrollView {
    private BaseIndicatorAdapter mAdapter;
    private LinearLayout mContainer;

    public TrackIndicatorView(Context context) {
        this(context, null);
    }

    public TrackIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContainer = new LinearLayout(context);
        this.addView(mContainer);
    }

    public void setAdapter(BaseIndicatorAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter can not be null");
        }

        this.mAdapter = adapter;
        int viewCount = adapter.getCount();
        for (int i = 0; i < viewCount; i++) {
            View indicatorView = adapter.getView(i, mContainer);
            mContainer.addView(indicatorView);
        }
    }
}
