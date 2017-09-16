package com.pronetway.baselib.recyclerview.helper;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

/**
 * Created by jin on 2017/5/26.
 */
public class DiffCallBack extends DiffUtil.Callback {
    @Override
    public int getOldListSize() {
        return 0;
    }

    @Override
    public int getNewListSize() {
        return 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
