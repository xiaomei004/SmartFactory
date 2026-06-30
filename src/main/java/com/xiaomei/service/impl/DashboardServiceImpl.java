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

    @Autowired private EquipmentMapper equipmentMapper;
    @Autowired private ProductOrderMapper orderMapper;
    @Autowired private ProductPlanMapper planMapper;
    @Autowired private ProductScheduleMapper scheduleMapper;

    @Override
    public Map<String, Object> getStatistics(Integer factoryId) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Equipment stats
        List<Equipment> allEquip = equipmentMapper.selectByStatus(StatusEnum.EQUIPMENT_ENABLED);
        int enabled = allEquip.size();
        int disabled = equipmentMapper.selectByStatus(StatusEnum.EQUIPMENT_DISABLED).size();
        int fault = equipmentMapper.selectByStatus(StatusEnum.EQUIPMENT_FAULT).size();
        int totalEquip = enabled + disabled + fault;

        // Equipment with active schedules
        int producingEquip = 0;
        int standbyEquip = 0;
        for (Equipment e : allEquip) {
            int activeCount = equipmentMapper.countActiveSchedulesByEquipment(e.getId());
            if (activeCount > 0) producingEquip++; else standbyEquip++;
        }

        Map<String, Object> equipmentStats = new LinkedHashMap<>();
        equipmentStats.put("total", totalEquip);
        equipmentStats.put("enabled", enabled);
        equipmentStats.put("disabled", disabled);
        equipmentStats.put("fault", fault);
        equipmentStats.put("producing", producingEquip);
        equipmentStats.put("standby", standbyEquip);
        equipmentStats.put("startupRate", totalEquip > 0 ? Math.round((producingEquip + standbyEquip) * 100.0 / totalEquip) : 0);
        equipmentStats.put("faultRate", totalEquip > 0 ? Math.round(fault * 100.0 / totalEquip) : 0);
        equipmentStats.put("runningRate", totalEquip > 0 ? Math.round(producingEquip * 100.0 / totalEquip) : 0);
        equipmentStats.put("efficiency", enabled > 0 ? Math.round(producingEquip * 100.0 / enabled) : 0);
        result.put("equipment", equipmentStats);

        // Order stats by status
        int offset = 0;
        int pageSize = 10000;
        // Count by scanning all orders (simplified)
        List<ProductOrder> allOrders = orderMapper.selectList(factoryId, 0, pageSize);
        long notAccepted = allOrders.stream().filter(o -> o.getOrderStatus() == StatusEnum.ORDER_NOT_ACCEPTED).count();
        long accepted = allOrders.stream().filter(o -> o.getOrderStatus() == StatusEnum.ORDER_ACCEPTED).count();
        long rejected = allOrders.stream().filter(o -> o.getOrderStatus() == StatusEnum.ORDER_REJECTED).count();
        long producing = allOrders.stream().filter(o -> o.getOrderStatus() == StatusEnum.ORDER_PRODUCING).count();
        long completed = allOrders.stream().filter(o -> o.getOrderStatus() == StatusEnum.ORDER_COMPLETED).count();
        Map<String, Long> orderStats = new LinkedHashMap<>();
        orderStats.put("notAccepted", notAccepted);
        orderStats.put("accepted", accepted);
        orderStats.put("rejected", rejected);
        orderStats.put("producing", producing);
        orderStats.put("completed", completed);
        orderStats.put("total", (long) allOrders.size());
        result.put("order", orderStats);

        // Yearly summary (month by month from actual data)
        List<ProductPlan> allPlans = planMapper.selectList(factoryId, 0, pageSize);
        List<ProductSchedule> allSchedules = scheduleMapper.selectList(factoryId, 0, pageSize);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        List<Map<String, Object>> months = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            int month = m;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", year + "-" + String.format("%02d", m));
            item.put("orders", allOrders.stream().filter(o -> {
                if (o.getCreateTime() == null) return false;
                Calendar c = Calendar.getInstance(); c.setTime(o.getCreateTime());
                return c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) + 1 == month;
            }).count());
            item.put("plans", allPlans.stream().filter(p -> {
                if (p.getCreateTime() == null) return false;
                Calendar c = Calendar.getInstance(); c.setTime(p.getCreateTime());
                return c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) + 1 == month;
            }).count());
            item.put("schedules", allSchedules.stream().filter(s -> {
                if (s.getCreateTime() == null) return false;
                Calendar c = Calendar.getInstance(); c.setTime(s.getCreateTime());
                return c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) + 1 == month;
            }).count());
            months.add(item);
        }
        result.put("yearlySummary", months);
        result.put("status", "ok");
        return result;
    }
}
