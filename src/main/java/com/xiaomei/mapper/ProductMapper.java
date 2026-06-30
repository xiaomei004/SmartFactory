package com.xiaomei.mapper;

import com.xiaomei.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM t_product WHERE flag = 0 AND factory_id = #{factoryId} " +
            "ORDER BY id DESC LIMIT #{offset}, #{pageSize}")
    List<Product> selectList(@Param("factoryId") Integer factoryId,
                             @Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM t_product WHERE flag = 0 AND factory_id = #{factoryId}")
    Long selectCount(@Param("factoryId") Integer factoryId);

    @Select("SELECT * FROM t_product WHERE id = #{id} AND flag = 0")
    Product selectById(@Param("id") Integer id);

    @Insert("INSERT INTO t_product (flag, create_time, create_userid, update_time, update_userid, " +
            "product_num, product_name, product_img_url, factory_id) " +
            "VALUES (0, NOW(), #{createUserid}, NOW(), #{updateUserid}, " +
            "#{productNum}, #{productName}, #{productImgUrl}, #{factoryId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE t_product SET product_name = #{productName}, product_num = #{productNum}, " +
            "product_img_url = #{productImgUrl}, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id} AND flag = 0")
    int update(Product product);

    @Update("UPDATE t_product SET flag = 1, update_time = NOW(), update_userid = #{updateUserid} " +
            "WHERE id = #{id}")
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);

    @Select("SELECT * FROM t_product WHERE product_name = #{productName} AND factory_id = #{factoryId} AND flag = 0")
    Product selectByNameAndFactory(@Param("productName") String productName, @Param("factoryId") Integer factoryId);

    @Select("SELECT COUNT(*) FROM t_product_order WHERE product_id = #{productId} AND flag = 0 " +
            "AND order_status IN (10,20,40)")
    int countOrdersByProductId(@Param("productId") Integer productId);
}
