package com.pronetway.baselib.recyclerview.headfootview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.pronetway.baselib.R;


/**
 * Created by jin on 2017/5/10.
 * 默认的下拉刷新view
 */
public class DefaultRefreshCreator extends RefreshViewCreator {

    // 加载数据的ImageView
    private View mRefreshIv;
    private TextView mRefreshState;

    @Override
    public View getRefreshView(Context context, ViewGroup parent) {
        View refreshView = LayoutInflater.from(context).inflate(R.layout.j_refresh_header, parent, false);
        mRefreshIv = refreshView.findViewById(R.id.iv_pic);
        mRefreshState = (TextView) refreshView.findViewById(R.id.tv_state);
        return refreshView;
    }

    @Override
    public void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus) {
        float rotate = ((float) currentDragHeight) / refreshViewHeight;
        // 不断下拉的过程中不断的旋转图片
        mRefreshState.setText("松开刷新...");
        mRefreshIv.setRotation(rotate * 360);
    }

    @Override
    public void onRefreshing() {
        // 刷新的时候不断旋转
        RotateAnimation animation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        mRefreshIv.startAnimation(animation);
        mRefreshState.setText("正在刷新...");
    }

    @Override
    public void onStopRefresh() {
        // 停止加载的时候清除动画
        mRefreshIv.setRotation(0);
        mRefreshIv.clearAnimation();
        mRefreshState.setText("刷新完成...");
    }
}
