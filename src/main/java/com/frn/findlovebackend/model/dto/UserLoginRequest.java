package com.frn.findlovebackend.model.dto;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-17 21:31
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -7008341085765156738L;
    private String userAccount;

    private String userPassword;


}
