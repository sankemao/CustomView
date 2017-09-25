package com.pronetway.baselib.datahandler;

/**
 * Created by jin on 2017/9/25.
 */
public enum  IOHandlerFactory {
    INSTANCE;

    IOHandler spHandler, diskHandler, memoryHandler;

    public IOHandler createIOHandler(Class<? extends IOHandler> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PerferencesIOHandler();
    }

    /**
     * sp处理数据
     */
    public IOHandler getSpHandler() {
        return spHandler == null ? createIOHandler(PerferencesIOHandler.class) : spHandler;
    }

    /**
     * 磁盘处理数据.
     */
    public IOHandler getDiskHandler() {
        return diskHandler == null ? createIOHandler(MemoryIOHandler.class) : diskHandler;
    }

    /**
     * 内存处理数据
     */
    public IOHandler getMemoryHandler() {
        return memoryHandler == null ? createIOHandler(MemoryIOHandler.class) : memoryHandler;
    }

}
