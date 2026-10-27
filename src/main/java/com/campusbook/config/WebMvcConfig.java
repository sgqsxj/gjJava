package com.campusbook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册登录拦截器，配置拦截路径和排除路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                        // 需要登录才能访问的 API
                        "/api/order/**",
                        "/api/favorite/**",
                        "/api/user/me/**",
                        "/api/user/update-password",
                        // 需要登录才能访问的页面（后续步骤会用到）
                        "/center/**",
                        "/order/**",
                        "/favorite/**",
                        "/book/publish"
                )
                .excludePathPatterns(
                        // 公开 API
                        "/api/user/register",
                        "/api/user/login",
                        "/api/user/logout",
                        "/api/user/current",
                        "/api/book/list",
                        "/api/book/detail",
                        "/api/book/search",
                        "/api/category/**",
                        // 静态资源
                        "/",
                        "/login",
                        "/register",
                        "/book",
                        "/book/detail",
                        "/error",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**"
                );
    }
}
