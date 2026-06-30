package com.xiaomei.mapper;

import com.xiaomei.entity.OrderTrack;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderTrackMapper {

    @Insert("INSERT INTO t_order_track (flag, create_time, create_userid, update_time, update_userid, " +
            "schedule_id, plan_id, product_id, working_count, qualified_count, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{scheduleId}, #{planId}, #{productId}, #{workingCount}, #{qualifiedCount}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderTrack track);

    @Select("SELECT * FROM t_order_track WHERE schedule_id = #{scheduleId} AND flag = 0")
    List<OrderTrack> selectByScheduleId(@Param("scheduleId") Integer scheduleId);
}
