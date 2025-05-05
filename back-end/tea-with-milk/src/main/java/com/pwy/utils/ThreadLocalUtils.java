package com.pwy.utils;

public class ThreadLocalUtils {
    private static   final  ThreadLocal THREAD_LOCAL=new ThreadLocal<>();

    public static void set(Object val){
        THREAD_LOCAL.set(val);

    }
    public static Object get(){
        return  THREAD_LOCAL.get();
    }
    public static  void  remove(){
        THREAD_LOCAL.remove();
    }
}
