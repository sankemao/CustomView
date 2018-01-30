package com.pronetway.customview.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description:TODO
 * Create Time: 2017/12/26.17:23
 * Author:jin
 * Email:210980059@qq.com
 */
public class WifiUtils {

    /**
     * 打开热点
     * @param context       上下文
     * @param spotName      热点名称
     * @param spotPassword  热点密码
     * @return              是否打开成功.
     */
    public static boolean openHotSpot(Context context, String spotName, @Nullable String spotPassword) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        WifiConfiguration apConfig = new WifiConfiguration();

        apConfig.SSID = spotName;

        if (!TextUtils.isEmpty(spotPassword)) {
            apConfig.preSharedKey = spotPassword;
        }

        apConfig.hiddenSSID = false;

        apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedKeyManagement.set(4);//WifiConfiguration.KeyMgmt.WPA2_PSK为4, 但不能拿到, 所以直接填入4.
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        apConfig.status = WifiConfiguration.Status.ENABLED;

        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(wifiManager, apConfig, true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭热点
     * @param context   上下文
     */
    public static void closeHotSpot(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);

            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
