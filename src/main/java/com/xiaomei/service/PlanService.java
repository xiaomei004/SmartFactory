package com.xiaomei.service;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.ProductPlan;

import java.util.Map;

public interface PlanService {
    PageObject<ProductPlan> queryList(BaseQuery query);
    ProductPlan queryById(Integer id);
    Map<String, String> add(ProductPlan plan);
    Map<String, String> start(Integer id, Integer updateUserid);
    Map<String, String> complete(Integer id, Integer updateUserid);
    Map<String, String> delete(Integer id, Integer updateUserid);
}
