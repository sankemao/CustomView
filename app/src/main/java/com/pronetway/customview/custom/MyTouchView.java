package com.pronetway.customview.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;


/**
 * Description:TODO
 * Create Time: 2018/4/19.10:24
 * Author:jin
 * Email:210980059@qq.com
 */
public class MyTouchView extends View {

    private int mActivePointerId;
    private float mDownX;
    private float mLastX;
    private float mDownY;
    private float mLastY;
    private int mTouchSloplop;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private int mL;
    private int mR;
    private int mT;
    private int mB;

    /**
     * 记录是否拖动过
     */
    private boolean hasDragged;

    public MyTouchView(Context context) {
        this(context, null);
    }

    public MyTouchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSloplop = ConvertUtils.px2dp(viewConfiguration.getScaledTouchSlop());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();

    }

    int lastX;
    int lastY;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("++++++View ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d("++++++View ACTION_MOVE");

                return false;
            case MotionEvent.ACTION_UP:
                LogUtils.d("++++++View ACTION_UP");

                return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("++++++View  dispatchTouchEvent ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d("++++++View  dispatchTouchEvent ACTION_MOVE");

                return false;
            case MotionEvent.ACTION_UP:
                LogUtils.d("++++++View  dispatchTouchEvent ACTION_UP");

                return false;
        }
        return super.dispatchTouchEvent(event);
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int pointerIndex;
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                mActivePointerId = event.getPointerId(0);
//                mDownX = event.getRawX();
//                mLastX = mDownX;
//
//                mDownY = event.getRawY();
//                mLastY = mDownY;
//                //onTouchEvent，只要父控件不拦截，那么一定会走到action_down的判断，
//                //至于后续的action_move以及action_up等事件是否消费，
//                //取决于此处返回值，返回true, 则后续事件能消费，否则不处理后续事件。
//                break;
//            case MotionEvent.ACTION_MOVE:
//                pointerIndex = event.findPointerIndex(mActivePointerId);
//                final float x = event.getRawX();
//                final float y = event.getRawY();
//
//                float diffX = x - mLastX;
//                float diffY = y - mLastY;
//                if (Math.abs(diffX) > mTouchSloplop || Math.abs(diffY) > mTouchSloplop) {
//                    moveTargetBy(diffX, diffY);
//                }
//                mLastX = x;
//                mLastY = y;
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return true;
//    }

    private void moveTargetBy(float dx, float dy) {
        //方式1，不要用这种方式，该方法对dx做了不能两次相同的判断。dx是相对于起点而不是上一次的。
//        ViewCompat.setTranslationX(this, dx);
//        ViewCompat.setTranslationY(this, dy);

        //方式2，dx，对于ViewGroup, dx为event.getX() - mLastX;
        //对于View, dx应该为event.getX() - mDownX; 或者dx为event.getRawX() - mLastX(event.getRawX)
        ViewCompat.offsetLeftAndRight(this, (int) dx);
        ViewCompat.offsetTopAndBottom(this, (int) dy);

        //方式3，layout
//        mL = (int) (getLeft() + dx);
//        mR = mL + mMeasuredWidth;
//        mT = (int) (getTop() + dy);
//        mB = mT + mMeasuredHeight;
//        this.layout(mL, mT, mR, mB);
//
//        if (!hasDragged) {
//            hasDragged = true;
//        }
    }

    /**
     * 对于view来讲，需要在layout中判断是否拖动过，防止View刷新后，view回到原先的位置。
     * 对于ViewGroup, 在onLayout中判断即可。
     */
    @Override
    public void layout(int l, int t, int r, int b) {
        if (hasDragged) {
            l = mL;
            t = mT;
            r = mR;
            b = mB;
        }
        super.layout(l, t, r, b);
    }
}
