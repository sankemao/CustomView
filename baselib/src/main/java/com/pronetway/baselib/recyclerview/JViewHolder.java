package com.pronetway.baselib.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jin on 2017/5/10.
 */
public class JViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;

    public JViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public <T extends View> T getViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public JViewHolder setViewVisibility(int viewId, int visibility) {
        getViewById(viewId).setVisibility(visibility);
        return this;
    }

    public JViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getViewById(viewId);
        tv.setText(text);
        return this;
    }

    public JViewHolder setSelected(int viewId, boolean selected) {
        View view = getViewById(viewId);
        view.setSelected(selected);
        return this;


    }

    public JViewHolder setImgByUrl(int viewId, HolderImageLoader imageLoader) {
        ImageView imageView = getViewById(viewId);
        if (imageLoader == null) {
            throw new NullPointerException("imageLoader is null");
        }
        imageLoader.displayImage(imageView.getContext(), imageView, imageLoader.getImagePath());
        return this;
    }

    public JViewHolder setCircleImgByUrl(int viewId, HolderImageLoader imageLoader) {
        ImageView imageView = getViewById(viewId);
        if (imageLoader == null) {
            throw new NullPointerException("imageLoader is null");
        }
        imageLoader.displayCircleImage(imageView.getContext(), imageView, imageLoader.getImagePath());
        return this;
    }

    public JViewHolder setTextStyle(int viewId, int style) {
        TextView textView = getViewById(viewId);
        textView.getPaint().setFlags(style);
        return this;
    }

    public JViewHolder setOnClickListener(int viewId,
                                          View.OnClickListener listener) {
        View view = getViewById(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public static abstract class HolderImageLoader {
        private Object mImagePath;

        public HolderImageLoader(Object imagePath) {
            this.mImagePath = imagePath;
        }

        public Object getImagePath() {
            return mImagePath;
        }

        public abstract void displayImage(Context context, ImageView imageView, Object imagePath);

        public abstract void displayCircleImage(Context context, ImageView imageView, Object imagePath);
    }


    /*****
     * 条目点击事件
     ************/
    public JViewHolder setOnItemClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
        return this;
    }

    public JViewHolder setOnItemLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
        return this;
    }

}
