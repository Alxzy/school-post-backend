package com.frn.findlovebackend.service;
import java.util.Date;

import com.frn.findlovebackend.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;



/**
 * @author Administrator
 * @version 1.0
 * @date 2023-12-15 20:24
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    UserService userService;

    @Test
    public void testAdd(){
        User user = new User();
        user.setId(0L);
        user.setUserName("kid");
        user.setUserAccount("111111");
        user.setUserAvatar("");
        user.setGender(0);
        user.setUserRole("1");
        user.setUserPassword("");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        System.out.println(userService);
        userService.save(user);

    }
}