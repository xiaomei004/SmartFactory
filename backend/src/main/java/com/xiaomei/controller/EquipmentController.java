package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.Equipment;
import com.xiaomei.entity.EquipmentProduct;
import com.xiaomei.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/equipment")
public class EquipmentController extends BaseController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping("/list")
    public PageObject<Equipment> list(@RequestBody BaseQuery query) {
        if (query.getFactoryId() == null) {
            query.setFactoryId(getFactoryId());
        }
        return equipmentService.queryList(query);
    }

    @PostMapping("/detail")
    public Equipment detail(@RequestBody Equipment equipment) {
        return equipmentService.queryById(equipment.getId());
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody Equipment equipment) {
        equipment.setCreateUserid(getCurrentUserId());
        equipment.setUpdateUserid(getCurrentUserId());
        if (equipment.getFactoryId() == null) {
            equipment.setFactoryId(getFactoryId());
        }
        return equipmentService.add(equipment);
    }

    @PostMapping("/edit")
    public Map<String, String> edit(@RequestBody Equipment equipment) {
        equipment.setUpdateUserid(getCurrentUserId());
        return equipmentService.edit(equipment);
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestBody Equipment equipment) {
        return equipmentService.delete(equipment.getId(), getCurrentUserId());
    }

    @PostMapping("/products")
    public List<EquipmentProduct> products(@RequestBody Equipment equipment) {
        return equipmentService.getProducts(equipment.getId());
    }

    @PostMapping("/setProducts")
    public Map<String, String> setProducts(@RequestBody Map<String, Object> params) {
        Integer equipmentId = (Integer) params.get("equipmentId");
        @SuppressWarnings("unchecked")
        List<Integer> productIdList = (List<Integer>) params.get("productIds");
        Integer[] productIds = productIdList != null ? productIdList.toArray(new Integer[0]) : new Integer[0];
        return equipmentService.setProducts(equipmentId, productIds, getFactoryId());
    }
}
