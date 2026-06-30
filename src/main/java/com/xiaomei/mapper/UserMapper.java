package com.xiaomei.mapper;

import com.xiaomei.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM t_user WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    java.util.List<User> selectList(@Param("factoryId") Integer factoryId,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_user WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCount(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_user WHERE id = #{id} AND flag = 0")
    User selectById(@Param("id") Integer id);

    @Select("SELECT * FROM t_user WHERE user_name = #{userName} AND flag = 0")
    User selectByUserName(@Param("userName") String userName);

    @Select("SELECT * FROM t_user WHERE user_name = #{userName} AND user_passwd = #{userPasswd} AND flag = 0")
    User login(@Param("userName") String userName, @Param("userPasswd") String userPasswd);

    @Insert("INSERT INTO t_user (flag, create_time, create_userid, update_time, update_userid, " +
            "user_status, user_name, user_real_name, user_passwd, user_job_num, " +
            "user_phone_num, user_email, role_id, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "0, #{userName}, #{userRealName}, #{userPasswd}, #{userJobNum}, " +
            "#{userPhoneNum}, #{userEmail}, #{roleId}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE t_user SET update_time = NOW(), update_userid = #{updateUserid}")
    int update(User user);

    @Update("UPDATE t_user SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
