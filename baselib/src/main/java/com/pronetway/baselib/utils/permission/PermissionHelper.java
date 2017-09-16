package com.pronetway.baselib.utils.permission;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by jin on 2017/5/8.
 * 注解 + 反射
 */
public class PermissionHelper {
    private Object mObject;                 //fragment or activity
    private int mRequestCode;               //eg: CALL_PHONE_REQUEST_CODE
    private String[] mRequestPermissions;    //eg: new String[]{"Manifest.permission.CALL_PHONE"}, CALL_PHONE_REQUEST_CODE);

    private PermissionHelper(Object object){
        this.mObject = object;
    }

    public static void  requestPermission(Activity activity, int requestCode, String[] permissions){
        PermissionHelper.with(activity).requestCode(requestCode).
                requestPermission(permissions).request();
    }

    public static void  requestPermission(Fragment fragment, int requestCode, String[] permissions){
        PermissionHelper.with(fragment).requestCode(requestCode).
                requestPermission(permissions).request();
    }

    //链式调用
    // 传Activity
    public static PermissionHelper with(Activity activity){
        return new PermissionHelper(activity);
    }

    // 传Fragment
    public static PermissionHelper with(Fragment fragment){
        return new PermissionHelper(fragment);
    }

    // 添加一个请求码
    public PermissionHelper requestCode(int requestCode){
        this.mRequestCode = requestCode;
        return this;
    }

    // 添加请求的权限数组
    public PermissionHelper requestPermission(String... permissions){
        this.mRequestPermissions = permissions;
        return this;
    }

    public void request() {
        // 3.2 首先判断当前的版本是不是6.0 及以上
        if(!PermissionUtils.isOverMarshmallow()){
            // 3.3 如果不是6.0以上  那么直接执行方法   反射获取执行方法
            // 执行什么方法并不确定 那么我们只能采用注解的方式给方法打一个标记，
            // 然后通过反射去执行。  注解 + 反射  执行Activity里面的callPhone
            PermissionUtils.executeSucceedMethod(mObject,mRequestCode);
            return;
        }
        // 3.3 如果是6.0以上  那么首先需要判断权限是否授予
        // 需要申请的权限中 获取没有授予过得权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(mObject, mRequestPermissions);

        // 3.3.1 如果授予了 那么我们直接执行方法   反射获取执行方法
        if(deniedPermissions.size() == 0){
            // 全部都是授予过的
            PermissionUtils.executeSucceedMethod(mObject,mRequestCode);
        }else {
            // 3.3.2 如果没有授予 那么我们就申请权限  申请权限
            ActivityCompat.requestPermissions(PermissionUtils.getActivity(mObject),
                    deniedPermissions.toArray(new String[deniedPermissions.size()]),
                    mRequestCode);
        }
    }

    /**
     * 处理权限的回调
     */
    public static void requestPermissionsResult(Object obj, int requestCode, String[] permissions) {
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(obj, permissions);
        if (deniedPermissions.size() == 0) {
            PermissionUtils.executeSucceedMethod(obj, requestCode);
        } else {
            PermissionUtils.executeFailMethod(obj, requestCode);
        }
    }

}
