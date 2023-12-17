package com.frn.findlovebackend.controller;

import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.dto.UserLoginRequest;
import com.frn.findlovebackend.model.dto.UserRegisterRequest;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.frn.findlovebackend.common.ErrorCode.PARAM_ERROR;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 20:37
 * 用户模块接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
//            return ResultUtils.error(PARAM_ERROR);
            throw new BusinessException(PARAM_ERROR);
        }
        // 1.取出数据
        String userPassword = userRegisterRequest.getUserPassword();
        String userAccount = userRegisterRequest.getUserAccount();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 2.非空校验
        if(userPassword == null || userAccount == null || checkPassword == null){
            throw new BusinessException(PARAM_ERROR);
//            return ResultUtils.error(PARAM_ERROR);
        }
        // 3.调用业务方法
        long userId = userService.register(userAccount, userPassword, checkPassword);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> register(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest httpServletRequest){
        if(userLoginRequest == null){
            throw new BusinessException(PARAM_ERROR);
        }
        // 1.取出数据
        String userPassword = userLoginRequest.getUserPassword();
        String userAccount = userLoginRequest.getUserAccount();

        // 2.非空校验
        if(userPassword == null || userAccount == null){
            throw new BusinessException(PARAM_ERROR);
//            return ResultUtils.error(PARAM_ERROR);
        }
        // 3.调用业务方法
        User user = userService.login(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user);
    }






}
