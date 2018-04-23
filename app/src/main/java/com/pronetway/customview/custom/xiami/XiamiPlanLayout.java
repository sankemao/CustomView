package com.pronetway.customview.custom.xiami;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.pronetway.customview.R;


/**
 * @author cginechen
 * @date 2016-12-27
 */

public class XiamiPlanLayout extends ViewGroup {
    private static final String TAG = "EventDispatchPlanLayout";
    private static final int INVALID_POINTER = -1;

    private int mHeaderViewId = 0;
    private int mTargetViewId = 0;
    private View mHeaderView;
    private View mTargetView;
    private ITargetView mTarget;

    private int mTouchSlop;

    private int mHeaderInitOffset;
    private int mHeaderCurrentOffset;
    private int mHeaderEndOffset = 0;

    private int mTargetInitOffset;
    private int mTargetCurrentOffset;
    private int mTargetEndOffset = 0;

    private int mActivePointerId = INVALID_POINTER;
    private boolean mIsDragging;
    private float mInitialDownY;
    private float mInitialMotionY;
    private float mLastMotionY;

    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;
    private int mMinVelocity;

    private Scroller mScroller;
    private boolean mNeedScrollToInitPos = false;
    private boolean mNeedScrollToEndPos = false;
    private float mInitialDownX;
    private int mHeaderViewHeight;
    private int mHeaderViewWidth;
    private int mHoverViewId;
    private View mHoverView;
    private int mHoverViewWidth;
    private int mHoverViewHeight;

    public XiamiPlanLayout(Context context) {
        this(context, null);
    }

