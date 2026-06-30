package com.xiaomei.service.impl;

import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.Factory;
import com.xiaomei.mapper.FactoryMapper;
import com.xiaomei.service.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FactoryServiceImpl implements FactoryService {

    @Autowired
    private FactoryMapper factoryMapper;

    @Override
    public Factory queryById(Integer id) {
        return factoryMapper.selectById(id);
    }

    @Override
    public Map<String, String> register(Factory factory) {
        if (factory.getFactoryName() == null || factory.getFactoryName().trim().isEmpty()) {
            return error("工厂名称不能为空");
        }
        factory.setFactoryStatus(StatusEnum.FACTORY_NORMAL);
        factoryMapper.insert(factory);
        return success("注册成功");
    }

    private Map<String, String> success(String msg) {
        return Map.of("status", "ok", "msg", msg);
    }

    private Map<String, String> error(String msg) {
        return Map.of("status", "error", "msg", msg);
    }
}
