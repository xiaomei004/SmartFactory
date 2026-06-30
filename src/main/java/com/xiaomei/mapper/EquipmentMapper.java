package com.xiaomei.mapper;

import com.xiaomei.entity.Equipment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EquipmentMapper {

    @Select("SELECT * FROM t_equipment WHERE flag = 0 ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<Equipment> selectAll(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_equipment WHERE flag = 0")
    Long selectCount();

    @Select("SELECT * FROM t_equipment WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<Equipment> selectList(@Param("factoryId") Integer factoryId,
                               @Param("offset") Integer offset,
                               @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_equipment WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCountByFactory(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_equipment WHERE id = #{id} AND flag = 0")
    Equipment selectById(@Param("id") Integer id);

    @Select("SELECT * FROM t_equipment WHERE flag = 0 AND equipment_status = #{status}")
    List<Equipment> selectByStatus(@Param("status") Integer status);

    @Insert("INSERT INTO t_equipment (flag, create_time, create_userid, update_time, update_userid, " +
            "equipment_seq, equipment_name, equipment_img_url, equipment_status, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{equipmentSeq}, #{equipmentName}, #{equipmentImgUrl}, #{equipmentStatus}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Equipment equipment);

    @Update("UPDATE t_equipment SET equipment_name = #{equipmentName}, equipment_seq = #{equipmentSeq}, " +
            "equipment_img_url = #{equipmentImgUrl}, equipment_status = #{equipmentStatus}, " +
            "update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id} AND flag = 0")
    int update(Equipment equipment);

    @Update("UPDATE t_equipment SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
