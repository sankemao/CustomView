package com.pronetway.baselib.datahandler;

import android.os.Parcelable;

import com.blankj.utilcode.util.CacheUtils;

/**
 * Created by jin on 2017/9/25.
 */
public class DiskIOHandler implements IOHandler {
    @Override
    public void save(String key, String value) {
        CacheUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, boolean value) {
        CacheUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, int value) {
        CacheUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, Object value) {
        CacheUtils.getInstance().put(key, (Parcelable) value);
    }

    @Override
    public void save(String key, float value) {
        CacheUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, long value) {
        CacheUtils.getInstance().put(key, value);
    }

    @Override
    public String getString(String key, String def) {
        return CacheUtils.getInstance().getString(key, def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return (boolean) CacheUtils.getInstance().getSerializable(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return (int) CacheUtils.getInstance().getSerializable(key, def);
    }

    @Override
    public Object getObject(String key, Object def) {
        return CacheUtils.getInstance().getSerializable(key, def);
    }

    @Override
    public float getFloat(String key, float def) {
        return (float) CacheUtils.getInstance().getSerializable(key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return (long) CacheUtils.getInstance().getSerializable(key, def);
    }


}
