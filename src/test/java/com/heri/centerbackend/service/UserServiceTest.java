package com.heri.centerbackend.service;

import com.heri.centerbackend.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * 用户测试
 *
 * @author heri
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAdduser(){
        User user = new User();
        user.setUsername("whatDay");
        user.setUserAccount("123");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setPlanetCode("ss");
        boolean result = userService.save(user);

    }

    @Test
    void testRegister(){
        String userAccount = "heri";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        userService.userRegister(userAccount, userPassword, checkPassword);

    }

}