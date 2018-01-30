package com.pronetway.customview.test.threadtest;

/**
 * Description:TODO
 * Create Time: 2018/1/8.9:51
 * Author:jin
 * Email:210980059@qq.com
 */
public class Simple1 {

    public static void main(String[] args) throws InterruptedException {
        Object obj = new Object();
        Object lock = new Object();
        synchronized (obj) {
            System.out.println("wait之前");
            obj.wait();
            System.out.println("wait之后");
            obj.notifyAll();
            System.out.println("notifyAll之后");
        }
    }
}
