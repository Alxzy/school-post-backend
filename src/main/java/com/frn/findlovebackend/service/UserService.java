package com.frn.findlovebackend.service;

import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-12-15 20:23:03
*/

public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long register(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @return 用户对象
     */
    User login(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);
}
