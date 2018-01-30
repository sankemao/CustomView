package com.pronetway.customview.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.pronetway.customview.R;

/**
 * Description:TODO
 * Create Time: 2018/1/30.8:50
 * Author:jin
 * Email:210980059@qq.com
 */
public class WaveViewBySin extends View {
    private static final String TAG = "WaveViewBySin";
    //画笔
    private Paint mPaint;
    //线条颜色
    private int mWaveColor = 0xaaFF7E37;
    //绘制路径
    private Path mPath;
    //角速度
    private double ω;
    //初相
    private float φ;
    //速度
    private float mSpeed = 3f;
    //振幅A，偏距K
    private int A, K;
    //开始相位
    private float mStartPeriod;
    //是否自动开启动画
    private boolean mWaveAutoStart;
    private ValueAnimator mVa;

    public WaveViewBySin(Context context) {
        this(context, null);
    }

    public WaveViewBySin(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveViewBySin(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveViewBySin);
        mWaveColor = ta.getColor(R.styleable.WaveViewBySin_wave_color, mWaveColor);
        K = A = ta.getDimensionPixelSize(R.styleable.WaveViewBySin_wave_amplitude, K);
        mStartPeriod = ta.getFloat(R.styleable.WaveViewBySin_wave_start_period, 0);
        mWaveAutoStart = ta.getBoolean(R.styleable.WaveViewBySin_wave_auto_start, mWaveAutoStart);
        ta.recycle();
    }

    /**
     * 画笔初始化
     */
    private void initPaint() {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mWaveColor);
    }

    /**
     * onMeasure之后执行
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ω = 2 * Math.PI / getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSin(canvas);
    }


    private void drawSin(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(0, getHeight());
        float y;

        for (int x = 0; x <= getWidth(); x += 20) {
            y = (float) (A * Math.sin(ω * x + φ + Math.PI * mStartPeriod) + K);
            mPath.lineTo(x, getHeight() - y);
        }

        mPath.lineTo(getWidth(), 0);
        mPath.lineTo(0, 0);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 动画处理，不停的改变φ并重绘
     */
    private void initAnimator() {
        mVa = ValueAnimator.ofFloat(0, getWidth());
        mVa.setDuration(1000);
        mVa.setRepeatCount(ValueAnimator.INFINITE);
        mVa.setInterpolator(new LinearInterpolator());
        mVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //左加右减
                φ -= mSpeed / 100;
                invalidate();
            }
        });
        if (mWaveAutoStart) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (!mVa.isRunning()) {
                        mVa.start();
                    }
                }
            });
        }
    }

    public void startAnimator() {
        if (mVa != null) {
            if (!mVa.isRunning()) {
                mVa.start();
            }
        } else {
            initAnimator();
        }
    }

    public void stopAnimator() {
        if (mVa != null) {
            mVa.cancel();
            mVa = null;
        }
    }

    /**
     * 暂停动画
     */
    public void pauseAnimator() {
        if (mVa != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mVa.pause();
            }
        }
    }

    /**
     * 恢复动画
     */
    public void resumeAnimator() {
        if (mVa != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mVa.resume();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: ");
        startAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
        stopAnimator();
    }
}
