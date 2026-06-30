package com.xiaomei.mapper;

import com.xiaomei.entity.ProductSchedule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductScheduleMapper {

    @Select("SELECT * FROM t_product_schedule WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<ProductSchedule> selectList(@Param("factoryId") Integer factoryId,
                                     @Param("offset") Integer offset,
                                     @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_product_schedule WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCount(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_product_schedule WHERE id = #{id} AND flag = 0")
    ProductSchedule selectById(@Param("id") Integer id);

    @Select("SELECT * FROM t_product_schedule WHERE plan_id = #{planId} AND flag = 0")
    List<ProductSchedule> selectByPlanId(@Param("planId") Integer planId);

    @Insert("INSERT INTO t_product_schedule (flag, create_time, create_userid, update_time, update_userid, " +
            "schedule_seq, schedule_count, schedule_status, plan_id, product_id, equipment_id, " +
            "start_date, end_date, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{scheduleSeq}, #{scheduleCount}, #{scheduleStatus}, #{planId}, #{productId}, " +
            "#{equipmentId}, #{startDate}, #{endDate}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSchedule schedule);

    @Update("UPDATE t_product_schedule SET schedule_status = #{scheduleStatus}, " +
            "schedule_count = #{scheduleCount}, equipment_id = #{equipmentId}, " +
            "start_date = #{startDate}, end_date = #{endDate}, " +
            "update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id} AND flag = 0")
    int update(ProductSchedule schedule);

    @Update("UPDATE t_product_schedule SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
