package com.pronetway.baselib.http;

import android.content.Context;
import android.text.TextUtils;

import com.pronetway.baselib.http.cache.CacheDataUtil;
import com.pronetway.baselib.http.callback.EngineCallBack;
import com.pronetway.baselib.http.callback.FileCallBack;
import com.pronetway.baselib.http.progress.DownInterceptor;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jin on 2017/7/16.
 * 默认引擎
 */
public class OkhttpEngine implements IHttpEngine {
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    public void addInterceptor(Interceptor interceptor) {
        mOkHttpClient = mOkHttpClient.newBuilder().addInterceptor(interceptor).build();
    }

    @Override
    public void supportHttps() {
        HttpsConfig.SSLParams sslParams = HttpsConfig.getSslSocketFactory();
        mOkHttpClient = mOkHttpClient.newBuilder().hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).build();
    }

    @Override
    public void setConnTimeOut(int timeOut) {
        mOkHttpClient = mOkHttpClient.newBuilder().connectTimeout(timeOut, TimeUnit.SECONDS).build();
    }

    /**
     * 下载文件
     */
    @Override
    public void downFile(Context context, String url, final FileCallBack callBack) {
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
        mOkHttpClient
                .newBuilder()
                .addInterceptor(new DownInterceptor(callBack))
                .build()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onError();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                File file = callBack.convertResponse(response);
                                callBack.downSuccess(file);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                                callBack.downError();
                            }
                        }
                    }
                });
    }

    @Override
    public void post(boolean cache, Context context, String url, Map<String, Object> params, final EngineCallBack callBack) {
        final String jointUrl = HttpUtils.jointParams(url, params);//用来打印
//        Log.e("Post请求路径: ", jointUrl);
        RequestBody requestBody = appendBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
//                        Log.e("Post返回结果: ", result);
                        callBack.onSuccess(result);
                    }
                }
        );
    }

    /**
     * 组装post请求参数body
     *
     * @param params
     * @return
     */
    private RequestBody appendBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    /**
     * 添加参数.
     * @param builder
     * @param params
     */
    private void addParams(MultipartBody.Builder builder, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
//                builder.addFormDataPart(key, params.get(key) + "");
                Object value = params.get(key);
                if (value instanceof File) {
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                } else if (value instanceof List) {
                    // 代表提交的是List集合
                    try {
                        List<File> listFiles = (List<File>) value;
                        for (int i = 0; i < listFiles.size(); i++) {
                            // 获取文件
                            File file = listFiles.get(i);
                            builder.addFormDataPart(key + i, file.getName(), RequestBody
                                    .create(MediaType.parse(guessMimeType(file
                                            .getAbsolutePath())), file));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.addFormDataPart(key, value + "");
                }
            }
        }
    }

    /**
     * 猜测文件类型
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    /**
     * 异步get请求.
     */
    @Override
    public void get(final boolean cache, Context context, String url, Map<String, Object> params, Map<String, String> headers, final EngineCallBack callBack) {
        // 请求路径  参数 + 路径代表唯一标识
        final String finalUrl = HttpUtils.jointParams(url, params);
//        Log.e("Get请求路径：", finalUrl);

        // 1.判断需不需要缓存，然后判断有没有
        if (cache) {
            String resultJson = CacheDataUtil.getCacheResultJson(finalUrl);
            if (!TextUtils.isEmpty(resultJson)) {
//                Log.e("TAG", "已读到缓存");
                // 需要缓存，而且数据库有缓存,直接就去执行，里面执行成功
                callBack.onSuccess(resultJson);
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl)
                .tag(context)
                .headers(Headers.of(headers));
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultJson = response.body().string();
                // 获取数据之后会执行成功方法
                if (cache) {
                    String cacheResultJson = CacheDataUtil.getCacheResultJson(finalUrl);
                    if (!TextUtils.isEmpty(resultJson)) {
                        // 比对内容
                        if (resultJson.equals(cacheResultJson)) {
                            // 内容一样，不需要执行成功成功方法刷新界面
//                            Log.e("数据和缓存一致：", resultJson);
                            return;
                        }
                    }
                }
                // 2.2 执行成功方法
                callBack.onSuccess(resultJson);
//                Log.e("Get返回结果：", resultJson);
                if (cache) {
                    // 2.3 缓存数据
                    CacheDataUtil.cacheData(finalUrl, resultJson);
                }
            }
        });
    }

    /**
     * 同步get请求
     */
    @Override
    public Response get(Context context, String url, Map<String, Object> params) {
        final String finalUrl = HttpUtils.jointParams(url, params);
//        Log.e("Get请求路径：", finalUrl);
        Request.Builder requestBuilder = new Request.Builder()
                .url(finalUrl)
                .tag(context);
        //可以省略，默认是GET请求
        requestBuilder.method("GET", null);
        Request request = requestBuilder.build();
        try {
            return mOkHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 根据tag取消请求
     * 参照:
     * https://github.com/jeasonlzy/okhttp-OkGo/blob/1b2a7edd092b7dedf321a2c5911a3e132db82d9b/okgo/src/main/java/com/lzy/okgo/OkGo.java
     * @param tag
     */
    @Override
    public void cancelRequest(Object tag) {
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        synchronized (OkhttpEngine.class){
            for (Call call : dispatcher.queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : dispatcher.runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }
}
