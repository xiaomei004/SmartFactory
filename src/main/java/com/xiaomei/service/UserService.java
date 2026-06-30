package com.xiaomei.service;

import com.xiaomei.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(String userName, String userPasswd);
    Map<String, String> register(User user);
}
