package com.usr.common;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回通用结果，服务响应数据均为此项
 * @param <T>
 */
@Data
public class R<T>{
    private Integer code;// 编码1成功 ，0或其他失败

    private String msg; // 错误信息

    private T data;// 数据

    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object){
        R<T> r = new R<>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
