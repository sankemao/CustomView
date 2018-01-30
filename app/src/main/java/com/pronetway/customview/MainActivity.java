package com.pronetway.customview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.customview.ui.CustomViewsActivity;
import com.pronetway.customview.ui.WebViewFirst;
import com.pronetway.customview.util.WifiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();

        initData();
    }

    private void initView() {

    }

    private void initData() {
        items = new ArrayList<>();
        items.add("a");
        items.add("a");
        items.add("a");
        items.add("a");
        ToastUtils.showShort(items.size() + "");
    }

    @OnClick({R.id.btn_webview, R.id.btn_custom_views, R.id.btn_test_list, R.id.btn_change_ssid})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_webview:
                startActivity(new Intent(this, WebViewFirst.class));
                break;
            case R.id.btn_custom_views:
                startActivity(new Intent(this, CustomViewsActivity.class));
                break;
            case R.id.btn_test_list:
                items = new ArrayList<>();
                ToastUtils.showShort(items.size() + "");
                break;
            case R.id.btn_change_ssid:
                boolean result = WifiUtils.openHotSpot(this, "aaa", "1111");
                ToastUtils.showShort("热点打开结果为: " + result);
                break;
            default:
                break;
        }
    }

}