    public XiamiPlanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.XiamiPlanLayout, 0, 0);
        mHeaderViewId = array.getResourceId(R.styleable.XiamiPlanLayout_header_view, 0);
        mTargetViewId = array.getResourceId(R.styleable.XiamiPlanLayout_target_view, 0);
        mHoverViewId = array.getResourceId(R.styleable.XiamiPlanLayout_hover_view, 0);

        mHeaderInitOffset = array.getDimensionPixelSize(R.styleable.
                XiamiPlanLayout_header_init_offset, Util.dp2px(getContext(), 20));
        mTargetInitOffset = array.getDimensionPixelSize(R.styleable.
                XiamiPlanLayout_target_init_offset, Util.dp2px(getContext(), 40));
        mHeaderCurrentOffset = mHeaderInitOffset;
        mTargetCurrentOffset = mTargetInitOffset;

        mTargetEndOffset = Util.dp2px(context, 60);
        array.recycle();

        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mTouchSlop = Util.px2dp(context, vc.getScaledTouchSlop()); //系统的值是8dp,太大了。。。

        mScroller = new Scroller(getContext());
        mScroller.setFriction(0.98f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeaderViewId != 0) {
            mHeaderView = findViewById(mHeaderViewId);
        }
        if (mTargetViewId != 0) {
            mTargetView = findViewById(mTargetViewId);
            ensureTarget();
        }
        if (mHoverViewId != 0) {
            mHoverView = findViewById(mHoverViewId);
        }
    }

    private void ensureTarget() {
        if (mTargetView instanceof ITargetView) {
            mTarget = (ITargetView) mTargetView;
        } else {
            throw new RuntimeException("TargetView should implement interface ITargetView");
        }
    }

    private void ensureHeaderViewAndScrollView() {
        if (mHeaderView != null && mTargetView != null && mHoverView != null) {
            return;
        }
        if (mHeaderView == null && mTargetView == null && mHoverView == null && getChildCount() >= 3) {
            mHoverView = getChildAt(0);
            mHeaderView = getChildAt(1);
            mTargetView = getChildAt(2);
            ensureTarget();
            return;
        }
        throw new RuntimeException("please ensure headerView and scrollView");
    }

    /**
     * 绘制顺序
     * @param childCount 子view的总个数
     * @param i 子view所在位置
     * 先绘制header, 再绘制hover，最后绘制target.
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        ensureHeaderViewAndScrollView();
        int hoverIndex = indexOfChild(mHoverView);
        int headerIndex = indexOfChild(mHeaderView);
        if (hoverIndex == i) {
            return 1;
        } else if (headerIndex == i) {
            return 0;
        } else {
            return 2;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // 去掉默认行为，使得每个事件都会经过这个Layout
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ensureHeaderViewAndScrollView();
        measureChild(mTargetView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        measureChild(mHoverView, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        ensureHeaderViewAndScrollView();

        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        mTargetView.layout(childLeft, childTop + mTargetCurrentOffset,
                childLeft + childWidth, childTop + childHeight + mTargetCurrentOffset);

        mHeaderViewWidth = mHeaderView.getMeasuredWidth();
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.layout((width / 2 - mHeaderViewWidth / 2), mHeaderCurrentOffset,
                (width / 2 + mHeaderViewWidth / 2), mHeaderCurrentOffset + mHeaderViewHeight);

        mHoverViewWidth = mHoverView.getMeasuredWidth();
        mHoverViewHeight = mHoverView.getMeasuredHeight();
        mHoverView.layout((width - mHoverViewWidth) / 2, -mHoverViewHeight, (width / 2 + mHoverViewWidth /2), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureHeaderViewAndScrollView();
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;

        //当listView可以向上滚动时（下拉），不拦截事件
        if (!isEnabled() || mTarget.canChildScrollUp()) {
            Log.d(TAG, "fast end onIntercept: isEnabled = " + isEnabled() + "; canChildScrollUp = "
                    + mTarget.canChildScrollUp());
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //记录当前活动的pointerId
                mActivePointerId = ev.getPointerId(0);
                mIsDragging = false;
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                mInitialDownX = ev.getX(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                //移动后的y坐标
                final float y = ev.getY(pointerIndex);
                final float x = ev.getX(pointerIndex);
                //该方法中根据y坐标，判断如果是下拉或target偏移量大于0（listView的下拉在该方法最开始就排除了，所以到这里只要判断是下拉，就判定为drag），
                // mIsDragging置为true,拦截事件，交由自身处理
                startDragging(y);
                if (mIsDragging) {
                    if (Math.abs(x - mInitialDownX) > Math.abs(y - mInitialDownY)) {
                        mIsDragging = false;
                    }
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                //多指
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //取消拦截事件
                mIsDragging = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsDragging;
    }

    /**
     * 下拉的时候，判断list是否能够下拉，能的话ViewGroup不拦截事件交给listView处理，否则拦截事件，事件分发不到子view，
     * 直接交给自身（下面的）的onTouchEvent处理。
     * 上拉的时候，判断mTargetCurrentOffset是否大于mTargetEndOffset, 小于等于不拦截事件交给listView处理，大于的话拦截事件，拖动targetView
     * mTargetCurrentOffset会越来越小，直至等于mTargetEndOffset，这时，事件应该由targetView处理变为listView处理（原本父View处理的事件转交给子view），
     * 系统源码逻辑是父View消费了down事件，即（mFirstTouchTarget = 父View，具体看ViewGroup源码dispatchTouchEvent方法），
     * 子view就不再有机会处理事件了，怎么办呢，这个时候我们需要主动向下派发一个down事件
     * 【ev.setAction(MotionEvent.ACTION_DOWN);dispatchTouchEvent(ev);】
     * 该down事件传递给子view（onInterceptTouchEvent中取消了事件拦截，因此该down事件能够传给子view），此时（mFirstTouchTarget = 子View）
     * 至此后续的move事件子view就可以处理了，表现在这里就是listView可以滑动了。
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;
        //target可以下拉，自身不处理，其实无所谓。
        if (!isEnabled() || mTarget.canChildScrollUp()) {
            Log.d(TAG, "fast end onTouchEvent: isEnabled = " + isEnabled() + "; canChildScrollUp = "
                    + mTarget.canChildScrollUp());
            return false;
        }

        //初始化速度追踪器
        acquireVelocityTracker(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //获取活动的pointerId
                mActivePointerId = ev.getPointerId(0);
                //状态重置
                mIsDragging = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                //判断如果下拉，或target偏移量大于0。
                startDragging(y);
                //自身开始targetView的拖动，非targetView中列表
                if (mIsDragging) {
                    float dy = y - mLastMotionY;
                    if (dy >= 0) {
                        //下拉，直接移动targetView.
                        Log.d(TAG, "YYYYY: " + y + "      "  + mLastMotionY);
                        moveTargetView(dy);
                    } else {
                        //上拉
                        if (mTargetCurrentOffset + dy <= mTargetEndOffset) {
                            //如果偏移量减去移动距离后，偏移量小于等于0，移动targetView
                            moveTargetView(dy);
                            // 重新dispatch一次down事件，使得列表可以继续滚动
                            int oldAction = ev.getAction();
                            ev.setAction(MotionEvent.ACTION_DOWN);
                            dispatchTouchEvent(ev);
                            ev.setAction(oldAction);
                        } else {
                            //否则直接移动targetView.
                            moveTargetView(dy);
                        }
                    }
                    mLastMotionY = y;
                }

                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                //初始按下坐标以及上次按下坐标都得转移到这根手指所处坐标。
                mInitialMotionY = ev.getY(pointerIndex);
                mLastMotionY = mInitialMotionY;
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsDragging) {
                    mIsDragging = false;
                    //计算瞬时速度
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    final float vy = mVelocityTracker.getYVelocity(mActivePointerId);
                    finishDrag((int) vy);
                }
                mActivePointerId = INVALID_POINTER;
                releaseVelocityTracker();
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
                return false;
        }

        return mIsDragging;
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void finishDrag(int vy) {
        Log.i(TAG, "TouchUp: vy = " + vy);
        //手指滑动起点坐标 - 手指滑动终点坐标
        //有时我们下拉fling，手指抬起瞬间有轻微的反方向滑动，导致vy<0，targetView反向fling,，加上该判断过滤这种情况
        final float diffY = mLastMotionY - mInitialMotionY;

        if (vy > 0 && diffY > 0) {
            //下拉
            mNeedScrollToInitPos = true;
            mScroller.fling(0, mTargetCurrentOffset, 0, vy, 0, 0, mTargetEndOffset, Integer.MAX_VALUE);
            invalidate();
        } else if (vy < 0 && diffY < 0) {
            //上拉
            mNeedScrollToEndPos = true;
            mScroller.fling(0, mTargetCurrentOffset, 0, vy, 0, 0, mTargetEndOffset, Integer.MAX_VALUE);
            invalidate();
        } else {
            if (mTargetCurrentOffset <= (mTargetEndOffset + mTargetInitOffset) / 2) {
                mNeedScrollToEndPos = true;
            } else {
                mNeedScrollToInitPos = true;
            }
            invalidate();
        }
    }

    private void startDragging(float y) {
        //1，下拉靠第一个条件判断；2，或者target偏移量大于0(上拉是靠第二个条件判断)；
        if (y > mInitialDownY || mTargetCurrentOffset > mTargetEndOffset) {
            final float yDiff = Math.abs(y - mInitialDownY);
            if (yDiff > mTouchSlop && !mIsDragging) {
                //初始移动的坐标
                mInitialMotionY = y;
                Log.e("startDragging: ", mInitialMotionY + " ");
                mLastMotionY = mInitialMotionY;
                mIsDragging = true;
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        if (pointerId == mActivePointerId) {
            //this was our active pointer going up.Choose a new
            // active pointer and adjust accordingly.
            actionIndex = actionIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(actionIndex);
            mInitialMotionY = event.getY(actionIndex);
            mLastMotionY = mInitialMotionY;
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void moveTargetView(float dy) {
        int target = (int) (mTargetCurrentOffset + dy);
        moveTargetViewTo(target);
    }

    private void moveTargetViewTo(int target) {
        //设定最小偏移量
        target = Math.max(target, mTargetEndOffset);
        //设定最大偏移量
        target = Math.min(target, mTargetInitOffset);
        //偏移dy
        ViewCompat.offsetTopAndBottom(mTargetView, target - mTargetCurrentOffset);
        //记录当前偏移量
        mTargetCurrentOffset = target;

        //当targetView 偏移量为0(mTargetEndOffset)的时候，hover向下偏移mHoverViewHeight
        //当targetView 偏移量为mTargetInitOffset的时候，hover偏移量为0
        if (mTargetCurrentOffset <= mTargetInitOffset && mTargetCurrentOffset >= mTargetEndOffset) {
            int total = mTargetInitOffset - mTargetEndOffset;
            float percent =  (mTargetCurrentOffset - mTargetEndOffset) * 1.0f / total;
            ViewCompat.setTranslationY(mHoverView, mHoverViewHeight * (1-percent));
        }
    }

    public interface ITargetView {
        boolean canChildScrollUp();

        void fling(float vy);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            //fling阶段
            Log.d(TAG, "computeScroll: " + "开始fling" + mScroller.getCurrY());
            int offsetY = mScroller.getCurrY();
            moveTargetViewTo(offsetY);
            invalidate();
        } else if (mNeedScrollToInitPos) {
            //fling结束后，回滚到初始位置
            mNeedScrollToInitPos = false;
            if (mTargetCurrentOffset == mTargetInitOffset) {
                return;
            }
            Log.d(TAG, "computeScroll: " + "fling结束后，回到下面");
            mScroller.startScroll(0, mTargetCurrentOffset, 0, mTargetInitOffset - mTargetCurrentOffset);
            invalidate();
        } else if (mNeedScrollToEndPos) {
            //fling结束后，回滚到顶点
            Log.d(TAG, "computeScroll: " + "fling结束后，跑到上面");
            mNeedScrollToEndPos = false;
            if (mTargetCurrentOffset == mTargetEndOffset) {
                if (mScroller.getCurrVelocity() > 0) {
                    // 如果还有速度，则传递给子view
                    mTarget.fling(-mScroller.getCurrVelocity());
                }
            }
            mScroller.startScroll(0, mTargetCurrentOffset, 0, mTargetEndOffset - mTargetCurrentOffset);
            invalidate();
        }
    }
}
