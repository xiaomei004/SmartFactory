package com.xiaomei.service.impl;

import com.xiaomei.entity.User;
import com.xiaomei.mapper.UserMapper;
import com.xiaomei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> login(String userName, String userPasswd) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.login(userName, userPasswd);
        if (user == null) {
            result.put("status", "error");
            result.put("msg", "用户名或密码错误");
            return result;
        }
        user.setUserPasswd(null); // 不返回密码
        result.put("status", "ok");
        result.put("msg", "登录成功");
        result.put("user", user);
        return result;
    }

    @Override
    public Map<String, String> register(User user) {
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            return error("用户名不能为空");
        }
        if (user.getUserPasswd() == null || user.getUserPasswd().trim().isEmpty()) {
            return error("密码不能为空");
        }
        if (user.getFactoryId() == null) {
            return error("工厂ID不能为空");
        }
        User exist = userMapper.selectByUserName(user.getUserName());
        if (exist != null) {
            return error("用户名已存在");
        }
        userMapper.insert(user);
        return success("注册成功");
    }

    private Map<String, String> success(String msg) {
        return Map.of("status", "ok", "msg", msg);
    }

    private Map<String, String> error(String msg) {
        return Map.of("status", "error", "msg", msg);
    }
}
