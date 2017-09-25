package com.pronetway.baselib.datahandler;

import com.blankj.utilcode.util.SPUtils;

/**
 * Created by jin on 2017/9/25.
 */
public class PerferencesIOHandler implements IOHandler {


    @Override
    public void save(String key, String value) {
        SPUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, boolean value) {
        SPUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, int value) {
        SPUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, Object value) {
        throw new IllegalStateException("sp中不能取obj");
    }

    @Override
    public void save(String key, float value) {
        SPUtils.getInstance().put(key, value);
    }

    @Override
    public void save(String key, long value) {
        SPUtils.getInstance().put(key, value);
    }

    @Override
    public String getString(String key, String def) {
        return SPUtils.getInstance().getString(key, def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return SPUtils.getInstance().getBoolean(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return SPUtils.getInstance().getInt(key, def);
    }

    @Override
    public Object getObject(String key, Object def) {
        throw new IllegalStateException("sp中不能取obj");
    }

    @Override
    public float getFloat(String key, float def) {
        return SPUtils.getInstance().getFloat(key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return SPUtils.getInstance().getLong(key, def);
    }

}
