package com.mit.fabricsdk.dto;

import lombok.Data;

/**
 * @author Haodong Li
 * @date 2023年05月24日 14:01
 */
@Data
public class BaseResponse<T> {
    public static Integer SUCCESS = 200;
    public static Integer ERROR = 400;
    private Integer code = SUCCESS;
    private T data;
    private String msg;

    public BaseResponse(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public BaseResponse(T data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public BaseResponse(T data) {
        this(data, "");
    }

    public BaseResponse(String msg) {
        this(null, msg);
    }

}

