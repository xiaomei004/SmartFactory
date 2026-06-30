package com.xiaomei.service;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.ProductOrder;

import java.util.Map;

public interface OrderService {
    PageObject<ProductOrder> queryList(BaseQuery query);
    ProductOrder queryById(Integer id);
    Map<String, String> add(ProductOrder order);
    Map<String, String> accept(Integer id, Integer updateUserid);
    Map<String, String> reject(Integer id, Integer updateUserid);
    Map<String, String> complete(Integer id, Integer updateUserid);
    Map<String, String> delete(Integer id, Integer updateUserid);
}
