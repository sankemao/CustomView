package com.pronetway.customview.ui.brvah;

import android.content.Context;

import com.google.gson.Gson;
import com.pronetway.baselib.http.callback.EngineCallBack;
import com.pronetway.baselib.utils.ThreadUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by jin on 2017/7/23.
 * 项目通用的callback
 * 参考: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
 */
public abstract class SimpleCallBack<T> implements EngineCallBack {
    @Override
    public void onPreExecute(Context cxt, Map<String, Object> params) {
        //可以在这里添加一些共用的参数.
        onPreExecute();
    }

    @Override
    public void onSuccess(final String result) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        final Type type = params[0];//获取泛型T的具体类型, 包括T自己的泛型 eg: baseResponse<userInfo>

        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
//                if (type == String.class) {//T为String, 不解析, 直接返回String.
//                    onMainSuccess((T)result);
//                    return;
//                }
                Type rawType = null;
                if ((type instanceof ParameterizedType)) {//泛型嵌套才会走进这个判断.目的是获取泛型外层
                    rawType = ((ParameterizedType) type).getRawType();
                }

                final Gson gson = new Gson();
                try {
                    //当rawType == null(泛型只有一层), 或泛型嵌套了, 但外层泛型不为BaseResponse.
                    if (rawType != BaseResponse.class) {
                        onMainSuccess(((T) gson.fromJson(result, type)));
                    } else {
                        final BaseResponse baseResPonse = gson.fromJson(result, type);
                        final String code = baseResPonse.getResult();
                        if ("0".equals(code)) {
                            onMainSuccess(((T) baseResPonse));
                        } else {
                            onError(new IllegalStateException(code));
                        }
                    }
                } catch (Exception e) {
                    onError(e);
                }
            }
        });

    }

    protected void onPreExecute() {

    }

    @Override
    public void onError(final Exception e) {
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainError(e);
            }
        });
    }

    //返回可以直接操作的对象
    public abstract void onMainSuccess(T result);

    //主线程中.
    protected abstract void onMainError(Exception e);

}
