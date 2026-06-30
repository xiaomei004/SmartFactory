package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.ProductPlan;
import com.xiaomei.entity.ProductSchedule;
import com.xiaomei.mapper.ProductPlanMapper;
import com.xiaomei.mapper.ProductScheduleMapper;
import com.xiaomei.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ProductScheduleMapper scheduleMapper;
    @Autowired
    private ProductPlanMapper planMapper;

    @Override
    public PageObject<ProductSchedule> queryList(BaseQuery query) {
        Integer factoryId = query.getFactoryId();
        if (factoryId == null) return PageObject.error("工厂ID不能为空");
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<ProductSchedule> list = scheduleMapper.selectList(factoryId, offset, query.getPageSize());
        Long total = scheduleMapper.selectCount(factoryId);
        return new PageObject<>(list, query.getPageNum(), query.getPageSize(), total);
    }

    @Override
    public ProductSchedule queryById(Integer id) { return scheduleMapper.selectById(id); }

    @Override
    public Map<String, String> add(ProductSchedule schedule) {
        if (schedule.getPlanId() == null) return error("计划ID不能为空");
        if (schedule.getProductId() == null) return error("产品ID不能为空");
        if (schedule.getFactoryId() == null) return error("工厂ID不能为空");
        if (schedule.getScheduleSeq() == null || schedule.getScheduleSeq().trim().isEmpty()) {
            schedule.setScheduleSeq("PS" + System.currentTimeMillis());
        }
        if (schedule.getScheduleCount() == null) schedule.setScheduleCount(0);
        schedule.setScheduleStatus(StatusEnum.SCHEDULE_NOT_STARTED);
        scheduleMapper.insert(schedule);

        // 如果计划未启动则自动启动
        ProductPlan plan = planMapper.selectById(schedule.getPlanId());
        if (plan != null && plan.getPlanStatus() == StatusEnum.PLAN_NOT_STARTED) {
            plan.setPlanStatus(StatusEnum.PLAN_STARTED);
            plan.setUpdateUserid(schedule.getUpdateUserid());
            planMapper.update(plan);
        }
        return success("创建工单成功");
    }

    @Override
    public Map<String, String> start(Integer id, Integer updateUserid) {
        ProductSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) return error("工单不存在");
        if (schedule.getEquipmentId() == null) return error("请先分配设备");
        if (schedule.getScheduleStatus() != StatusEnum.SCHEDULE_NOT_STARTED) return error("只能启动未开始的工单");
        schedule.setScheduleStatus(StatusEnum.SCHEDULE_PRODUCING);
        schedule.setUpdateUserid(updateUserid);
        scheduleMapper.update(schedule);
        return success("工单已启动");
    }

    @Override
    public Map<String, String> complete(Integer id, Integer updateUserid) {
        ProductSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) return error("工单不存在");
        if (schedule.getScheduleStatus() != StatusEnum.SCHEDULE_PRODUCING) return error("只能完成生产中的工单");
        schedule.setScheduleStatus(StatusEnum.SCHEDULE_COMPLETED);
        schedule.setUpdateUserid(updateUserid);
        scheduleMapper.update(schedule);
        return success("工单已完成");
    }

    @Override
    public Map<String, String> assignEquipment(Integer id, Integer equipmentId, Integer updateUserid) {
        ProductSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) return error("工单不存在");
        schedule.setEquipmentId(equipmentId);
        schedule.setUpdateUserid(updateUserid);
        scheduleMapper.update(schedule);
        return success("设备分配成功");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        scheduleMapper.delete(id, updateUserid);
        return success("删除成功");
    }

    private Map<String, String> success(String msg) { return Map.of("status", "ok", "msg", msg); }
    private Map<String, String> error(String msg) { return Map.of("status", "error", "msg", msg); }
}
