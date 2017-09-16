package com.pronetway.baselib.recyclerview;

import android.support.annotation.LayoutRes;

/**
 * Created by jin on 2017/5/8.
 */
public interface MultiTypeSupport<T> {
    @LayoutRes
    int getLayoutId(T item, int position);
}
