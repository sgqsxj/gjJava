package com.campusbook.common;

/**
 * 未登录异常
 * 登录拦截器抛出此异常，由 GlobalExceptionHandler 捕获返回 401
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super("请先登录");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
