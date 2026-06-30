package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.EquipmentProduct;
import com.xiaomei.entity.OrderTrack;
import com.xiaomei.entity.ProductOrder;
import com.xiaomei.entity.ProductPlan;
import com.xiaomei.entity.ProductSchedule;
import com.xiaomei.mapper.EquipmentProductMapper;
import com.xiaomei.mapper.OrderTrackMapper;
import com.xiaomei.mapper.ProductOrderMapper;
import com.xiaomei.mapper.ProductPlanMapper;
import com.xiaomei.mapper.ProductScheduleMapper;
import com.xiaomei.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderMapper orderMapper;
    @Autowired
    private EquipmentProductMapper equipmentProductMapper;
    @Autowired
    private ProductPlanMapper planMapper;
    @Autowired
    private ProductScheduleMapper scheduleMapper;
    @Autowired
    private OrderTrackMapper orderTrackMapper;

    @Override
    public PageObject<ProductOrder> queryList(BaseQuery query) {
        Integer factoryId = query.getFactoryId();
        if (factoryId == null) return PageObject.error("工厂ID不能为空");
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<ProductOrder> list = orderMapper.selectList(factoryId, offset, query.getPageSize());
        Long total = orderMapper.selectCount(factoryId);
        return new PageObject<>(list, query.getPageNum(), query.getPageSize(), total);
    }

    @Override
    public ProductOrder queryById(Integer id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Map<String, String> add(ProductOrder order) {
        if (order.getProductId() == null) return error("产品ID不能为空");
        if (order.getProductCount() == null || order.getProductCount() <= 0) return error("产品数量必须大于0");
        if (order.getEndDate() == null) return error("截止日期不能为空");
        if (order.getFactoryId() == null) return error("工厂ID不能为空");
        if (order.getOrderSeq() == null || order.getOrderSeq().trim().isEmpty()) {
            order.setOrderSeq("ORD" + System.currentTimeMillis());
        }
        order.setOrderStatus(StatusEnum.ORDER_NOT_ACCEPTED);
        orderMapper.insert(order);
        return success("下单成功");
    }

    @Override
    public Map<String, String> accept(Integer id, Integer updateUserid) {
        ProductOrder order = orderMapper.selectById(id);
        if (order == null) return error("订单不存在");
        if (order.getOrderStatus() != StatusEnum.ORDER_NOT_ACCEPTED) return error("只能接未接单的订单");

        // 产能校验：该产品关联的所有设备产能之和 >= 订单数量
        List<EquipmentProduct> eps = equipmentProductMapper.selectByProductId(order.getProductId());
        int totalCapacity = 0;
        for (EquipmentProduct ep : eps) {
            if (ep.getYield() != null) totalCapacity += ep.getYield();
        }
        if (totalCapacity > 0 && totalCapacity < order.getProductCount()) {
            return error("产能不足：可用产能" + totalCapacity + "，订单需求" + order.getProductCount());
        }

        order.setOrderStatus(StatusEnum.ORDER_ACCEPTED);
        order.setFactoryYield(totalCapacity);
        order.setUpdateUserid(updateUserid);
        orderMapper.updateStatus(order);
        return success("接单成功");
    }

    @Override
    public Map<String, String> reject(Integer id, Integer updateUserid) {
        ProductOrder order = orderMapper.selectById(id);
        if (order == null) return error("订单不存在");
        if (order.getOrderStatus() != StatusEnum.ORDER_NOT_ACCEPTED) return error("只能拒绝未接单的订单");
        order.setOrderStatus(StatusEnum.ORDER_REJECTED);
        order.setUpdateUserid(updateUserid);
        orderMapper.updateStatus(order);
        return success("已拒绝");
    }

    @Override
    public Map<String, String> complete(Integer id, Integer updateUserid) {
        ProductOrder order = orderMapper.selectById(id);
        if (order == null) return error("订单不存在");
        if (order.getOrderStatus() != StatusEnum.ORDER_PRODUCING) return error("只能完成生产中的订单");

        // 校验：已完成数量 >= 订单数量（从订单跟踪表累计）
        List<ProductPlan> plans = planMapper.selectByOrderId(id);
        int totalQualified = 0;
        for (ProductPlan plan : plans) {
            List<ProductSchedule> schedules = scheduleMapper.selectByPlanId(plan.getId());
            for (ProductSchedule sch : schedules) {
                List<OrderTrack> tracks = orderTrackMapper.selectByScheduleId(sch.getId());
                for (OrderTrack t : tracks) {
                    if (t.getQualifiedCount() != null) totalQualified += t.getQualifiedCount();
                }
            }
        }
        if (totalQualified < order.getProductCount()) {
            return error("合格数量不足：已完成" + totalQualified + "，订单需求" + order.getProductCount());
        }

        // 级联完成所有关联计划和工单
        for (ProductPlan plan : plans) {
            if (plan.getPlanStatus() != StatusEnum.PLAN_COMPLETED) {
                plan.setPlanStatus(StatusEnum.PLAN_COMPLETED);
                plan.setUpdateUserid(updateUserid);
                planMapper.update(plan);
            }
            List<ProductSchedule> schedules = scheduleMapper.selectByPlanId(plan.getId());
            for (ProductSchedule sch : schedules) {
                if (sch.getScheduleStatus() != StatusEnum.SCHEDULE_COMPLETED) {
                    sch.setScheduleStatus(StatusEnum.SCHEDULE_COMPLETED);
                    sch.setUpdateUserid(updateUserid);
                    scheduleMapper.update(sch);
                }
            }
        }

        order.setOrderStatus(StatusEnum.ORDER_COMPLETED);
        order.setUpdateUserid(updateUserid);
        orderMapper.updateStatus(order);
        return success("订单完成");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        ProductOrder order = orderMapper.selectById(id);
        if (order == null) return error("订单不存在");
        if (order.getOrderStatus() == StatusEnum.ORDER_PRODUCING) return error("生产中的订单不可删除");
        orderMapper.delete(id, updateUserid);
        return success("删除成功");
    }

    private Map<String, String> success(String msg) { return Map.of("status", "ok", "msg", msg); }
    private Map<String, String> error(String msg) { return Map.of("status", "error", "msg", msg); }
}
