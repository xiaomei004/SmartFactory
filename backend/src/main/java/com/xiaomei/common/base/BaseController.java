package com.xiaomei.common.base;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    protected Integer getCurrentUserId() {
        Object userId = request.getAttribute("currentUserId");
        return userId != null ? (Integer) userId : 1; // 默认用户ID=1
    }

    protected Integer getFactoryId() {
        Object factoryId = request.getAttribute("factoryId");
        return factoryId != null ? (Integer) factoryId : 1; // 默认工厂ID=1
    }

    protected Map<String, String> success(String msg) {
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        result.put("msg", msg == null ? "操作成功" : msg);
        return result;
    }

    protected Map<String, String> error(String msg) {
        Map<String, String> result = new HashMap<>();
        result.put("status", "error");
        result.put("msg", msg == null ? "操作失败" : msg);
        return result;
    }
}
