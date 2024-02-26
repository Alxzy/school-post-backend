package com.frn.findlovebackend.common;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 20:54
 * 错误码
 */
public enum ErrorCode {
    SUCCESS(0,"ok"),
    PARAM_ERROR(40000,"请求参数错误"),
    NULL_ERROR(40001,"请求数据为空"),
    NOT_LOGIN(40100,"未登录"),
    NO_AUTO(40101,"无权限"),

    OPERATION_ERROR(40102,"操作有误"),
    NOT_FOUND_ERROR(40103,"操作对象不存在"),
    SYSTEM_ERROR(50000,"系统内部异常");
    /**
     * 错误码
     */
    private final int code;
    /**
     * 状态码的信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
