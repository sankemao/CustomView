package com.pronetway.customview.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.pronetway.customview.R;

public class WebViewFirst extends AppCompatActivity implements ChromeClientCallbackManager.ReceivedTitleCallback {

    private AgentWeb mAgentWeb;

    //WebViewClient
    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String javascript = "javascript:function hideOther() {" +
                    "document.getElementsByClassName('mui-bar mui-bar-nav')[0].remove();" +
                    "}";

            //创建方法
            view.loadUrl(javascript);

            //加载方法
            view.loadUrl("javascript:hideOther();");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_first);

        LinearLayout llContainer = (LinearLayout) findViewById(R.id.ll_container);
        //传入Activity
        mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(llContainer, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                .setReceivedTitleCallback(this) //设置 Web 页面的 title 回调
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go("http://125.46.108.2:8036/wap/smxjiaowu.html");

        WebView mWebView = mAgentWeb.getWebCreator().get();
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
