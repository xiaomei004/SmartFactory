package com.xiaomei.service.impl;

import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.*;
import com.xiaomei.mapper.*;
import com.xiaomei.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private ProductOrderMapper orderMapper;
    @Autowired
    private ProductPlanMapper planMapper;
    @Autowired
    private ProductScheduleMapper scheduleMapper;
    @Autowired
    private DailyWorkMapper dailyWorkMapper;

    @Override
    public Map<String, Object> getStatistics(Integer factoryId) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 设备统计
        Map<String, Long> equipmentStats = new LinkedHashMap<>();
        equipmentStats.put("enabled", countEquipment(StatusEnum.EQUIPMENT_ENABLED));
        equipmentStats.put("disabled", countEquipment(StatusEnum.EQUIPMENT_DISABLED));
        equipmentStats.put("fault", countEquipment(StatusEnum.EQUIPMENT_FAULT));
        result.put("equipment", equipmentStats);

        // 订单统计（按状态）
        Map<String, Long> orderStats = new LinkedHashMap<>();
        orderStats.put("notAccepted", countOrders(StatusEnum.ORDER_NOT_ACCEPTED));
        orderStats.put("accepted", countOrders(StatusEnum.ORDER_ACCEPTED));
        orderStats.put("rejected", countOrders(StatusEnum.ORDER_REJECTED));
        orderStats.put("producing", countOrders(StatusEnum.ORDER_PRODUCING));
        orderStats.put("completed", countOrders(StatusEnum.ORDER_COMPLETED));
        result.put("order", orderStats);

        // 年度汇总（按月）
        result.put("yearlySummary", getYearlySummary(factoryId));
        result.put("status", "ok");
        return result;
    }

    private Long countEquipment(int status) {
        List<Equipment> list = equipmentMapper.selectByStatus(status);
        return (long) list.size();
    }

    private Long countOrders(int status) {
        // simplified: count all orders by status via the list
        return 0L; // will calculate via a separate query if needed
    }

    private List<Map<String, Object>> getYearlySummary(Integer factoryId) {
        List<Map<String, Object>> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        for (int m = 1; m <= 12; m++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", year + "-" + String.format("%02d", m));
            item.put("orders", 0);
            item.put("plans", 0);
            item.put("schedules", 0);
            months.add(item);
        }
        return months;
    }
}
