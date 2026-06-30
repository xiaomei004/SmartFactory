package com.xiaomei.mapper;

import com.xiaomei.entity.DailyWork;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DailyWorkMapper {

    @Select("SELECT * FROM t_daily_work WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<DailyWork> selectList(@Param("factoryId") Integer factoryId,
                               @Param("offset") Integer offset,
                               @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_daily_work WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCount(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_daily_work WHERE id = #{id} AND flag = 0")
    DailyWork selectById(@Param("id") Integer id);

    @Select("SELECT * FROM t_daily_work WHERE schedule_id = #{scheduleId} AND flag = 0")
    List<DailyWork> selectByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Insert("INSERT INTO t_daily_work (flag, create_time, create_userid, update_time, update_userid, " +
            "schedule_id, equipment_id, equipment_seq, start_time, end_time, " +
            "working_count, qualified_count, unqualified_cout, complete_flag, factory_id, bak) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{scheduleId}, #{equipmentId}, #{equipmentSeq}, #{startTime}, #{endTime}, " +
            "#{workingCount}, #{qualifiedCount}, #{unqualifiedCout}, #{completeFlag}, #{factoryId}, #{bak})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DailyWork work);

    @Update("UPDATE t_daily_work SET working_count = #{workingCount}, qualified_count = #{qualifiedCount}, " +
            "unqualified_cout = #{unqualifiedCout}, complete_flag = #{completeFlag}, end_time = #{endTime}, " +
            "bak = #{bak}, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id} AND flag = 0")
    int update(DailyWork work);

    @Update("UPDATE t_daily_work SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
