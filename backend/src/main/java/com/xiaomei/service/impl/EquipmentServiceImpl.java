package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.common.constant.StatusEnum;
import com.xiaomei.entity.Equipment;
import com.xiaomei.entity.EquipmentProduct;
import com.xiaomei.mapper.EquipmentMapper;
import com.xiaomei.mapper.EquipmentProductMapper;
import com.xiaomei.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;
    @Autowired
    private EquipmentProductMapper equipmentProductMapper;

    @Override
    public PageObject<Equipment> queryList(BaseQuery query) {
        Integer factoryId = query.getFactoryId();
        if (factoryId == null) return PageObject.error("工厂ID不能为空");
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<Equipment> list = equipmentMapper.selectList(factoryId, offset, query.getPageSize());
        Long total = equipmentMapper.selectCountByFactory(factoryId);
        return new PageObject<>(list, query.getPageNum(), query.getPageSize(), total);
    }

    @Override
    public Equipment queryById(Integer id) { return equipmentMapper.selectById(id); }

    @Override
    public List<EquipmentProduct> getProducts(Integer equipmentId) {
        return equipmentProductMapper.selectByEquipmentId(equipmentId);
    }

    @Override
    public Map<String, String> add(Equipment equipment) {
        if (equipment.getEquipmentName() == null || equipment.getEquipmentName().trim().isEmpty()) {
            return error("设备名称不能为空");
        }
        if (equipment.getEquipmentSeq() == null || equipment.getEquipmentSeq().trim().isEmpty()) {
            return error("设备序号不能为空");
        }
        if (equipment.getFactoryId() == null) return error("工厂ID不能为空");
        // 序列号唯一
        Equipment exist = equipmentMapper.selectBySeqAndFactory(equipment.getEquipmentSeq(), equipment.getFactoryId());
        if (exist != null) return error("设备序号已存在");
        if (equipment.getEquipmentStatus() == null) equipment.setEquipmentStatus(StatusEnum.EQUIPMENT_ENABLED);
        equipmentMapper.insert(equipment);
        return success("添加成功");
    }

    @Override
    public Map<String, String> edit(Equipment equipment) {
        if (equipment.getId() == null) return error("设备ID不能为空");
        Equipment existing = equipmentMapper.selectById(equipment.getId());
        if (existing == null) return error("设备不存在");
        if (equipment.getEquipmentName() != null && !equipment.getEquipmentName().trim().isEmpty()) {
            existing.setEquipmentName(equipment.getEquipmentName());
        }
        if (equipment.getEquipmentSeq() != null && !equipment.getEquipmentSeq().trim().isEmpty()) {
            Equipment dup = equipmentMapper.selectBySeqAndFactory(equipment.getEquipmentSeq(), existing.getFactoryId());
            if (dup != null && !dup.getId().equals(equipment.getId())) return error("设备序号已存在");
            existing.setEquipmentSeq(equipment.getEquipmentSeq());
        }
        if (equipment.getEquipmentImgUrl() != null) existing.setEquipmentImgUrl(equipment.getEquipmentImgUrl());
        if (equipment.getEquipmentStatus() != null) existing.setEquipmentStatus(equipment.getEquipmentStatus());
        existing.setUpdateUserid(equipment.getUpdateUserid());
        equipmentMapper.update(existing);
        return success("修改成功");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        // 有关联活跃工单不可删除
        int activeSchedules = equipmentMapper.countActiveSchedulesByEquipment(id);
        if (activeSchedules > 0) return error("该设备存在" + activeSchedules + "个活跃工单，不可删除");
        equipmentMapper.delete(id, updateUserid);
        equipmentProductMapper.deleteByEquipmentId(id);
        return success("删除成功");
    }

    @Override
    public Map<String, String> setProducts(Integer equipmentId, Integer[] productIds, Integer factoryId) {
        equipmentProductMapper.deleteByEquipmentId(equipmentId);
        if (productIds != null) {
            for (Integer productId : productIds) {
                EquipmentProduct ep = new EquipmentProduct();
                ep.setEquipmentId(equipmentId);
                ep.setProductId(productId);
                ep.setFactoryId(factoryId);
                equipmentProductMapper.insert(ep);
            }
        }
        return success("关联成功");
    }

    private Map<String, String> success(String msg) { return Map.of("status", "ok", "msg", msg); }
    private Map<String, String> error(String msg) { return Map.of("status", "error", "msg", msg); }
}
