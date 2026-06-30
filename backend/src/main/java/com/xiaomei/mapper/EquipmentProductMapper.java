package com.xiaomei.mapper;

import com.xiaomei.entity.EquipmentProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EquipmentProductMapper {

    @Select("SELECT * FROM t_equipment_product WHERE equipment_id = #{equipmentId}")
    List<EquipmentProduct> selectByEquipmentId(@Param("equipmentId") Integer equipmentId);

    @Select("SELECT * FROM t_equipment_product WHERE product_id = #{productId}")
    List<EquipmentProduct> selectByProductId(@Param("productId") Integer productId);

    @Insert("INSERT INTO t_equipment_product (equipment_id, product_id, yield, unit, factory_id) " +
            "VALUES (#{equipmentId}, #{productId}, #{yield}, #{unit}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EquipmentProduct ep);

    @Update("UPDATE t_equipment_product SET yield = #{yield}, unit = #{unit} WHERE id = #{id}")
    int update(EquipmentProduct ep);

    @Delete("DELETE FROM t_equipment_product WHERE id = #{id}")
    int delete(@Param("id") Integer id);

    @Delete("DELETE FROM t_equipment_product WHERE equipment_id = #{equipmentId}")
    int deleteByEquipmentId(@Param("equipmentId") Integer equipmentId);
}
