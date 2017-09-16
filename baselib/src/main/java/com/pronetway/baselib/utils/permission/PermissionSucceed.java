package com.pronetway.baselib.utils.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jin on 2017/5/8.
 */
@Target(ElementType.METHOD)//放在什么位置: 方法
@Retention(RetentionPolicy.RUNTIME)//运行时检测 source为编译时检测
public @interface PermissionSucceed {
    public int requestCode();
}
