package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.pronetway.customview.R;

/**
 * Created by jin on 2017/9/14.
 *
 */
public class MyRadioButton extends AppCompatRadioButton {

//    private Paint mPaint;
//    private Paint mPaint2;
//
//    public MyRadiobutton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.YELLOW);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(4);
//
//        mPaint2 = new Paint();
//        mPaint2.setAntiAlias(true);
//        mPaint2.setColor(Color.YELLOW);
//    }
//
//    public MyRadiobutton(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
////        super.onDraw(canvas);
//        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 2, mPaint);
//
//        if (isChecked()) {
//            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 20, mPaint2);
//        }
//
//    }


    private int buttonSize;//半径
    private int buttonColor;
    private Paint paint;
    private int borderSize = 2;
    private Paint textPaint;
    private int bTextColor;
    private int bTextSize;
    private String bText;
    private Rect mBounds;
    private int dy;
    private int measuredWidth;
    private int measuredHeight;
    private int padding;//文字到button的距离
    private Paint mPaint2;

    public MyRadioButton(Context context) {
        this(context,null);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context,attrs);
    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initData(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRadioButton);
        if(typedArray != null){
            buttonSize = typedArray.getDimensionPixelSize(R.styleable.MyRadioButton_buttonSize, 10);
            bTextSize = typedArray.getDimensionPixelSize(R.styleable.MyRadioButton_bTextSize, (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
            buttonColor = typedArray.getColor(R.styleable.MyRadioButton_buttonColor, Color.GRAY);
            bTextColor = typedArray.getColor(R.styleable.MyRadioButton_bTextColor, Color.GRAY);
            bText = typedArray.getString(R.styleable.MyRadioButton_bText);
            typedArray.recycle();

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(buttonColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);

            mPaint2 = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(buttonColor);


            mBounds = new Rect();
            padding = 10;

            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(bTextColor);
            textPaint.setTextSize(bTextSize);
            textPaint.getTextBounds(bText, 0, bText.length(), mBounds);

            //y 基线 baseline
            Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
            dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;

        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else{
            width = getPaddingLeft() + buttonSize * 2 + padding + mBounds.width() + getPaddingRight();
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else{
            int finalHeight = buttonSize * 2 > mBounds.height() ? buttonSize * 2 : mBounds.height();
            height = getPaddingTop() + finalHeight + getPaddingBottom();
        }

        setMeasuredDimension(width,height);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        measuredWidth = getMeasuredWidth();
//        measuredHeight = getMeasuredHeight();
//    }

    @Override
    protected void onDraw(Canvas canvas) {
//        //画空心外圆
//        paint.setStrokeWidth(borderSize);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(buttonColor);
        canvas.drawCircle(buttonSize, measuredHeight / 2, buttonSize - borderSize, paint);

        //画实心內圆
        if(isChecked()){
//            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(buttonSize,measuredHeight / 2,buttonSize - borderSize - 5, mPaint2);
        }

        //画文字
        canvas.drawText(bText,buttonSize + padding,measuredHeight / 2 + dy,textPaint);
    }
}
