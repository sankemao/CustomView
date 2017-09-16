package com.pronetway.baselib.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by jin on 2017/6/19.
 * dilog的辅助处理类.
 */
class DialogViewHelper {
    private View mContentView = null;
    private SparseArray<WeakReference<View>> mViews;

    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    public DialogViewHelper(Context context, int viewLayoutResId) {
        this();
        mContentView = LayoutInflater.from(context).inflate(viewLayoutResId, null);
    }

    public void setContentView(View contentView) {
        this.mContentView = contentView;
    }

    //获取content内容的view.
    public View getContentView() {
        return mContentView;
    }

    public void setText(@IdRes int resId, CharSequence text) {
        TextView tv = getView(resId);
        if (tv != null) {
            tv.setText(text);
        }
    }

    public void setOnClickListener(@IdRes int resId, View.OnClickListener listener) {
        View view = getView(resId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public <T extends View> T getView(int resId) {
        //这里要注意判空.
        WeakReference<View> viewWeakReference = mViews.get(resId);
        View view = null;
        if (viewWeakReference != null) {
            view = viewWeakReference.get();
        }
        if (view == null) {
            view = mContentView.findViewById(resId);
            if (view != null) {
                mViews.put(resId, new WeakReference<View>(view));
            }
        }
        return (T) view;
    }
}
