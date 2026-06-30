package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.ProductOrder;
import com.xiaomei.entity.ProductPlan;
import com.xiaomei.mapper.ProductOrderMapper;
import com.xiaomei.mapper.ProductPlanMapper;
import com.xiaomei.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private ProductPlanMapper planMapper;
    @Autowired
    private ProductOrderMapper orderMapper;

    @Override
    public PageObject<ProductPlan> queryList(BaseQuery query) {
        Integer factoryId = query.getFactoryId();
        if (factoryId == null) return PageObject.error("工厂ID不能为空");
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<ProductPlan> list = planMapper.selectList(factoryId, offset, query.getPageSize());
        Long total = planMapper.selectCount(factoryId);
        return new PageObject<>(list, query.getPageNum(), query.getPageSize(), total);
    }

    @Override
    public ProductPlan queryById(Integer id) { return planMapper.selectById(id); }

    @Override
    @Transactional
    public Map<String, String> add(ProductPlan plan) {
        if (plan.getOrderId() == null) return error("订单ID不能为空");
        if (plan.getProductId() == null) return error("产品ID不能为空");
        if (plan.getFactoryId() == null) return error("工厂ID不能为空");
        if (plan.getPlanSeq() == null || plan.getPlanSeq().trim().isEmpty()) {
            plan.setPlanSeq("P" + System.currentTimeMillis());
        }
        if (plan.getPlanCount() == null) plan.setPlanCount(0);
        plan.setPlanStatus(StatusEnum.PLAN_NOT_STARTED);
        planMapper.insert(plan);

        // 将订单状态改为生产中
        ProductOrder order = orderMapper.selectById(plan.getOrderId());
        if (order != null && order.getOrderStatus() == StatusEnum.ORDER_ACCEPTED) {
            order.setOrderStatus(StatusEnum.ORDER_PRODUCING);
            order.setUpdateUserid(plan.getUpdateUserid());
            orderMapper.updateStatus(order);
        }
        return success("创建计划成功");
    }

    @Override
    public Map<String, String> start(Integer id, Integer updateUserid) {
        ProductPlan plan = planMapper.selectById(id);
        if (plan == null) return error("计划不存在");
        if (plan.getPlanStatus() != StatusEnum.PLAN_NOT_STARTED) return error("只能启动未启动的计划");
        plan.setPlanStatus(StatusEnum.PLAN_STARTED);
        plan.setUpdateUserid(updateUserid);
        planMapper.update(plan);
        return success("计划已启动");
    }

    @Override
    public Map<String, String> complete(Integer id, Integer updateUserid) {
        ProductPlan plan = planMapper.selectById(id);
        if (plan == null) return error("计划不存在");
        if (plan.getPlanStatus() != StatusEnum.PLAN_STARTED) return error("只能完成已启动的计划");
        plan.setPlanStatus(StatusEnum.PLAN_COMPLETED);
        plan.setUpdateUserid(updateUserid);
        planMapper.update(plan);
        return success("计划已完成");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        planMapper.delete(id, updateUserid);
        return success("删除成功");
    }

    private Map<String, String> success(String msg) { return Map.of("status", "ok", "msg", msg); }
    private Map<String, String> error(String msg) { return Map.of("status", "error", "msg", msg); }
}
