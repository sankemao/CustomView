package com.pronetway.baselib.http;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.pronetway.baselib.http.callback.EngineCallBack;
import com.pronetway.baselib.http.callback.FileCallBack;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by jin on 2017/7/16.
 *
 */
public class HttpUtils {

    //直接带参数, 链式调用
    private String mUrl;
    //请求方式.
    private int mType = GET_TYPE;
    private static final int POST_TYPE = 0x0011;
    private static final int GET_TYPE = 0x0012;

    private Context mContext;

    private Map<String, Object> mParams;
    private Map<String, String> mHeaders;

    private boolean mCache;

    private HttpUtils(Context context) {
        mContext = context;
        mParams = new HashMap<>();
        mHeaders = new HashMap<>();
    }

    public static HttpUtils with(Context context) {
        return new HttpUtils(context);
    }

    public HttpUtils url(String url) {
        this.mUrl = url;
        return this;
    }

    //请求方式.
    public HttpUtils post() {
        mType = POST_TYPE;
        return this;
    }

    public HttpUtils get() {
        mType = GET_TYPE;
        return this;
    }

    /**
     * 配置缓存.
     */
    public HttpUtils cache(boolean isCache) {
        mCache = isCache;
        return this;
    }

    public HttpUtils headers(String key, String value) {
        mHeaders.put(key, value);
        return this;
    }

    public HttpUtils addParam(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    public HttpUtils addParam(Map<String, Object> params) {
        mParams.putAll(params);
        return this;
    }

    //同步
    public Response execute() {
        if (mType == GET_TYPE) {
            return get(mUrl, mParams);
        }

        //post同步请求待完成.
        return null;
    }

    //添加回调
    public void enqueue() {
        enqueue(null);
    }

    /**
     * 异步.
     */
    public void enqueue(EngineCallBack callBack) {
        if (callBack == null) {
            callBack = EngineCallBack.Default_call_back;
        }
        //共用.
        callBack.onPreExecute(mContext, mParams);

        //判断执行方法
        if (mType == POST_TYPE) {
            post(mUrl, mParams, callBack);
        }

        if (mType == GET_TYPE) {
            get(mUrl, mParams, mHeaders, callBack);
        }
    }

    /**
     * 下载文件.
     * @param fileCallBack
     */
    public void downFile(FileCallBack fileCallBack) {
        if (fileCallBack == null) {
            LogUtils.d("必须设置fileCallback");
            return;
        }
        downFile(mUrl, fileCallBack);
    }

    private static IHttpEngine mHttpEngine = new OkhttpEngine();

    //用于在application中初始化引擎.
    public static void init(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
    }
    //每次可以改变.
    public void exchangeEngine(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
    }

    // FIXME: 2017/7/21
    private void get(String url, Map<String, Object> params, Map<String, String> headers, EngineCallBack callBack) {
        mHttpEngine.get(mCache, mContext, url, params, headers, callBack);
    }

    private Response get(String url, Map<String, Object> params) {
        return mHttpEngine.get(mContext, url, params);
    }
    
    

    private void post(String url, Map<String, Object> params, EngineCallBack callBack) {
        mHttpEngine.post(mCache, mContext, url, params, callBack);
    }

    private void downFile(String url, FileCallBack callBack) {
        mHttpEngine.downFile(mContext, url, callBack);
    }

    /**
     * 拼接参数.
     */
    protected static String jointParams(String url, Map<String, Object> params) {
        if (params == null || params.size() <= 0) {
            return url;
        }
        StringBuffer stringBuffer = new StringBuffer(url);
        if (!url.contains("?")) {
            stringBuffer.append("?");
        } else {
            if (!url.endsWith("?")) {
                stringBuffer.append("&");
            }
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");
        }

        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        return stringBuffer.toString();
    }


    /**
     * 解析一个类上面的class-泛型
     * 如果支持泛型，返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class，因为可能有多个，所以是数组。
     */
    protected static Class<?> analysisClazzInfo(Object object) {
        Type genType = object.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }

    public static void addInterceptor(Interceptor interceptor) {
        mHttpEngine.addInterceptor(interceptor);
    }

    public static void setConnTimeOut(int timeOut) {
        mHttpEngine.setConnTimeOut(timeOut);
    }

    public static void cancelRequest(Object tag) {
        mHttpEngine.cancelRequest(tag);
    }

    public static void supportHttps() {
        mHttpEngine.supportHttps();
    }
}
