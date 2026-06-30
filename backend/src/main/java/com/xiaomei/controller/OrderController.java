package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.ProductOrder;
import com.xiaomei.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/list")
    public PageObject<ProductOrder> list(@RequestBody BaseQuery query) {
        if (query.getFactoryId() == null) query.setFactoryId(getFactoryId());
        return orderService.queryList(query);
    }

    @PostMapping("/detail")
    public ProductOrder detail(@RequestBody ProductOrder order) { return orderService.queryById(order.getId()); }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody ProductOrder order) {
        order.setCreateUserid(getCurrentUserId());
        order.setUpdateUserid(getCurrentUserId());
        if (order.getFactoryId() == null) order.setFactoryId(getFactoryId());
        return orderService.add(order);
    }

    @PostMapping("/accept")
    public Map<String, String> accept(@RequestBody ProductOrder order) {
        return orderService.accept(order.getId(), getCurrentUserId());
    }

    @PostMapping("/reject")
    public Map<String, String> reject(@RequestBody ProductOrder order) {
        return orderService.reject(order.getId(), getCurrentUserId());
    }

    @PostMapping("/complete")
    public Map<String, String> complete(@RequestBody ProductOrder order) {
        return orderService.complete(order.getId(), getCurrentUserId());
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestBody ProductOrder order) {
        return orderService.delete(order.getId(), getCurrentUserId());
    }
}
