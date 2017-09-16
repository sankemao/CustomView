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
 *
 */
public class DefaultLoadMoreCreator extends LoadViewCreator {
    // 加载数据的ImageView
    private View mRefreshIv;
    private TextView mLoadState;

    @Override
    public View getLoadView(Context context, ViewGroup parent) {
        View refreshView = LayoutInflater.from(context).inflate(R.layout.j_refresh_header, parent, false);
        mLoadState = (TextView) refreshView.findViewById(R.id.tv_state);
        mRefreshIv = refreshView.findViewById(R.id.iv_pic);
        mLoadState.setText("上拉加载...");
        return refreshView;
    }

    @Override
    public void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus) {
        float rotate = ((float) currentDragHeight) / loadViewHeight;
        // 不断下拉的过程中不断的旋转图片
        mLoadState.setText("松开加载...");
        mRefreshIv.setRotation(rotate * 360);
    }

    @Override
    public void onLoading() {
        // 刷新的时候不断旋转
        mRefreshIv.setVisibility(View.VISIBLE);
        RotateAnimation animation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        mRefreshIv.startAnimation(animation);
        mLoadState.setText("正在加载...");
    }

    @Override
    public void onStopLoad() {
        // 停止加载的时候清除动画
        mRefreshIv.setRotation(0);
        mRefreshIv.clearAnimation();
        mLoadState.setText("上拉加载...");
    }

    @Override
    public void onEndLoad() {
        mRefreshIv.setRotation(0);
        mRefreshIv.clearAnimation();
        mRefreshIv.setVisibility(View.GONE);
        mLoadState.setText("--end--");
    }

    @Override
    public void reset() {
        mRefreshIv.setVisibility(View.VISIBLE);
        mLoadState.setText("上拉加载...");
    }

    @Override
    public void onError() {
        mRefreshIv.setRotation(0);
        mRefreshIv.clearAnimation();
        mRefreshIv.setVisibility(View.GONE);
        mLoadState.setText("请上拉重试~");
    }

}
