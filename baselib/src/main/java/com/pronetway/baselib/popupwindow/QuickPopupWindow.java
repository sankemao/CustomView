package com.pronetway.baselib.popupwindow;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by jin on 2017/9/25.16:56
 * 邮箱:210980059@qq.com
 * 描述:builder设计模式快速构建popupWindow.
 */
public class QuickPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener {

    private PopupBuilder mBuilder;
    private Context mContext;
    private Window mWindow;

    protected QuickPopupWindow(PopupBuilder popupBuilder) {
        super(popupBuilder.mContext);
        this.mContext = popupBuilder.mContext;
        this.mBuilder = popupBuilder;
        apply();
        dealBackground();
    }

    private void dealBackground() {
        // 获取当前Activity的window
        Activity activity = (Activity) mContext;
        if(activity != null){
            //如果设置的值在0 - 1的范围内，则用设置的值，否则用默认值
            final  float alpha = mBuilder.mBackgroundDrakValue;
            mWindow = activity.getWindow();
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = alpha;
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mWindow.setAttributes(params);
        }

        this.setOnDismissListener(this);
    }

    @Override
    public void onDismiss() {
        if(mBuilder.mOnDismissListener!=null){
            mBuilder.mOnDismissListener.onDismiss();
        }

        //如果设置了背景变暗，那么在dissmiss的时候需要还原
        if(mWindow!=null){
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = 1.0f;
            mWindow.setAttributes(params);
        }
        if(this.isShowing()){
            this.dismiss();
        }
    }

