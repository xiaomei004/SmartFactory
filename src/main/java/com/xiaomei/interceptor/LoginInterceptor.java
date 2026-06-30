package com.xiaomei.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 * 从请求头获取用户ID和工厂ID，注入到 request attribute
 * 如无则使用默认值（开发阶段简化处理）
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取用户和工厂信息
        String userId = request.getHeader("X-User-Id");
        String factoryId = request.getHeader("X-Factory-Id");

        request.setAttribute("currentUserId", userId != null ? Integer.parseInt(userId) : 1);
        request.setAttribute("factoryId", factoryId != null ? Integer.parseInt(factoryId) : 1);

        return true;
    }
}
