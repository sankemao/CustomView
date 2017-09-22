package com.pronetway.customview.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.pronetway.customview.R;

/**
 * Created by jin on 2017/9/21.
 *
 */
public class SubmitButton extends View {
    private static final int STATE_NONE = 0x0011;
    private static final int STATE_SUBMIT = 0x0012;
    private static final int STATE_LOADING = 0x0013;
    private static final int STATE_RESULT = 0x0014;
    private int viewState = STATE_NONE;

    private static final int STYLE_LOADING = 0;
    private static final int STYLE_PROGRESS = 1;

    //按钮默认颜色.
    private int mButtonColor = Color.parseColor("#19CC95");
    //成功的颜色.
    private int mSucceedColor = Color.parseColor("#19CC95");
    //失败的颜色.
    private int mFailedColor = Color.parseColor("#FC8E34");
    //按钮文字大小
    private int mTextSize;
    //样式
    private int mProgressStyle;
    //文字
    private String mButtonText = "";
    private Paint mBgPaint;
    private Paint mLoadingPaint;
    private Paint mTextPaint;
    private Paint mResultPaint;
    private Path mButtonPath;
    private Path mLoadPath;
    private Path mResultPath;
    private Path mDstPath;
    private RectF mCircleLeft;
    private RectF mCircleRight;
    private RectF mCircleMid;
    private PathMeasure mPathMeasure;
    private int mTextHeight;
    private int mTextWidth;
    private int mBaseLine;

    private int x, y;
    private int mWidth, mHeight;
    private int MAX_WIDTH;
    private int MAX_HEIGHT;

    private boolean mIsSucceed;
    private boolean mIsDoResult;

    private float mloadValue;
    private float mCurrentProgress;
    private ValueAnimator mLoadingAnim;
    private ValueAnimator mSubmitAnim;
    private ValueAnimator mResultAnim;

    private OnResultEndListener mListener;

    public SubmitButton(Context context) {
        this(context, null);
    }

