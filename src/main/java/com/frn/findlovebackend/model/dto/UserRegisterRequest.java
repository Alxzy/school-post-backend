package com.frn.findlovebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 20:41
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable{

    private static final long serialVersionUID = 6304460216845072079L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
