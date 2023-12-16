package com.frn.findlovebackend.service;

import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
