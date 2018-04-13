package com.pronetway.customview;

/**
 * Description:TODO
 * Create Time: 2018/4/10.16:05
 * Author:jin
 * Email:210980059@qq.com
 */
public class Student {
    public String name;
    public int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return name + " " + age;
    }
}
