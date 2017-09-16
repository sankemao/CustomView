package com.pronetway.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jin on 2017/9/14.
 * 手指侧滑指示
 */
public class LetterSideBar extends View {

    public static String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private Paint mPaint;
    private int mTextWidth;
    private String mCurrentLetter;

    public LetterSideBar(Context context) {
        this(context, null);
    }

    public LetterSideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);
    }

    private void initPaint(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //自定义属性.
        mPaint.setTextSize(sp2px(12));
        mPaint.setColor(Color.BLUE);
    }

    private float sp2px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * 主要是宽度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽度 = 字体宽度(取决于画笔) + padding.
        mTextWidth = (int)mPaint.measureText("A");
        int width = getPaddingLeft() + getPaddingRight() + mTextWidth;

        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //每个字母的中心位置.
        int itemHeight =  (getHeight() - getPaddingTop() - getPaddingBottom()) / mLetters.length;

        for (int i = 0; i < mLetters.length; i++) {
            int letterCenterY = i * itemHeight + itemHeight / 2 + getPaddingTop();
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int dy = (int) ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
            int baseLine = letterCenterY + dy;
            int x = getWidth() / 2 - mTextWidth / 2;

            //绘制高亮
            if (mLetters[i].equals(mCurrentLetter)) {
                mPaint.setColor(Color.RED);
                canvas.drawText(mCurrentLetter, x, baseLine, mPaint);
            } else {
                mPaint.setColor(Color.BLUE);
                canvas.drawText(mLetters[i], x, baseLine, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                int itemHeight =  (getHeight() - getPaddingTop() - getPaddingBottom()) / mLetters.length;
                int currentPosition = (int) ((currentY - getPaddingTop()) / itemHeight);
                if (currentPosition < 0) {
                    currentPosition = 0;
                }

                if (currentPosition > mLetters.length - 1) {
                    currentPosition = mLetters.length - 1;
                }

                mCurrentLetter = mLetters[currentPosition];

                if (mListener != null) {
                    mListener.touch(mCurrentLetter);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }


    private LetterTouchListener mListener;

    public void setOnLetterTouchListener(LetterTouchListener listener) {
        this.mListener = listener;
    }

    public interface LetterTouchListener {
        void touch(CharSequence letter);
    }

}
