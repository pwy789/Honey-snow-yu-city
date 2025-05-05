package com.pwy.enums;



public enum ResponseEnum {
    USER_NOT_EXIST(401,"用户不存在!"),
    PASSWORD_ERROR(402,"密码错误!"),
    LOGIN_SUCCESS(200,"登陆成功"),
    GET_DATA_SUCCESS(200,"获取数据成功 ")
    ;

    private final int code;
    private final String message;

    ResponseEnum(int code, String message) {
        this.code=code;
        this.message=message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
