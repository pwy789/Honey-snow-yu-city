package com.pwy.common;

import com.pwy.enums.ResponseEnum;
import lombok.Data;

@Data
public class Result {

    private Integer code;

    private String message;

    private Object data;
    public static Result success(){
        Result r=new Result();
        r.code=200;

        return  r;
    }
    public static Result success(String message){
        Result r=new Result();
        r.code=200;
        r.message=message;
        return  r;
    }
    public static Result success(Object data){
        Result r=new Result();
        r.code=200;
        r.data=data;
        return  r;
    }
    public static  Result success(Object obj,String message){
        Result r=new Result();
        r.code=200;
        r.data=obj;
        r.message=message;
        return  r;
    }
    public static  Result success(Object obj, ResponseEnum re){
        Result r=new Result();
        r.code=re.getCode();
        r.data=obj;
        r.message=re.getMessage();
        return  r;
    }
    public static Result error(String message){
        Result r=new Result();
        r.code=404;
        r.data="";
        r.message=message;
        return  r;
    }
    public static Result error(ResponseEnum re){
        Result r=new Result();
        r.code=re.getCode();
        r.data="";
        r.message=re.getMessage();
        return r;
    }
}
