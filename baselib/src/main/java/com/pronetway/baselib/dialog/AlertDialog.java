package com.pronetway.baselib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.pronetway.baselib.R;


/**
 * Created by jin on 2017/6/19.
 *
 */
public class AlertDialog extends Dialog {
    private AlertController mController;

    /**
     * new出AlertController对象,  对象持有dialog引用.
     */
    protected AlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        mController = new AlertController(this, this.getWindow());
    }

    public void setText(@IdRes int resId, CharSequence text) {
        mController.setText(resId, text);
    }

    public void setOnClickListener(@IdRes int resId, View.OnClickListener listener) {
        mController.setOnClickListener(resId, listener);
    }

    public <T extends View> T getView(int resId) {
       return mController.getView(resId);
    }


    public static class Builder {
        private final AlertController.AlertParams P;
        public Builder(@NonNull Context cxt) {
            this(cxt, R.style.dialog);
        }

        /**
         * 第一步.
         * builder中new出AlertController的内部类AlertParams对象P,
         * 并在接下来通过创建者模式给P设置各种参数.
         */
        public Builder(@NonNull Context cxt, @StyleRes int themeResId) {
            P = new AlertController.AlertParams(cxt, themeResId);
        }

        /**
         * 第二步, 设置参数.
         * @param layoutId
         * @return
         */
        public Builder setContentView(@LayoutRes int layoutId) {
            P.mView = null;
            P.mViewLayoutResId = layoutId;
            return this;
        }

        public Builder setContentView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }


        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setText(@IdRes int viewId, CharSequence text) {
            P.mTextArray.put(viewId, text);
            return this;
        }

        public Builder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
            P.mClickArray.put(viewId, listener);
            return this;
        }

        //配置一些万能的参数.
        public Builder fullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        //从底部弹出.
        public Builder fromBottom(boolean isAnimation) {
            if (isAnimation) {
                P.mAnimations = R.style.dialog_from_bottom_anim;
            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }
        //设置dialog的宽高.
        public Builder setWidthAndHeight(int width, int height) {
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }

        //TODO: 添加默认动画.
        public Builder addDefaultAnimation() {
            P.mAnimations = R.style.dialog_middle_scale_anim;
            return this;
        }

        //设置动画.
        public Builder setAnimations(int styleAnimation) {
            P.mAnimations = styleAnimation;
            return this;
        }

        //设置是否可取消
        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        public Builder setDimAmount(float amount) {
            P.mAmount = amount;
            return this;
        }

        /**
         * 第三步.
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the dialog. If no additional
         * processing is needed, {@link #show()} may be called instead to both
         * create and display the dialog.
         */
        public AlertDialog create() {
            // We can't use Dialog's 3-arg constructor with the createThemeContextWrapper param,
            // so we always have to re-set the theme
            final AlertDialog dialog = new AlertDialog(P.mContext, P.mThemeResId);//此处才真正创建出了dialog.
            //将含有参数的内部类P与它的外部Controller绑定.
            //注意, 此时的Controller对象中有dialog对象.
            //因此在P中进行主要的属性和dialog的绑定.
            P.apply(dialog.mController);
            //也有一些简单的属性直接在此和dialog绑定.
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * 第四步
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder and immediately displays the dialog.
         * <p>
         * Calling this method is functionally identical to:
         * <pre>
         *     AlertDialog dialog = builder.create();
         *     dialog.show();
         * </pre>
         */
        public AlertDialog show() {
            final AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }
}

