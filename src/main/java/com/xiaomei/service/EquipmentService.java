package com.xiaomei.service;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.Equipment;
import com.xiaomei.entity.EquipmentProduct;

import java.util.List;
import java.util.Map;

public interface EquipmentService {
    PageObject<Equipment> queryList(BaseQuery query);
    Equipment queryById(Integer id);
    List<EquipmentProduct> getProducts(Integer equipmentId);
    Map<String, String> add(Equipment equipment);
    Map<String, String> edit(Equipment equipment);
    Map<String, String> delete(Integer id, Integer updateUserid);
    Map<String, String> setProducts(Integer equipmentId, Integer[] productIds, Integer factoryId);
}
