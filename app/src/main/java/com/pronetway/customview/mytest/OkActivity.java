package com.pronetway.customview.mytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.pronetway.customview.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);

        OkHttpClient client = new OkHttpClient();


//        RequestBody requestBody = new RequestBody();
//        new FormBody.Builder().add().build();
        //1. 构建一个请求, url, 端口, 请求头的一些参数, 表单提交(contentType, contentLength)
        Request request = new Request.Builder()
//                .post()
                .url("http://www.baidu.com")
                .build();
        //2. 将Request封装成一个RealCall
        Call call = client.newCall(request);
        //3. 队列处理执行, 重点(RealCall中的enqueue)
        //AsyncCall是RealCall的内部类,给了Okhttp的dispatcher
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.e("result=" + result);
            }
        });
    }
}
