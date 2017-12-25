package com.pronetway.customview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;

import com.pronetway.baselib.datahandler.IOHandler;
import com.pronetway.baselib.popupwindow.QuickPopupWindow;
import com.pronetway.customview.R;
import com.pronetway.customview.custom.SubmitButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AliLoadingActivity extends AppCompatActivity {

//    @Bind(R.id.ali_loading_view)
//    AliLoadingView mAliLoadingView;
    @Bind(R.id.btn_start)
    Button mBtnStart;
    @Bind(R.id.like_view)
    SubmitButton mLikeView;
    private IOHandler mDiskHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_loading);
        ButterKnife.bind(this);
        mLikeView.setOnResultEndListener(new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
//                ToastUtils.showShort("登录成功");
                mLikeView.reset();
            }
        });

    }

    @OnClick(R.id.btn_start)
    public void onClick() {
//        mAliLoadingView.startCircleAnim();
        mLikeView.doResult(true);

        new QuickPopupWindow.PopupBuilder(this)
                .setContentView(R.layout.pop_test)
                .setSize(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                .setCancelable(false)
                .show(mBtnStart);
    }
}
