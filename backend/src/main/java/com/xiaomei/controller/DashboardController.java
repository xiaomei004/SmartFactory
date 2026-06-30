package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/statistics")
    public Map<String, Object> statistics(@RequestBody Map<String, Integer> params) {
        Integer factoryId = params.getOrDefault("factoryId", getFactoryId());
        return dashboardService.getStatistics(factoryId);
    }
}
