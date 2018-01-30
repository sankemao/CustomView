package com.pronetway.customview.test;

/**
 * Description:TODO
 * Create Time: 2017/12/27.9:51
 * Author:jin
 * Email:210980059@qq.com
 */
public class Father {
    public void methodA() {
        methodB();
    }

    public void methodB() {
        System.out.println("测试, 我在Father中");
    }
}
