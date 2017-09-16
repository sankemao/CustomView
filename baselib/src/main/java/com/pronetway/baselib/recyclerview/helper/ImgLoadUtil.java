package com.pronetway.baselib.recyclerview.helper;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Glide加载图片
 */

public class ImgLoadUtil {

    public static void displayImage(Object url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url)
                .dontAnimate()      //解决大小在刷新前不一致问题
                .into(imageView);
    }

//    public static void displayImageAsBmp(Object url, ImageView imageView) {
//        Glide.with(imageView.getContext())
//                .load(url)
////                .asBitmap()
//                .centerCrop()
//                .error(getDefaultPic(0))
//                .into(imageView);
//    }

    /**
     * 加载圆形图片
     * @param imageView
     * @param imageUrl
     */
    public static void displayCircle(ImageView imageView, Object imageUrl) {
        Glide.with(imageView.getContext()).load(imageUrl)
                .transform(new GlideCircleTransform(imageView.getContext()))
//                .placeholder(getDefaultPic(1))
//                .error(getDefaultPic(1))
                .dontAnimate()
                .into(imageView);
    }

    public static void displayRecyImage(Object url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url)
//                .placeholder(getDefaultPic(0))
                .centerCrop()
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new GlideDrawableImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                    }
                });
    }


}