    private void apply() {
        View rootView;
        if (mBuilder.mContentLayoutId > 0) {
            rootView = LayoutInflater.from(mContext).inflate(mBuilder.mContentLayoutId, null);
        } else {
            rootView = mBuilder.mContentView;
        }
        this.setContentView(rootView);

        //设置宽高.
        if (mBuilder.mWidth != 0 && mBuilder.mHeight != 0) {
            this.setWidth(mBuilder.mWidth);
            this.setHeight(mBuilder.mHeight);
        } else {
            this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        this.setClippingEnabled(mBuilder.mClippEnable);
        if(mBuilder.mIgnoreCheekPress){
            this.setIgnoreCheekPress();
        }
        if(mBuilder.mInputMode!=-1){
            this.setInputMethodMode(mBuilder.mInputMode);
        }
        if(mBuilder.mSoftInputMode!=-1){
            this.setSoftInputMode(mBuilder.mSoftInputMode);
        }
        if(mBuilder.mOnDismissListener!=null){
            this.setOnDismissListener(mBuilder.mOnDismissListener);
        }
        if(mBuilder.mOnTouchListener!=null){
            this.setTouchInterceptor(mBuilder.mOnTouchListener);
        }
        this.setTouchable(mBuilder.mTouchable);


        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //2017.6.27 add:fix 设置  setOutsideTouchable（false）点击外部取消的bug.
        // 判断是否点击PopupWindow之外的地方关闭 popWindow
        if(!mBuilder.mCancelable){
            //注意这三个属性必须同时设置，不然不能disMiss，以下三行代码在Android 4.4 上是可以，然后在Android 6.0以上，下面的三行代码就不起作用了，就得用下面的方法
            this.setFocusable(true);
            this.setOutsideTouchable(false);
//            this.setBackgroundDrawable(null);
            //注意下面这三个是contentView 不是PopupWindow
            this.getContentView().setFocusable(true);
            this.getContentView().setFocusableInTouchMode(true);
            this.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        QuickPopupWindow.this.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            //在Android 6.0以上 ，只能通过拦截事件来解决
            this.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN)
                            && ((x < 0) || (x >= mBuilder.mWidth) || (y < 0) || (y >= mBuilder.mHeight))) {
//                        Log.e(TAG,"out side ");
//                        Log.e(TAG,"width:"+this.getWidth()+"height:"+this.getHeight()+" x:"+x+" y  :"+y);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                        Log.e(TAG,"out side ...");
                        return true;
                    }
                    return false;
                }
            });
        }else{
            this.setFocusable(mBuilder.mFocusable);
            this.setOutsideTouchable(mBuilder.mCancelable);
        }
    }

    public static class PopupBuilder {
        private Context mContext;
        private int mWidth, mHeight;
        private boolean mFocusable;
        private int mContentLayoutId;
        private View mContentView;
        private boolean mCancelable = true;//默认点击外部可以取消
        private int mAnimationStyle;
        private boolean mClippEnable = true;
        private boolean mIgnoreCheekPress;
        private int mInputMode;
        private OnDismissListener mOnDismissListener;
        private int mSoftInputMode;
        private boolean mTouchable;
        private View.OnTouchListener mOnTouchListener;
        private float mBackgroundDrakValue = 0.7f;
        private SparseArray<View.OnClickListener> mClickArray = new SparseArray<>();

        public PopupBuilder(Context cxt) {
            this.mContext = cxt;
        }

        //设置宽高.
        public PopupBuilder setSize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        //设置是否接收焦点.
        public PopupBuilder setFocusable(boolean focusable){
            this.mFocusable = focusable;
            return this;
        }

        //设置contentview.
        public PopupBuilder setContentView(@LayoutRes int contentLayoutId){
            this.mContentLayoutId = contentLayoutId;
            mContentView = null;
            return this;
        }

        //设置contentview.
        public PopupBuilder setContentView(View view) {
            this.mContentView = view;
            this.mContentLayoutId = 0;
            return this;
        }

        //设置是否可点击外面取消.
        public PopupBuilder setCancelable(boolean cancelable){
            this.mCancelable = cancelable;
            return this;
        }

        //设置弹窗动画.
        public PopupBuilder setAnimation(int animationStyle){
            this.mAnimationStyle = animationStyle;
            return this;
        }

        public PopupBuilder setClippingEnable(boolean enable){
            this.mClippEnable =enable;
            return this;
        }

        public PopupBuilder setIgnoreCheekPress(boolean ignoreCheekPress){
            this.mIgnoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupBuilder setInputMethodMode(int mode){
            this.mInputMode = mode;
            return this;
        }

        public PopupBuilder setOnDissmissListener(OnDismissListener onDissmissListener){
            this.mOnDismissListener = onDissmissListener;
            return this;
        }


        public PopupBuilder setSoftInputMode(int softInputMode){
            this.mSoftInputMode = softInputMode;
            return this;
        }


        public PopupBuilder setTouchable(boolean touchable){
            this.mTouchable = touchable;
            return this;
        }

        public PopupBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter){
            this.mOnTouchListener = touchIntercepter;
            return this;
        }

        /**
         * 设置背景变暗的值
         */
        public PopupBuilder setBgDarkAlpha(float darkValue){
            this.mBackgroundDrakValue = darkValue;
            return this;
        }

        //设置点击事件, 与id绑定
        public PopupBuilder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
            this.mClickArray.put(viewId, listener);
            return this;
        }

        //构建具体对象.
        public QuickPopupWindow create() {
            QuickPopupWindow quickPopupWindow = new QuickPopupWindow(this);
            return quickPopupWindow;
        }

        //显示.
        public QuickPopupWindow show(View anchor) {
            QuickPopupWindow quickPopupWindow = create();
            quickPopupWindow.showAsDropDown(anchor);
            return quickPopupWindow;
        }

        //显示.
        public QuickPopupWindow show(View anchor, int xOff, int yOff) {
            QuickPopupWindow quickPopupWindow = create();
            quickPopupWindow.showAsDropDown(anchor, xOff, yOff);
            return quickPopupWindow;
        }

        //显示.
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public QuickPopupWindow show(View anchor, int xOff, int yOff, int gravity) {
            QuickPopupWindow quickPopupWindow = create();
            quickPopupWindow.showAsDropDown(anchor, xOff, yOff, gravity);
            return quickPopupWindow;
        }


    }
}
