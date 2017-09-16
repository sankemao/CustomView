package com.pronetway.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pronetway.customview.R;

/**
 * Created by jin on 2017/9/1.
 * 变色字体
 */
public class ColorTrackTextView extends TextView {

    private Paint mOriginPaint;
    private Paint mChangePaint;
    private float mCurrentProgress = 0.0f;
    private Direction mDirection = Direction.LEFT_TO_RIGHT;

    public enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    public void setDirection(Direction direction) {
        this.mDirection = direction;
    }

    /**
     * 在代码中调用
     * @param context
     */
    public ColorTrackTextView(Context context) {
        this(context, null);
    }

    /**
     * 在布局中使用
     * @param context
     * @param attrs
     */
    public ColorTrackTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 在布局中使用, 带有style
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ColorTrackTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs);
    }

    /**
     * 设置分割线位置, 百分比
     * @param currentProgress
     */
    public void setCurrentProgress(float currentProgress) {
        this.mCurrentProgress = currentProgress;
        invalidate();
    }

    /**
     * 设置画笔颜色
     * @param originColor
     */
    public void setOriginColor(int originColor) {
        this.mOriginPaint.setColor(originColor);
    }

    public void setChangeColor(int changeColor) {
        this.mChangePaint.setColor(changeColor);
    }

    /**
     * 初始化画笔
     */
    private void initPaint(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView);
        int originColor = a.getColor(R.styleable.ColorTrackTextView_originColor, getTextColors().getDefaultColor());
        int changeColor = a.getColor(R.styleable.ColorTrackTextView_changeColor, getTextColors().getDefaultColor());

        mOriginPaint = getPaintByColor(originColor);
        mChangePaint = getPaintByColor(changeColor);

        a.recycle();
    }

    private Paint getPaintByColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        //抗锯齿.
        paint.setAntiAlias(true);
        //防抖动.
        paint.setDither(true);
        paint.setTextSize(getTextSize());
        //设置粗体
        paint.setFakeBoldText(true);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas); 重写onDraw.
        //变色中间分割线
        int middle = (int) (mCurrentProgress * getWidth());
        if (mDirection == Direction.LEFT_TO_RIGHT) {
            //绘制变色
            drawText(canvas, mChangePaint, 0, middle);
            //绘制原始颜
            drawText(canvas, mOriginPaint, middle, getWidth());
        } else {
            drawText(canvas, mChangePaint, getWidth() - middle, getWidth());
            drawText(canvas, mOriginPaint, 0, getWidth() - middle);
        }
    }

    /**
     * 绘制文字
     * @param canvas    画布
     * @param paint     画笔
     * @param start     变色起始位置(clipPath)
     * @param end       结束位置
     */
    private void drawText(Canvas canvas, Paint paint, int start, int end) {
        String content = getText().toString();
        //在裁剪前先保存画布.canvas.save();和canvas.restore();成对使用, 不会对画布的其他元素影响.
        canvas.save();
        Rect rect = new Rect(start, 0, end, getHeight());
        //裁剪
        canvas.clipRect(rect);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        int x = getWidth() / 2 - bounds.width() / 2;
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        int baseLine = getHeight() / 2 + dy;//dy为正值.
        canvas.drawText(content, x, baseLine, paint);
        //还原画布
        canvas.restore();
    }
}
