package com.pronetway.baselib.recyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by jin on 2017/5/25.
 * 可自定义分割线的高度以及颜色.
 */
public class MyItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private Paint mPaint;

    public MyItemDecoration(int space, int color) {
        this.space = space;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, space);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int adapterPosition = parent.getChildAdapterPosition(child);
            //最后一个条目不绘制分割线.
            if (adapterPosition == parent.getAdapter().getItemCount() - 1) {
                continue;
            }
            int top = child.getBottom();
            int bottom = top + space;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
