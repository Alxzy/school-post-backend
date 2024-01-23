package com.frn.findlovebackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.frn.findlovebackend.annotation.AuthCheck;
import com.frn.findlovebackend.common.BaseResponse;
import com.frn.findlovebackend.common.DeleteRequest;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.common.ResultUtils;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.dto.*;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.model.vo.UserVO;
import com.frn.findlovebackend.service.UserService;
import com.frn.findlovebackend.service.impl.UserServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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

    // region 登录相关

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

    /**
     * 获取当前用户信息
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/currentUser")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest httpServletRequest){
        // 校验
        if(httpServletRequest == null){
            throw new BusinessException(PARAM_ERROR);
        }
        // 1.调用service层方法得到 currentUser
        User loginUser = userService.getLoginUser(httpServletRequest);
        // 2.封装脱敏对象
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(loginUser,userVO);
        // 3.返回
        return ResultUtils.success(userVO);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(PARAM_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    // endregion

    // region 管理员增删改查
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest){
        // 非空校验
        if(userAddRequest == null){
            throw new BusinessException(PARAM_ERROR);
        }
        // 1.拷贝数据到对象中
        User user = new User();
        // 简单说明一下这里为什么不加校验,毕竟是管理页面,没必要
        BeanUtils.copyProperties(userAddRequest,user);
        String verifiedPassword = DigestUtils.md5DigestAsHex((UserServiceImpl.SALT + user.getUserPassword()).getBytes(StandardCharsets.UTF_8));
        user.setUserPassword(verifiedPassword);
        // 2.添加
        boolean result = userService.save(user);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 3.返回
        return ResultUtils.success(user.getId());
    }

    @DeleteMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest){
        // 校验参数
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        Long id = deleteRequest.getId();
        if(id == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        boolean result = userService.removeById(id);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(result);
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateUserById(@RequestBody UserUpdateRequest updateRequest){
        // 校验参数
        if(updateRequest == null || updateRequest.getId() == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 1.拷贝数据到对象中
        User user = new User();
        // 简单说明一下这里为什么不加校验,毕竟是管理页面,没必要
        BeanUtils.copyProperties(updateRequest,user);
        String verifiedPassword = DigestUtils.md5DigestAsHex((UserServiceImpl.SALT + user.getUserPassword()).getBytes(StandardCharsets.UTF_8));
        user.setUserPassword(verifiedPassword);
        // 2.修改
        boolean result = userService.updateById(user);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 3.返回
        return ResultUtils.success(result);
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<UserVO> getUserById(int id,HttpServletRequest request){
        // 校验参数
        if(id < 0 || request == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 查询
        User user = userService.getById(id);
        if(user == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"没有对应的用户");
        }
        // 拷贝数据到对象中
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        // 返回
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     * @param userQueryRequest
     * @return
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = "admin")
    // todo 顶级神坑 不要问我怎么知道的，一晚上  如果要使用Get就不要使用 RequstBody 注解！！ 不然报错搞死你
    public BaseResponse<List<UserVO>> listUsers(UserQueryRequest userQueryRequest){
        // 1.校验参数
        if(userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.赋值,相似查询
        User userQuery = new User();
        BeanUtils.copyProperties(userQueryRequest,userQuery);
        // 3.查询
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>(userQuery);
        List<User> users = userService.list(userQueryWrapper);
        // 4.修改数据类型为 UserVO
        List<UserVO> userVOList = users.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());

        // 5.返回
        return ResultUtils.success(userVOList);
    }

    @GetMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserVO>> listUsersByPage(UserQueryRequest userQueryRequest){
        // 默认值
        long current = 1;
        long size = 10;

        // 1.校验参数,并赋值
        User userQuery = new User();
        if(userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }else{
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        // 2.查询
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), userQueryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        // 3.修改数据类型为 UserVO
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);

    }

    // endregion









}
