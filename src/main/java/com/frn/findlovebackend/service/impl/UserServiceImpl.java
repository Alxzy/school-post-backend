package com.frn.findlovebackend.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frn.findlovebackend.common.ErrorCode;
import com.frn.findlovebackend.exception.BusinessException;
import com.frn.findlovebackend.model.entity.User;
import com.frn.findlovebackend.service.UserService;
import com.frn.findlovebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.frn.findlovebackend.constant.UserConstant.ADMIN_ROLE;
import static com.frn.findlovebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-12-15 20:23:03
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Resource
    UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    private final String SALT = "furina";
    @Override
    public long register(String userAccount, String userPassword, String checkPassword) {
       // 1.校验数据
        // 非空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        checkAccountAndPassword(userAccount,userPassword);
        // 密码和校验密码相同
        if(!checkPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"两次输入的密码不一致");
        }
        // 上悲观锁,保证单服务器的情况下的线程安全
        synchronized (userAccount.intern()){
            // 账号不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount",userAccount);
            if(userMapper.selectCount(queryWrapper) > 0){
                throw new BusinessException(ErrorCode.PARAM_ERROR,"账号不能重复");
            }
            // 2.数据加密
            String verifyPassword =
                    DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
            // 3.插入用户数据，返回新用户id
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(verifyPassword);
            boolean isSuccess = save(user);
            if(!isSuccess) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败,数据库发送错误");
            }
            return user.getId();
        }
    }

    @Override
    public User login(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        // 1.校验数据
        // 非空
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"参数为空");
        }
        checkAccountAndPassword(userAccount,userPassword);
        // 2.校验密码
        String verifyPassword =
                DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",verifyPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed, userAccount Cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "用户不存在或密码错误");
        }
        // 3.用户信息脱敏
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setUserAvatar(user.getUserAvatar());
        safetyUser.setGender(user.getGender());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setCreateTime(new Date());


        // 4.记录用户登录态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
    // 校验账号和密码
    public void checkAccountAndPassword(String userAccount,String userPassword){
        // 1.校验账号密码是否合法

        // 账户长度不小于 4 位
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号长度过短");
        }
        // 密码不小于 8 位
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"密码长度过短");
        }
        // 账号不包含特殊字符
        String validRule = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%…… &*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validRule).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAM_ERROR,"账号不能包含特殊字符");
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }


}




