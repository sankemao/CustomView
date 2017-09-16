package com.pronetway.baselib.recyclerview;

import android.content.Context;
import android.widget.ImageView;

import com.pronetway.baselib.recyclerview.helper.ImgLoadUtil;

/**
 * Created by jin on 2017/5/10.
 *
 */
public class JHolderImageLoader extends JViewHolder.HolderImageLoader {
    public JHolderImageLoader(Object imagePath) {
        super(imagePath);
    }

    @Override
    public void displayImage(Context context, ImageView imageView, Object imagePath) {
        if (imagePath == null) {
            return;
        }
        ImgLoadUtil.displayImage(imagePath, imageView);
    }

    @Override
    public void displayCircleImage(Context context, ImageView imageView, Object imagePath) {
        if (imagePath == null) {
            return;
        }
        ImgLoadUtil.displayCircle(imageView, imagePath);
    }
}
