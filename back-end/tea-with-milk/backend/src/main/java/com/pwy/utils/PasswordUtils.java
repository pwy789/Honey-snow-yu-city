package com.pwy.utils;
import cn.hutool.crypto.digest.BCrypt;

public class PasswordUtils {


    //得到加盐后的hash密码
    public static  String getHashSaltedPassword(String originPassword){

        return BCrypt.hashpw(originPassword,BCrypt.gensalt());
    }
   //验证密码 形参1为前端传来的密码 形参2为从数据库中取到的加盐hash密码
    public  static  boolean verifyPassword(String password,String storedPassword){
    return  BCrypt.checkpw(password,storedPassword);
    }
}
