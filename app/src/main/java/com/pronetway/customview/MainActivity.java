package com.pronetway.customview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pronetway.customview.ui.CustomViewsActivity;
import com.pronetway.customview.ui.webviews.WebViewFirst;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

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

    }

    @OnClick({R.id.btn_webview, R.id.btn_custom_views})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_webview:
                startActivity(new Intent(this, WebViewFirst.class));
                break;
            case R.id.btn_custom_views:
                startActivity(new Intent(this, CustomViewsActivity.class));
                break;
        }
    }
}
