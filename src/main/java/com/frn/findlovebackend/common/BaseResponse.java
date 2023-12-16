package com.frn.findlovebackend.common;

import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 20:59
 * 全局通用返回类
 */
@Data
public class BaseResponse<T> {
    // 状态码
    private int code;
    // 数据
    private T data;
    // 信息
    private String message;

    public BaseResponse() {
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    // 成功
    public BaseResponse(int code, T data) {
        this(code,data,"");
    }
    // 失败
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage());
    }

}
