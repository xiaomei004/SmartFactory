package com.xiaomei.mapper;

import com.xiaomei.entity.Factory;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FactoryMapper {

    @Select("SELECT * FROM t_factory WHERE id = #{id} AND flag = 0")
    Factory selectById(@Param("id") Integer id);

    @Select("SELECT * FROM t_factory WHERE flag = 0")
    Factory selectAny();

    @Insert("INSERT INTO t_factory (flag, create_time, create_userid, update_time, update_userid, " +
            "bak, factory_name, factory_img_url, factory_addr, factory_url, factory_worker, factory_status) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{bak}, #{factoryName}, #{factoryImgUrl}, #{factoryAddr}, #{factoryUrl}, " +
            "#{factoryWorker}, #{factoryStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Factory factory);

    @Update("UPDATE t_factory SET factory_name = #{factoryName}, factory_addr = #{factoryAddr}, " +
            "factory_url = #{factoryUrl}, factory_worker = #{factoryWorker}, " +
            "update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id}")
    int update(Factory factory);
}
