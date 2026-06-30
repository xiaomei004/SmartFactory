package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.DailyWork;
import com.xiaomei.service.DailyWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dailyWork")
public class DailyWorkController extends BaseController {

    @Autowired
    private DailyWorkService dailyWorkService;

    @PostMapping("/list")
    public PageObject<DailyWork> list(@RequestBody BaseQuery query) {
        if (query.getFactoryId() == null) query.setFactoryId(getFactoryId());
        return dailyWorkService.queryList(query);
    }

    @PostMapping("/detail")
    public DailyWork detail(@RequestBody DailyWork work) { return dailyWorkService.queryById(work.getId()); }

    @PostMapping("/report")
    public Map<String, String> report(@RequestBody DailyWork work) {
        work.setCreateUserid(getCurrentUserId());
        work.setUpdateUserid(getCurrentUserId());
        if (work.getFactoryId() == null) work.setFactoryId(getFactoryId());
        return dailyWorkService.report(work);
    }

    @PostMapping("/completeReport")
    public Map<String, String> completeReport(@RequestBody DailyWork work) {
        return dailyWorkService.completeReport(work.getId(), getCurrentUserId());
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestBody DailyWork work) {
        return dailyWorkService.delete(work.getId(), getCurrentUserId());
    }
}
