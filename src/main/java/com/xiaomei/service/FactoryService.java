package com.xiaomei.service;

import com.xiaomei.entity.Factory;

import java.util.Map;

public interface FactoryService {
    Factory queryById(Integer id);
    Map<String, String> register(Factory factory);
}
