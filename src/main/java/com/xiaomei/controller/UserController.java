package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.entity.User;
import com.xiaomei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        return userService.login(user.getUserName(), user.getUserPasswd());
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        user.setCreateUserid(getCurrentUserId());
        user.setUpdateUserid(getCurrentUserId());
        if (user.getFactoryId() == null) {
            user.setFactoryId(getFactoryId());
        }
        return userService.register(user);
    }
}
