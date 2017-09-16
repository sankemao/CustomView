package com.pronetway.baselib.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private static Executor sExecutor = Executors.newSingleThreadExecutor();
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    public static void runOnMainThread(Runnable runnable) {
        sHandler.post(runnable);
    }
    public static void runOnSubThread(Runnable runnable) {
        sExecutor.execute(runnable);
    }
}