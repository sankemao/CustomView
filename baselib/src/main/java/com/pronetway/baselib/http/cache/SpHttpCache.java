package com.pronetway.baselib.http.cache;

import com.blankj.utilcode.util.SPUtils;

/**
 * Created by jin on 2017/8/26.
 */
public class SpHttpCache {
    public void saveCache(String finalUrl, String resultJson) {

    }

    public String getCache(String finalUrl) {
        return SPUtils.getInstance().getString(finalUrl);
    }
}
