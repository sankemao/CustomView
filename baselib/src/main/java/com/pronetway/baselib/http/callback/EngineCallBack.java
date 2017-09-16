package com.pronetway.baselib.http.callback;

import android.content.Context;

import java.util.Map;

/**
 * Created by jin on 2017/7/16.
 *
 */
public interface EngineCallBack {

    void onPreExecute(Context cxt, Map<String, Object> params);

    void onError(Exception e);

    void onSuccess(String result);

    //默认的
    EngineCallBack Default_call_back = new EngineCallBack() {
        @Override
        public void onPreExecute(Context cxt, Map<String, Object> params) {

        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onSuccess(String result) {

        }
    };

}
