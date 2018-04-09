package com.pronetway.customview.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.blankj.utilcode.util.LogUtils;
import com.pronetway.customview.R;
import com.pronetway.customview.custom.WheelView;

import java.util.ArrayList;
import java.util.List;

public class WheelViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);
        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);

        wheelView.setTextSize(80);
        wheelView.setVisibilityCount(7);
        wheelView.setTextGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        wheelView.setSelectedTextColor(Color.RED);
        final List<String> dataSources = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            dataSources.add("数据" + i);
        }
        wheelView.setDataSources(dataSources);
        wheelView.setCallBack(new WheelView.CallBack() {
            @Override
            public void onPositionSelect(int position) {
                LogUtils.e("当前选中条目： " + position);
            }
        });
    }
}
