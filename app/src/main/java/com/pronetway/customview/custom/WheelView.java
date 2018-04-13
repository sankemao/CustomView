package com.pronetway.customview.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.Scroller;

import com.blankj.utilcode.util.LogUtils;
import com.pronetway.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:TODO
 * Create Time: 2018/4/9.10:08
 * Author:jin
 * Email:210980059@qq.com
 */
public class WheelView extends View {
    private static final int DEFALUT_VISIBILITY_COUNT = 5;
    private static final int AUTO_VISIBILITY_COUNT = -1;
    private static final int INVALID_POINTER = -1;
    private static final long MAXIMUM_FLING_DURATION = 600L;

    private List<String> mDataSources;
    //滑动触发最短距离
    private int mTouchSlop;

    private int mSelectPosition;
    private boolean mNeedCheckDistanceY;
    private boolean mForceSelectPosition;
    private int mVisibilityCount = AUTO_VISIBILITY_COUNT;
    private int mTextVerticalSpacing = (int) dp2px(10);
    @ColorInt
    private int mNormalTextColor = Color.LTGRAY;
    @ColorInt
    private int mSelectedTextColor = Color.BLACK;
    @ColorInt
    private int mSelectedLineColor = Color.BLACK;
    private int mTextGravity;

    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mContentRect = new Rect();

    //textRect,selectRect 都以 ContentRect 为基准
    private final Rect mTextRect = new Rect();
    private final Rect mSelectedRect = new Rect();

    private float mTextSize = sp2px(16);
    private int mMaxTextWidth;
    private int mMaxTextHeight;

    //实例池
    private static final Pools.Pool<Rect> RECT_POOL = new Pools.SimplePool<>(20);
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;
    private Scroller mScroller;
    private int mMaximumDistanceY;
    private int mMinimumDistanceY;
    private boolean mNeedCalculate;
    //selectedRect.top-第一个textRect.top
    private int mDistanceY;
    private boolean mIsBeingDraged;
    private boolean mIsBeingFling;
    private ValueAnimator mRunAnimator;
    private CallBack mCallBack;

    private VelocityTracker mVelocityTracker;
    //被拖动
    private boolean mIsBeingDragged;
    //记录手指的id.
    private int mActivePointerId = INVALID_POINTER;
    private float mDownY;
    private float mLastY;
    private long mFlingDuration = MAXIMUM_FLING_DURATION;
    private int mFlingY;
    private long mStartFlingTime;


    private static Rect acquireRect() {
        Rect rect = RECT_POOL.acquire();
        if (rect == null) {
            rect = new Rect();
        }
        return rect;
    }

