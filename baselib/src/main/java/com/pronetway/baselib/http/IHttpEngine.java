package com.pronetway.baselib.http;

import android.content.Context;

import com.pronetway.baselib.http.callback.EngineCallBack;
import com.pronetway.baselib.http.callback.FileCallBack;

import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by jin on 2017/7/16.
 * 引擎的规范
 */
public interface IHttpEngine {

    void addInterceptor(Interceptor interceptor);

    void supportHttps();

    //get请求
    void get(boolean cache, Context context, String url, Map<String, Object> params, Map<String, String> headers, EngineCallBack callBack);

    //post请求
    void post(boolean cache, Context context, String url, Map<String, Object> params, EngineCallBack callBack);

    //同步请求.
    Response get(Context context, String url, Map<String, Object> params);

    //取消请求.
    void cancelRequest(Object tag);

    //下载文件
    void downFile(Context context, String url, FileCallBack callBack);

    //上传文件

    //https 添加证书.

    //设置超时
    void setConnTimeOut(int timeOut);

}
