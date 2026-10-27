package com.campusbook.config;

import com.campusbook.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 * 未登录时返回 401 JSON（API 请求）或重定向登录页（页面请求）
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final String SESSION_USER_KEY = "currentUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute(SESSION_USER_KEY);

        if (currentUser != null) {
            // 已登录，放行
            return true;
        }

        // 未登录：判断是 API 请求还是页面请求
        String requestURI = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");

        boolean isApi = requestURI.startsWith("/api/");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(xRequestedWith);
        boolean wantsJson = accept != null && accept.contains("application/json");

        if (isApi || isAjax || wantsJson) {
            // API 请求：返回 401 JSON
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
        } else {
            // 页面请求：重定向到登录页
            response.sendRedirect("/login?redirect=" + requestURI);
        }
        return false;
    }
}
