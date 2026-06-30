package com.xiaomei.service;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.ProductSchedule;

import java.util.Map;

public interface ScheduleService {
    PageObject<ProductSchedule> queryList(BaseQuery query);
    ProductSchedule queryById(Integer id);
    Map<String, String> add(ProductSchedule schedule);
    Map<String, String> start(Integer id, Integer updateUserid);
    Map<String, String> complete(Integer id, Integer updateUserid);
    Map<String, String> assignEquipment(Integer id, Integer equipmentId, Integer updateUserid);
    Map<String, String> delete(Integer id, Integer updateUserid);
}
