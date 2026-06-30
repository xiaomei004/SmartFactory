package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.entity.Factory;
import com.xiaomei.service.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/factory")
public class FactoryController extends BaseController {

    @Autowired
    private FactoryService factoryService;

    @PostMapping("/detail")
    public Factory detail(@RequestBody Factory factory) {
        return factoryService.queryById(factory.getId());
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Factory factory) {
        factory.setCreateUserid(getCurrentUserId());
        factory.setUpdateUserid(getCurrentUserId());
        return factoryService.register(factory);
    }
}
