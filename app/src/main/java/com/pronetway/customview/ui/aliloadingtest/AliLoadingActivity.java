package com.pronetway.customview.ui.aliloadingtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pronetway.customview.R;
import com.pronetway.customview.custom.AliLoadingView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AliLoadingActivity extends AppCompatActivity {

    @Bind(R.id.ali_loading_view)
    AliLoadingView mAliLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_loading);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_start)
    public void onClick() {
        mAliLoadingView.startCircleAnim();
    }
}