    public SubmitButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubmitButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubmitButton);
        mButtonColor = a.getColor(R.styleable.SubmitButton_bgColor, mButtonColor);
        mSucceedColor = a.getColor(R.styleable.SubmitButton_succeedColor, mSucceedColor);
        mFailedColor = a.getColor(R.styleable.SubmitButton_failedColor, mFailedColor);
        mTextSize = (int) a.getDimension(R.styleable.SubmitButton_buttonTextSize, sp2px(14));
        mProgressStyle = a.getInt(R.styleable.SubmitButton_progressStyle, STYLE_LOADING);
        if (!TextUtils.isEmpty(a.getString(R.styleable.SubmitButton_buttonText))) {
            mButtonText = a.getString(R.styleable.SubmitButton_buttonText);
        }
        a.recycle();
        this.setLayerType(LAYER_TYPE_SOFTWARE,null);

        initPaint();
        initPath();
        measureText();
    }

    private void initPaint() {
        //背景画笔
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mButtonColor);
//        mBgPaint.setStyle(Paint.Style.STROKE);
//        mBgPaint.setStrokeWidth(9);

        //loading画笔
        mLoadingPaint = new Paint();
        mLoadingPaint.setAntiAlias(true);
        mLoadingPaint.setColor(mButtonColor);
        mLoadingPaint.setStyle(Paint.Style.STROKE);
        mLoadingPaint.setStrokeWidth(10);

        mResultPaint = new Paint();
        mResultPaint.setAntiAlias(true);
        mResultPaint.setColor(Color.WHITE);
        mResultPaint.setStyle(Paint.Style.STROKE);
        mResultPaint.setStrokeWidth(10);
        mResultPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
    }

    private void initPath() {
        mButtonPath = new Path();
        mLoadPath = new Path();
        mResultPath = new Path();

        mDstPath = new Path();
        mCircleLeft = new RectF();
        mCircleRight = new RectF();
        mCircleMid = new RectF();
        mPathMeasure = new PathMeasure();
    }

    /**
     * 测量字体宽高以及baseLine.
     */
    private void measureText() {
        mTextWidth = (int) mTextPaint.measureText(mButtonText);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextHeight = (int) (metrics.bottom - metrics.top);
        mBaseLine = (int) -metrics.top;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        if (wMode == MeasureSpec.AT_MOST) {
            wSize = mTextWidth + 100;
        }

        if (hMode == MeasureSpec.AT_MOST) {
            hSize = (int) (mTextHeight * 2.5);
        }

        if (hSize > wSize) {
            hSize = (int) (wSize * 0.25);
        }

        x = wSize / 2;
        y = hSize / 2;

        mWidth = wSize - 10;
        mHeight = hSize - 10;
        MAX_WIDTH = wSize - 10;
        MAX_HEIGHT = hSize - 10;

        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //平移坐标
        canvas.translate(x, y);
        drawButton(canvas);
        if (viewState == STATE_NONE || viewState == STATE_SUBMIT && mWidth > mTextWidth) {
            drawButtonText(canvas);
        }
        if (viewState == STATE_LOADING) {
            drawLoading(canvas);
        }
        if (viewState == STATE_RESULT) {
            drawResult(canvas);
        }
    }

    /**
     * 绘制结果, 勾或叉
     */
    private void drawResult(Canvas canvas) {
        if (mIsSucceed) {
            mResultPath.moveTo(-mHeight / 6, 0);
            mResultPath.lineTo(0, (float) (-mHeight / 6 + (1 + Math.sqrt(5)) * mHeight / 12));
            mResultPath.lineTo(mHeight / 6, -mHeight / 6);
        } else {
            mResultPath.moveTo(-mHeight / 6, mHeight / 6);
            mResultPath.lineTo(mHeight / 6, -mHeight / 6);
            mResultPath.moveTo(-mHeight / 6, -mHeight / 6);
            mResultPath.lineTo(mHeight / 6, mHeight / 6);
        }
        canvas.drawPath(mResultPath, mResultPaint);
    }

    /**
     * 绘制loading.
     */
    private void drawLoading(Canvas canvas) {
        mDstPath.reset();
        mCircleMid.set(-MAX_HEIGHT / 2, -MAX_HEIGHT / 2, MAX_HEIGHT / 2, MAX_HEIGHT / 2);
        mLoadPath.addArc(mCircleMid, 270, 359.999f);
        mPathMeasure.setPath(mLoadPath, true);
        float startD = 0f, stopD;
        if (mProgressStyle == STYLE_LOADING) {
            startD = mPathMeasure.getLength() * mloadValue;
            stopD = startD + mPathMeasure.getLength() / 2 * mloadValue;
        } else {
            stopD = mPathMeasure.getLength() * mCurrentProgress;
        }
        //截取.
        mPathMeasure.getSegment(startD, stopD, mDstPath, true);
        canvas.drawPath(mDstPath, mLoadingPaint);
    }

    /**
     * 绘制按钮.
     */
    private void drawButton(Canvas canvas) {
        mButtonPath.reset();
        mCircleLeft.set(-mWidth / 2, -mHeight / 2, -mWidth / 2 + mHeight, mHeight / 2);
        mButtonPath.arcTo(mCircleLeft, 90, 180);
        mButtonPath.lineTo(mWidth / 2 - mHeight / 2, - mHeight / 2);
        mCircleRight.set(mWidth / 2 - mHeight, -mHeight / 2, mWidth / 2, mHeight / 2);
        mButtonPath.arcTo(mCircleRight, 270, 180);
        mButtonPath.close();
        canvas.drawPath(mButtonPath, mBgPaint);
    }

    /**
     * 绘制文字
     * @param canvas
     */
    private void drawButtonText(Canvas canvas) {
        mTextPaint.setAlpha(((mWidth - mTextWidth) * 255) / (MAX_WIDTH - mTextWidth));
        canvas.drawText(mButtonText, - mTextWidth / 2, - mTextHeight / 2 + mBaseLine, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (viewState == STATE_NONE) {
                    startSubmitAnim();
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 动画开始, 按钮从两边向中间收缩至圆形.
     */
    private void startSubmitAnim() {
        viewState = STATE_SUBMIT;
        mSubmitAnim = ValueAnimator.ofInt(MAX_WIDTH, MAX_HEIGHT);
        mSubmitAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWidth = (int) animation.getAnimatedValue();
                if (mWidth == mHeight) {
                    mBgPaint.setStyle(Paint.Style.STROKE);
                    mBgPaint.setStrokeWidth(6);
                    mBgPaint.setColor(Color.parseColor("#DDDDDD"));
                }
                invalidate();
            }
        });
        mSubmitAnim.setDuration(300);
        mSubmitAnim.setInterpolator(new AccelerateInterpolator());
        mSubmitAnim.start();
        mSubmitAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mIsDoResult) {
                    startResultAnim();
                } else {
                    startLoadingAnim();
                }
            }
        });
    }

    /**
     * loading
     */
    private void startLoadingAnim() {
        viewState = STATE_LOADING;
        if (mProgressStyle == STYLE_PROGRESS) {
            return;
        }
        mLoadingAnim = ValueAnimator.ofFloat(0.0f, 1.0f);
        mLoadingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mloadValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mLoadingAnim.setDuration(2000);
        mLoadingAnim.setRepeatCount(ValueAnimator.INFINITE);
        mLoadingAnim.start();
    }

    /**
     * 动画结束, 绘制勾或叉, 改变背景
     */
    private void startResultAnim() {
        viewState = STATE_RESULT;
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        mResultAnim = ValueAnimator.ofInt(MAX_HEIGHT, MAX_WIDTH);
        mResultAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWidth = (int) animation.getAnimatedValue();
                mResultPaint.setAlpha(((mWidth - mHeight) * 255) / (MAX_WIDTH - MAX_HEIGHT));
                if (mWidth == mHeight) {
                    if (mIsSucceed) {
                        mBgPaint.setColor(mSucceedColor);
                    } else {
                        mBgPaint.setColor(mFailedColor);
                    }
                    mBgPaint.setStyle(Paint.Style.FILL);
                }
                invalidate();
            }
        });
        mResultAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener == null) {
                    return;
                }
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onResultEnd();
                    }
                }, 500);
            }
        });
        mResultAnim.setDuration(300);
        mResultAnim.setInterpolator(new AccelerateInterpolator());
        mResultAnim.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSubmitAnim != null) {
            mSubmitAnim.cancel();
        }
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mResultAnim != null) {
            mResultAnim.cancel();
        }
    }

    /**
     * 设置submit结果
     *
     * @param isSucceed 是否成功
     */
    public void doResult(boolean isSucceed) {
        if (viewState == STATE_NONE || viewState == STATE_RESULT || mIsDoResult) {
            return;
        }
        mIsDoResult = true;
        this.mIsSucceed = isSucceed;
        if (viewState == STATE_LOADING) {
            startResultAnim();
        }
    }

    /**
     * 设置动画结束回调接口
     *
     * @param listener
     */
    public void setOnResultEndListener(OnResultEndListener listener) {
        this.mListener = listener;
    }

    /**
     * 结果动画结束回调接口
     */
    public interface OnResultEndListener {
        void onResultEnd();
    }

    /**
     * 恢复初始化Button状态
     */
    public void reset() {
        if (mSubmitAnim != null) {
            mSubmitAnim.cancel();
        }
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mResultAnim != null) {
            mResultAnim.cancel();
        }
        viewState = STATE_NONE;
        mWidth = MAX_WIDTH;
        mHeight = MAX_HEIGHT;
        mIsSucceed = false;
        mIsDoResult = false;
        mCurrentProgress = 0;
        initPaint();
        initPath();
        invalidate();
    }

    /**
     * 设置进度
     *
     * @param progress 进度值 (0-100)
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            return;
        }
        mCurrentProgress = (float) (progress * 0.01);
        if (mProgressStyle == STYLE_PROGRESS && viewState == STATE_LOADING) {
            invalidate();
        }
    }


    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private float dp2px(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}
