package com.xiaomei.service;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.DailyWork;

import java.util.Map;

public interface DailyWorkService {
    PageObject<DailyWork> queryList(BaseQuery query);
    DailyWork queryById(Integer id);
    Map<String, String> report(DailyWork work);
    Map<String, String> completeReport(Integer id, Integer updateUserid);
    Map<String, String> delete(Integer id, Integer updateUserid);
}
