package com.campusbook.common;

/**
 * 业务异常
 * 用于抛出业务逻辑错误，由 GlobalExceptionHandler 统一处理
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码，默认 500 */
    private Integer code;

    public BizException(String message) {
        super(message);
        this.code = 500;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }
}
