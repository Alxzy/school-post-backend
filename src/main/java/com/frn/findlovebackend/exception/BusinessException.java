package com.frn.findlovebackend.exception;

import com.frn.findlovebackend.common.ErrorCode;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 21:06
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {

    // 状态码
    private final int code;

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }
    // 没有详细信息的错误异常类
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    // 有详细信息的错误异常类
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }


}
