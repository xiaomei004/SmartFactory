package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.ProductOrder;
import com.xiaomei.mapper.ProductOrderMapper;
import com.xiaomei.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderMapper orderMapper;

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
        order.setOrderStatus(StatusEnum.ORDER_ACCEPTED);
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
        order.setOrderStatus(StatusEnum.ORDER_COMPLETED);
        order.setUpdateUserid(updateUserid);
        orderMapper.updateStatus(order);
        return success("订单完成");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        orderMapper.delete(id, updateUserid);
        return success("删除成功");
    }

    private Map<String, String> success(String msg) { return Map.of("status", "ok", "msg", msg); }
    private Map<String, String> error(String msg) { return Map.of("status", "error", "msg", msg); }
}
