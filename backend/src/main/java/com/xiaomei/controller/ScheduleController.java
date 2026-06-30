package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.ProductSchedule;
import com.xiaomei.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController extends BaseController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/list")
    public PageObject<ProductSchedule> list(@RequestBody BaseQuery query) {
        if (query.getFactoryId() == null) query.setFactoryId(getFactoryId());
        return scheduleService.queryList(query);
    }

    @PostMapping("/detail")
    public ProductSchedule detail(@RequestBody ProductSchedule schedule) {
        return scheduleService.queryById(schedule.getId());
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody ProductSchedule schedule) {
        schedule.setCreateUserid(getCurrentUserId());
        schedule.setUpdateUserid(getCurrentUserId());
        if (schedule.getFactoryId() == null) schedule.setFactoryId(getFactoryId());
        return scheduleService.add(schedule);
    }

    @PostMapping("/start")
    public Map<String, String> start(@RequestBody ProductSchedule schedule) {
        return scheduleService.start(schedule.getId(), getCurrentUserId());
    }

    @PostMapping("/complete")
    public Map<String, String> complete(@RequestBody ProductSchedule schedule) {
        return scheduleService.complete(schedule.getId(), getCurrentUserId());
    }

    @PostMapping("/assignEquipment")
    public Map<String, String> assignEquipment(@RequestBody Map<String, Integer> params) {
        return scheduleService.assignEquipment(params.get("id"), params.get("equipmentId"), getCurrentUserId());
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestBody ProductSchedule schedule) {
        return scheduleService.delete(schedule.getId(), getCurrentUserId());
    }
}