    //将实例重置状态，并放回实例池。
    private static void releaseRect(Rect rect) {
        rect.setEmpty();
        RECT_POOL.release(rect);
    }


    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            mDataSources = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                mDataSources.add("测试" + i);
            }
        }

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();

        mScroller = new Scroller(context);

        init(context, attrs);
    }

    public void setSelectPosition(int position) {
        if (!hasDataSource()) {
            return;
        }
        if (position > (mDataSources.size() - 1) || position < 0) {
            return;
        }
        mSelectPosition = position;
        mNeedCheckDistanceY = true;
        mForceSelectPosition = true;
    }

    public int getSelectPosition() {
        return mSelectPosition;
    }

    public void setDataSources(List<String> dataSources) {
        if (mDataSources == dataSources) {
            return;
        }
        mDataSources = dataSources;
        mSelectPosition = 0;
        requestLayout();
        postInvalidateOnAnimation();
    }

    public void setVisibilityCount(int visibilityCount) {
        if (mVisibilityCount == visibilityCount) {
            return;
        }
        mVisibilityCount = visibilityCount;
        requestLayout();
        postInvalidateOnAnimation();
    }

    public void setTextSize(float textSize) {
        if (mTextSize == textSize) {
            return;
        }
        mTextSize = textSize;
        requestLayout();
        postInvalidateOnAnimation();
    }

    public void setTextVerticalSpacing(int textVerticalSpacing) {
        if (mTextVerticalSpacing == textVerticalSpacing) {
            return;
        }
        mTextVerticalSpacing = textVerticalSpacing;
        requestLayout();
        postInvalidateOnAnimation();
    }

    public void setNormalTextColor(@ColorInt int normalTextColor) {
        if (mNormalTextColor == normalTextColor) {
            return;
        }
        mNormalTextColor = normalTextColor;
        postInvalidateOnAnimation();
    }

    public void setSelectedTextColor(@ColorInt int selectedTextColor) {
        if (mSelectedTextColor == selectedTextColor) {
            return;
        }
        mSelectedTextColor = selectedTextColor;
        postInvalidateOnAnimation();
    }

    public void setSelectedLineColor(@ColorInt int selectedLineColor) {
        if (mSelectedLineColor == selectedLineColor) {
            return;
        }
        mSelectedLineColor = selectedLineColor;
        postInvalidateOnAnimation();
    }

    public void setTextGravity(int textGravity) {
        if (mTextGravity == textGravity) {
            return;
        }
        mTextGravity = textGravity;
        postInvalidateOnAnimation();
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WheelView);

        try {
            setTextSize(ta.getDimension(R.styleable.WheelView_wheelTextSize, mTextSize));
            setTextVerticalSpacing(ta.getDimensionPixelSize(
                    R.styleable.WheelView_textVerticalSpacing,
                    mTextVerticalSpacing
            ));
            setNormalTextColor(ta.getColor(
                    R.styleable.WheelView_normalTextColor,
                    mNormalTextColor
            ));
            setSelectedTextColor(ta.getColor(
                    R.styleable.WheelView_selectedTextColor,
                    mSelectedTextColor
            ));
            setSelectedLineColor(ta.getColor(
                    R.styleable.WheelView_selectedLineColor,
                    mSelectedLineColor
            ));
            setTextGravity(ta.getInt(R.styleable.WheelView_textGravity, mTextGravity));
            setSelectPosition(ta.getInt(
                    R.styleable.WheelView_selectPosition,
                    mSelectPosition
            ));
            setVisibilityCount(ta.getInt(
                    R.styleable.WheelView_visibilityCount,
                    mVisibilityCount
            ));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    /**
     * 测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wantWidth = getPaddingLeft() + getPaddingRight();
        int wantHeight = getPaddingBottom() + getPaddingTop();
        calculateTextSize();
        wantWidth += mTextRect.width();
        if (mVisibilityCount > 0) {
            wantHeight += mTextRect.height() * mVisibilityCount;
        } else {
            wantHeight += mTextRect.height() * DEFALUT_VISIBILITY_COUNT;
        }
        setMeasuredDimension(
                resolveSize(wantWidth, widthMeasureSpec),
                resolveSize(wantHeight, heightMeasureSpec)
        );
        mNeedCalculate = true;
    }

    /**
     * 计算文字大小,给textRect赋值
     */
    private void calculateTextSize() {
        mMaxTextWidth = 0;
        mMaxTextHeight = 0;
        if (!hasDataSource()) {
            return;
        }
        mTextPaint.setTextSize(mTextSize);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int maxTextHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        for (String text : mDataSources) {
            mMaxTextWidth = (int) Math.max(mTextPaint.measureText(text), mMaxTextWidth);
        }
        mTextRect.set(0, 0, mMaxTextWidth, maxTextHeight + 2 * mTextVerticalSpacing);
        calculateDistanceY();
    }

    /**
     * 最小偏移距离0
     * 最大偏移距离所有文字高度-1
     */
    private void calculateDistanceY() {
        mMaximumDistanceY = 0;
        mMinimumDistanceY = 0;
        if (!hasDataSource()) {
            return;
        }
        mMaximumDistanceY = mTextRect.height() * (mDataSources.size() - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mNeedCalculate) {
            mNeedCalculate = false;
            calculate();
        }

        //当调用了setSelectPosition()设置新的position后
        if (mNeedCheckDistanceY) {
            mNeedCheckDistanceY = false;
            int newDistanceY = mTextRect.height() * mSelectPosition;
            animChangeDistanceY(newDistanceY);
        }

        canvas.clipRect(mContentRect);

        if (hasDataSource()) {
            int selectPosition = Math.max(0, mDistanceY / mTextRect.height());
            int remainder = mDistanceY % mTextRect.height();
            if (remainder > mTextRect.height() / 2) {
                selectPosition++;
            }
            selectPosition = Math.min(selectPosition, mDataSources.size() - 1);
            //不在拖动，不再fling, 和上次选择不同， 动画不再执行时触发
            if (!mIsBeingDraged && !mIsBeingFling && mSelectPosition != selectPosition && (mRunAnimator == null || !mRunAnimator.isRunning())) {
                if (mCallBack != null) {
                    mCallBack.onPositionSelect(selectPosition);
                }
                mSelectPosition = selectPosition;
            }
            //绘制条目总数量，可见数量+2
            final int drawCount = mContentRect.height() / mTextRect.height() + 2;
            int invisibleCount = 0;
            int dy = -mDistanceY;
            //这里所有的计算偏移都以 ContentRect 为基准
            if (mDistanceY > mSelectedRect.top) {
                //偏移距离>选择框顶部以上距离，说明有条目看不见了
                invisibleCount = (mDistanceY - mSelectedRect.top) / mTextRect.height();
                //画布平移为 = 总距离 - 看不见条目的总距离。
                dy = -(mDistanceY - invisibleCount * mTextRect.height());
            }

            int saveCount = canvas.save();
            //padding top
            canvas.translate(mContentRect.left, mContentRect.top);
            canvas.translate(0, dy);
            canvas.translate(0, mSelectedRect.top);
            for (int i = 0; (i < drawCount && (i < mDataSources.size() - invisibleCount)); i++) {
                final int position = invisibleCount + i;
                String text = mDataSources.get(position);
                if (i > 0) {
                    canvas.translate(0, mTextRect.height());
                }

                final PointF pointF = calculateTextGravity(text);
                mTextPaint.setTextSize(mTextSize);
                if (position == selectPosition) {
                    mTextPaint.setColor(mSelectedTextColor);
                } else {
                    mTextPaint.setColor(mNormalTextColor);
                }
                canvas.drawText(text, pointF.x, pointF.y, mTextPaint);
            }
            canvas.restoreToCount(saveCount);
        }

        int saveCount = canvas.save();
        mDrawPaint.setColor(mSelectedLineColor);
        canvas.translate(mContentRect.left, mContentRect.top);
        //绘制选中框两条线
        canvas.drawLine(mSelectedRect.left, mSelectedRect.top, mSelectedRect.right, mSelectedRect.top, mDrawPaint);
        canvas.drawLine(mSelectedRect.left, mSelectedRect.bottom, mSelectedRect.right, mSelectedRect.bottom, mDrawPaint);
        canvas.restoreToCount(saveCount);
    }

    /**
     * 计算文字绘制起点坐标
     */
    private PointF calculateTextGravity(String text) {
        PointF pointF = new PointF();
        final Rect textSizeRect = acquireRect();
        mTextPaint.getTextBounds(text, 0, text.length(), textSizeRect);
        switch (mTextGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                pointF.y = mTextRect.top + textSizeRect.height() - Math.abs(textSizeRect.bottom);
                break;
            case Gravity.BOTTOM:
                pointF.y = textSizeRect.bottom;
                break;
            default:
            case Gravity.CENTER_VERTICAL:
                pointF.y = mTextRect.exactCenterY() + textSizeRect.height() / 2f
                        - Math.abs(textSizeRect.bottom);
                break;
        }
        switch (mTextGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
            case Gravity.START:
                pointF.x = 0;
                break;
            case Gravity.END:
            case Gravity.RIGHT:
                pointF.x = mTextRect.right - textSizeRect.width();
                break;
            default:
            case Gravity.CENTER_HORIZONTAL:
                pointF.x = mTextRect.exactCenterX() - textSizeRect.width() / 2f;
                break;
        }
        releaseRect(textSizeRect);
        return pointF;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        if (!hasDataSource()) {
            return super.onTouchEvent(event);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                initOrResetVelocityTracker();
                mIsBeingFling = false;
                mScroller.computeScrollOffset();
                //如果scroller还在模拟滚动，那么就让mIsBeingDragged = true;
                if (mIsBeingDragged = !mScroller.isFinished()) {
                    //停止滚动
                    mScroller.abortAnimation();
                }

                if (mRunAnimator != null) {
                    mRunAnimator.cancel();
                }

                if (mIsBeingDragged) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        //当scroller自动滚动是再次按下，请求父控件不拦截事件
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                //保存pointerId
                mActivePointerId = event.getPointerId(0);
                //记录此刻位置
                mDownY = event.getY(0);
                mLastY = mDownY;

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //非第一个手指按下时
                final int actionIndex = event.getActionIndex();
                mActivePointerId = event.getPointerId(actionIndex);
                mDownY = event.getY(actionIndex);
                mLastY = mDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = event.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }
                //获取当前位置
                final float moveY = event.getY(pointerIndex);
                //第一次按下，且手指拖动距离触发
                if (!mIsBeingDragged && Math.abs(mDownY - moveY) > mTouchSlop) {
                    mIsBeingDragged = true;
                    ViewParent parent = getParent();
                    if (parent != null) {
                        //请求父控件不拦截
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                //继续拖动
                if (mIsBeingDragged) {
                    //distanceY 加上diff距离
                    mDistanceY += mLastY - moveY;
                    //边界控制
                    if (mDistanceY > mMaximumDistanceY) {
                        mDistanceY = mMaximumDistanceY;
                    }
                    if (mDistanceY < mMinimumDistanceY) {
                        mDistanceY = mMinimumDistanceY;
                    }
                    //改变distanceY，触发重绘
                    postInvalidateOnAnimation();
                }
                //继续记录此刻位置
                mLastY = moveY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //非最后一根手指离开
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                //获取手指抬起的瞬时速度
                int initialVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);
                //大于一定速度启用fling
                if (Math.abs(initialVelocity) > mMinimumFlingVelocity) {
                    //根据速度获取fling时长
                    mFlingDuration = Math.max(MAXIMUM_FLING_DURATION, getSplineFlingDuration(initialVelocity));
                    mScroller.fling(0, 0, 0, initialVelocity, 0, 0, -mMaximumDistanceY, mMaximumDistanceY);
                    //获取fling瞬间的Y坐标
                    mFlingY = mScroller.getStartY();
                    //优化fling时长，边界
                    if (Math.abs(mMaximumDistanceY - mDistanceY) < getHeight()) {
                        LogUtils.e("mMaximumDistanceY:" + mMaximumDistanceY + "  " + "mDistanceY: " + mDistanceY);
                        mFlingDuration = mFlingDuration / 3;
                    }
                    mIsBeingFling = true;
                    mStartFlingTime = SystemClock.elapsedRealtime();
                    postInvalidateOnAnimation();
                } else {
                    //手指抬起时，速度小时触发
                    correctionDistanceY();
                }
                mActivePointerId = INVALID_POINTER;
                mIsBeingDragged = false;
                resetVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                //前置事件被拦截，一切都复原
                mIsBeingFling = false;
                mActivePointerId = INVALID_POINTER;
                mIsBeingDragged = false;
                resetVelocityTracker();
                correctionDistanceY();
                break;
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        return true;
    }

    private void correctionDistanceY() {
        if (mDistanceY % mTextRect.height() != 0) {
            int position = mDistanceY / mTextRect.height();
            int remainder = mDistanceY % mTextRect.height();
            if (remainder >= mTextRect.height() / 2f) {
                position++;
            }
            int newDistanceY = position * mTextRect.height();
            animChangeDistanceY(newDistanceY);
        }
    }

    /**
     * 重置瞬时速度计算
     */
    private void resetVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    /**
     * 倒数第二根手指离开。
     */
    private void onSecondaryPointerUp(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        if (pointerId == mActivePointerId) {
            //this was our active pointer going up.Choose a new
            // active pointer and adjust accordingly.
            actionIndex = actionIndex == 0 ? 1 : 0;
            mActivePointerId = event.getPointerId(actionIndex);
            mDownY = event.getY(actionIndex);
            mLastY = mDownY;
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    /**
     * 动画滚动到指定position位置
     *
     * @param newDistanceY 指定位置与起始位置之间的距离
     */
    private void animChangeDistanceY(int newDistanceY) {
        if (newDistanceY > mMaximumDistanceY) {
            newDistanceY = mMaximumDistanceY;
        }
        if (newDistanceY < mMinimumDistanceY) {
            newDistanceY = mMinimumDistanceY;
        }
        if (newDistanceY != mDistanceY) {
            if (mRunAnimator != null && mRunAnimator.isRunning()) {
                mRunAnimator.cancel();
            }
            mRunAnimator = ValueAnimator.ofInt(mDistanceY, newDistanceY);
            mRunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDistanceY = (int) animation.getAnimatedValue();
                    postInvalidateOnAnimation();
                }
            });
            mRunAnimator.start();
        }
    }

    /**
     * 在确定了ContentRect的尺寸后，再次确定每格文字尺寸mTextRect
     */
    private void calculate() {
        mContentRect.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        if (mVisibilityCount > 0) {
            mTextRect.set(0, 0, mContentRect.width(), (int) (mContentRect.height() * 1.0 / mVisibilityCount));
        } else {
            mTextRect.set(0, 0, mContentRect.width(), mMaxTextHeight + 2 * mTextVerticalSpacing);
        }

        final int contentCenterY = mContentRect.centerY();
        int position = contentCenterY / mTextRect.height();
        if (contentCenterY % mTextRect.height() > 0) {
            position++;
        }
        mSelectedRect.set(
                0,
                mTextRect.height() * (position - 1),
                mContentRect.width(),
                mTextRect.height() * position
        );
        calculateDistanceY();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int currY = mScroller.getCurrY();
            //不断修改mDistanceY并触发重绘从而滚动。
            mDistanceY += mFlingY - currY;
            if (mDistanceY > mMaximumDistanceY) {
                mDistanceY = mMaximumDistanceY;
            }
            if (mDistanceY < mMinimumDistanceY) {
                mDistanceY = mMinimumDistanceY;
            }
            mFlingY = currY;
            if ((SystemClock.elapsedRealtime() - mStartFlingTime) >= mFlingDuration || currY == mScroller.getFinalY()) {
                //duration or current == final
                mScroller.abortAnimation();
            }
            postInvalidateOnAnimation();
        } else if(mIsBeingFling){
            //scroller不再自动滚动后，fling状态置为false, 并修正偏差
            mIsBeingFling = false;
            correctionDistanceY();
        }
    }

    public interface CallBack {
        void onPositionSelect(int position);
    }

    /////////////////////////////////////////////////////////////////
    // HELP METHOD
    /////////////////////////////////////////////////////////////////

    private boolean hasDataSource() {
        return mDataSources != null && !mDataSources.isEmpty();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //给filling使用。
        final float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
        mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f; // look and feel tuning
    }

    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)

    // A context-specific coefficient adjusted to physical values.
    private float mPhysicalCoeff;

    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));

    // Fling friction
    private float mFlingFriction = ViewConfiguration.getScrollFriction();

    /* Returns the duration, expressed in milliseconds */
    private int getSplineFlingDuration(int velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return (int) (1000.0 * Math.exp(l / decelMinusOne));
    }

    private double getSplineDeceleration(float velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics()
        );
    }
}
