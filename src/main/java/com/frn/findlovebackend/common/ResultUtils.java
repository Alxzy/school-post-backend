package com.frn.findlovebackend.common;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 21:14
 */
public class ResultUtils {
    // 成功
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<T>(0,data,"ok");
    }
    // 失败
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse error(int code,String message){
        return new BaseResponse<>(code,null,message);
    }

    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }
}
