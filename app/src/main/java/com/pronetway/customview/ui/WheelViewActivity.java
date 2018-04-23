package com.pronetway.customview.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

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

        final View contentView = findViewById(R.id.ll_content);

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

        final View testView = findViewById(R.id.view_test);

        final Button button = (Button) findViewById(R.id.btn_test);
        button.setOnClickListener(new View.OnClickListener() {
            float distanceX = 0;
            @Override
            public void onClick(View v) {
                ViewCompat.setTranslationX(testView, distanceX);
                distanceX = distanceX + 20;
            }
        });


//        final Button btn = (Button) findViewById(R.id.btn_scroll_to);
//        final Button btn2 = (Button) findViewById(R.id.btn_scroll_to2);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((View) v.getParent()).scrollTo(-20, -20);
//                ToastUtils.showShort("getScrollx: " + ((View) v.getParent()).getScrollX());
//            }
//        });
//
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((View) v.getParent()).scrollTo(-20, -20);
//                ToastUtils.showShort("getScrollx: " + ((View) v.getParent()).getScrollX());
//            }
//        });
    }
}
