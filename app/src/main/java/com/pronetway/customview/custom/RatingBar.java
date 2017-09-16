package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.pronetway.customview.R;

/**
 * Created by jin on 2017/9/12.
 * 自定义五星评分控件.
 */
public class RatingBar extends View {

    private Bitmap mStarNormalBitmap;
    private Bitmap mStarFocusBitmap;
    private int mGradeNumber = 5;
    private float mStarPadding;
    private int mStarFinalSize;

    private int mCurrentGrade;
    private Bitmap mDstStarFocusBitmap;
    private Bitmap mDstStarNormalBitmap;

    public RatingBar(Context context) {
        this(context, null);
    }

    public RatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatingBar);
        int starNormalId = a.getResourceId(R.styleable.RatingBar_startNormal, 0);
        int starFocusId = a.getResourceId(R.styleable.RatingBar_starFocus, 0);

        if (starFocusId == 0 || starNormalId == 0) {
            throw new IllegalStateException("请设置属性 starNormalId, starFocusId");
        }

        mStarNormalBitmap = BitmapFactory.decodeResource(getResources(), starNormalId);
        mStarFocusBitmap = BitmapFactory.decodeResource(getResources(), starFocusId);
        mGradeNumber = a.getInt(R.styleable.RatingBar_gradeNumber, mGradeNumber);
        if (mGradeNumber < 1) {
            throw new IllegalStateException("星星个数必须大于0");
        }
        mStarPadding = a.getDimension(R.styleable.RatingBar_starPadding, dp2px(12));
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mStarFinalSize = getStarFinalSize(mStarNormalBitmap.getWidth(), widthMeasureSpec);

        int width = mStarFinalSize * mGradeNumber + getPaddingLeft() * 2 + (int) (mStarPadding * (mGradeNumber - 1));

        int height = mStarFinalSize + 2 * getPaddingTop();

        Matrix matrix = new Matrix();
        matrix.postScale(mStarFinalSize / mStarNormalBitmap.getWidth(), mStarFinalSize / mStarNormalBitmap.getHeight());
        mDstStarFocusBitmap = Bitmap.createBitmap(mStarFocusBitmap, 0, 0, mStarFocusBitmap.getWidth(), mStarFocusBitmap.getHeight(), matrix, true);
        mDstStarNormalBitmap = Bitmap.createBitmap(mStarNormalBitmap, 0, 0, mStarFocusBitmap.getWidth(), mStarFocusBitmap.getHeight(), matrix, true);

        setMeasuredDimension(width, height);
    }

    private int getStarFinalSize(int defaultSize, int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = (MeasureSpec.getSize(measureSpec) - 2 * getPaddingLeft() - (int) ((mGradeNumber - 1) * mStarPadding)) / mGradeNumber;

        int finalSize = defaultSize;

        switch (mode) {
            //未指定大小, 直接用自身大小.
            case MeasureSpec.UNSPECIFIED:
                break;
            //尽可能的大, 但最大不能超过父控件给他的空间.
            case MeasureSpec.AT_MOST:
                finalSize = Math.min(defaultSize, size);
                break;
            //精确值
            case MeasureSpec.EXACTLY:
                finalSize = size;
                break;
        }
        return finalSize;
    }

    private float dp2px(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mGradeNumber; i++) {
            float startD = getPaddingLeft() + mStarFinalSize * i + mStarPadding * i;
            if (mCurrentGrade > i) {
                canvas.drawBitmap(mDstStarFocusBitmap, startD, getPaddingTop(), null);
            } else {
                canvas.drawBitmap(mDstStarNormalBitmap, startD, getPaddingTop(), null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();//获取相对于当前控件的位置.
                int currentGrade = (int) ((moveX - getPaddingLeft()) / (mStarFinalSize + mStarPadding / 2) + 1);
                if (currentGrade < 0) {
                    currentGrade = 0;
                }
                if (currentGrade > mGradeNumber) {
                    currentGrade = mGradeNumber;
                }
                LogUtils.d("当前的分数为: "+ currentGrade);
                // 分数相同的情况下不要绘制了 , 尽量减少onDraw()的调用
                if(currentGrade == mCurrentGrade){
                    return true;
                }
                mCurrentGrade = currentGrade;
                invalidate();
                break;
//            case MotionEvent.ACTION_UP:
//                break;
        }
        return super.onTouchEvent(event);
    }
}
