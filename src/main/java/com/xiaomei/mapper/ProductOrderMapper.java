package com.xiaomei.mapper;

import com.xiaomei.entity.ProductOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductOrderMapper {

    @Select("SELECT * FROM t_product_order WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<ProductOrder> selectList(@Param("factoryId") Integer factoryId,
                                  @Param("offset") Integer offset,
                                  @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_product_order WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCount(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_product_order WHERE id = #{id} AND flag = 0")
    ProductOrder selectById(@Param("id") Integer id);

    @Insert("INSERT INTO t_product_order (flag, create_time, create_userid, update_time, update_userid, " +
            "order_seq, order_source, product_id, product_count, end_date, order_status, factory_id, factory_yield) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{orderSeq}, #{orderSource}, #{productId}, #{productCount}, #{endDate}, " +
            "#{orderStatus}, #{factoryId}, #{factoryYield})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductOrder order);

    @Update("UPDATE t_product_order SET order_status = #{orderStatus}, factory_yield = #{factoryYield}, " +
            "update_time = NOW(), update_userid = #{updateUserid} WHERE id = #{id} AND flag = 0")
    int updateStatus(ProductOrder order);

    @Update("UPDATE t_product_order SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
