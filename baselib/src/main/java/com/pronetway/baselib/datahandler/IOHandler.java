package com.pronetway.baselib.datahandler;

/**
 * Created by jin on 2017/9/25.
 */
public interface IOHandler {
    void save(String key, String value);

    void save(String key, boolean value);

    void save(String key, int value);

    void save(String key, Object value);

    void save(String key, float value);

    void save(String key, long value);



    String getString(String key, String def);

    boolean getBoolean(String key, boolean def);

    int getInt(String key, int def);

    Object getObject(String key, Object def);

    float getFloat(String key, float def);

    long getLong(String key, long def);
}
