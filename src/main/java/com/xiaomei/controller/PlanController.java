package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.ProductPlan;
import com.xiaomei.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/plan")
public class PlanController extends BaseController {

    @Autowired
    private PlanService planService;

    @PostMapping("/list")
    public PageObject<ProductPlan> list(@RequestBody BaseQuery query) {
        if (query.getFactoryId() == null) query.setFactoryId(getFactoryId());
        return planService.queryList(query);
    }

    @PostMapping("/detail")
    public ProductPlan detail(@RequestBody ProductPlan plan) { return planService.queryById(plan.getId()); }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody ProductPlan plan) {
        plan.setCreateUserid(getCurrentUserId());
        plan.setUpdateUserid(getCurrentUserId());
        if (plan.getFactoryId() == null) plan.setFactoryId(getFactoryId());
        return planService.add(plan);
    }

    @PostMapping("/start")
    public Map<String, String> start(@RequestBody ProductPlan plan) {
        return planService.start(plan.getId(), getCurrentUserId());
    }

    @PostMapping("/complete")
    public Map<String, String> complete(@RequestBody ProductPlan plan) {
        return planService.complete(plan.getId(), getCurrentUserId());
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestBody ProductPlan plan) {
        return planService.delete(plan.getId(), getCurrentUserId());
    }
}
