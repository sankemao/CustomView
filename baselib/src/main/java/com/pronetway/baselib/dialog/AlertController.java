package com.pronetway.baselib.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by jin on 2017/6/19.
 */
class AlertController {
    private AlertDialog mDialog;
    private Window mWindow;

    private DialogViewHelper mViewHelper;//在内部类的Params的apply()方法中注入.

    public AlertController(AlertDialog dialog, Window window) {
        this.mDialog = dialog;
        this.mWindow = window;
    }

    public void setViewHelper(DialogViewHelper viewHelper) {
        mViewHelper = viewHelper;
    }

    public AlertDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取dialog的window.
     * @return
     */
    public Window getWindow() {
        return mWindow;
    }

    public void setText(@IdRes int resId, CharSequence text) {
        mViewHelper.setText(resId, text);
    }

    public void setOnClickListener(@IdRes int resId, View.OnClickListener listener) {
        mViewHelper.setOnClickListener(resId, listener);
    }

    public <T extends View> T getView(int resId) {
        return mViewHelper.getView(resId);
    }

    public static class AlertParams {
        public Context mContext;
        public int mThemeResId;
        //点击空白是否能取消.
        public boolean mCancelable = true;
        //监听
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        //布局view.
        public View mView;
        //布局layout id;
        public int mViewLayoutResId;
        //存放text.
        public SparseArray<CharSequence> mTextArray = new SparseArray<>();
        //存放点击事件
        public SparseArray<View.OnClickListener> mClickArray = new SparseArray<>();
        //默认宽高为包裹内容.
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        //动画
        public int mAnimations = 0;
        //位置
        public int mGravity = Gravity.CENTER;
        public float mAmount = 0.6f;

        public AlertParams(Context cxt, int themeResId) {
            this.mContext = cxt;
            this.mThemeResId = themeResId;
        }

        /**
         * 绑定和设置参数, 将参数都给controller.
         * @param controller
         */
        public void apply(AlertController controller) {
            //1. 布局 DialogViewHelper.
            DialogViewHelper viewHelper = null;
            if (mViewLayoutResId != 0) {
                viewHelper = new DialogViewHelper(mContext, mViewLayoutResId);
            }

            if (mView != null) {
                viewHelper = new DialogViewHelper();
                viewHelper.setContentView(mView);
            }

            if (viewHelper == null) {
                throw new IllegalArgumentException("请先调用setContentView :)");
            }

            //0. 设置Controller的辅助类.
            controller.setViewHelper(viewHelper);

            //1. 给dialog设置布局.
            controller.getDialog().setContentView(viewHelper.getContentView());

            //2. 设置文本
            int textArraySize = mTextArray.size();
            for (int i = 0; i < textArraySize; i++) {
                controller.setText(mTextArray.keyAt(i), mTextArray.valueAt(i));
            }

            //3. 设置点击
            int clickArraySize = mClickArray.size();
            for (int i = 0; i < clickArraySize; i++) {
                controller.setOnClickListener(mClickArray.keyAt(i), mClickArray.valueAt(i));
            }

            Window window = controller.getWindow();
            //4. 设置弹出位置
            window.setGravity(mGravity);
            //动画
            if (mAnimations != 0) {
                window.setWindowAnimations(mAnimations);
            }
            //去黑边, 正真全屏.
            window.getDecorView().setPadding(0,0,0,0);//设置全屏.
            //取消背景模糊 0~1.0f
            window.setDimAmount(mAmount);
            //尺寸.
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            window.setAttributes(params);
        }
    }
}
