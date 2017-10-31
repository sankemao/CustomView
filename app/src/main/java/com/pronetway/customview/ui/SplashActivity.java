package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.pronetway.customview.R;
import com.pronetway.customview.custom.SplashView;

public class SplashActivity extends AppCompatActivity {

    private ImageView mSplashLogo;
    private SplashView mSplashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashView = (SplashView) findViewById(R.id.splash);
        mSplashLogo = (ImageView) findViewById(R.id.splash_logo);
        mSplashView.setLogoShowCallBack(new SplashView.CallBack() {
            @Override
            public void showLogo() {
                mSplashLogo.setVisibility(View.VISIBLE);
            }

            @Override
            public void end() {
                ToastUtils.showShort("跳转到新页面");
            }
        });
    }
}
