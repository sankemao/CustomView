package com.pronetway.baselib.datahandler;

import android.util.LruCache;

/**
 * Created by jin on 2017/9/25.
 */
public class MemoryIOHandler implements IOHandler {

    final static int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;

    private static LruCache<String, Object> mCache = new LruCache<>(cacheSize);

    @Override
    public void save(String key, String value) {
        mCache.put(key, value);
    }

    @Override
    public void save(String key, boolean value) {
        mCache.put(key, value);
    }

    @Override
    public void save(String key, int value) {
        mCache.put(key, value);
    }

    @Override
    public void save(String key, Object value) {
        mCache.put(key, value);
    }

    @Override
    public void save(String key, float value) {
        mCache.put(key, value);
    }

    @Override
    public void save(String key, long value) {

    }

    @Override
    public String getString(String key, String def) {
        return getValue(key, def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return getValue(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return getValue(key, def);
    }

    @Override
    public Object getObject(String key, Object def) {
        return getValue(key, def);
    }

    @Override
    public float getFloat(String key, float def) {
        return getValue(key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return getValue(key, def);
    }

    private <T> T getValue(String key, T def) {
        Object o = mCache.get(key);
        if (o == null) {
            return def;
        }
        return ((T) o);
    }

    public void clear() {
        mCache.evictAll();
    }

}
