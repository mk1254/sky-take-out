package com.sky.context;

public class BaseContext {

    //ThreadLocal是Thread的局部变量,会给每个线程提供一份单独的存储空间,只有线程内才能获取对应的值
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
     // 需要在请求/线程结束时手动remove
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
