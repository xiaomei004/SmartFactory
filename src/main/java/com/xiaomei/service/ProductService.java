package com.xiaomei.service;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.Product;

import java.util.Map;

public interface ProductService {
    PageObject<Product> queryList(BaseQuery query);
    Product queryById(Integer id);
    Map<String, String> add(Product product);
    Map<String, String> edit(Product product);
    Map<String, String> delete(Integer id, Integer updateUserid);
}
