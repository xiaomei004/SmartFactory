package com.xiaomei.mapper;

import com.xiaomei.entity.ProductPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductPlanMapper {

    @Select("SELECT * FROM t_product_plan WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<ProductPlan> selectList(@Param("factoryId") Integer factoryId,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_product_plan WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCount(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_product_plan WHERE id = #{id} AND flag = 0")
    ProductPlan selectById(@Param("id") Integer id);

    @Select("SELECT * FROM t_product_plan WHERE order_id = #{orderId} AND flag = 0")
    List<ProductPlan> selectByOrderId(@Param("orderId") Integer orderId);

    @Insert("INSERT INTO t_product_plan (flag, create_time, create_userid, update_time, update_userid, " +
            "plan_seq, order_id, product_id, plan_count, delivery_date, plan_start_date, plan_end_date, " +
            "plan_status, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{planSeq}, #{orderId}, #{productId}, #{planCount}, #{deliveryDate}, " +
            "#{planStartDate}, #{planEndDate}, #{planStatus}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPlan plan);

    @Update("UPDATE t_product_plan SET plan_status = #{planStatus}, plan_count = #{planCount}, " +
            "delivery_date = #{deliveryDate}, plan_start_date = #{planStartDate}, plan_end_date = #{planEndDate}, " +
            "update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id} AND flag = 0")
    int update(ProductPlan plan);

    @Update("UPDATE t_product_plan SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
