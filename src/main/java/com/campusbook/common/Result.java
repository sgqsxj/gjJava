package com.campusbook.common;

import java.io.Serializable;

/**
 * 泛型统一响应体
 * 所有 Controller 返回值统一使用 Result<T> 包装
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 响应码：200成功，其他为业务错误 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 业务数据（泛型抽象） */
    private T data;

    /** 时间戳 */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    /** 成功（无数据） */
    public static <T> Result<T> success() {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("操作成功");
        return r;
    }

    /** 成功（带数据） */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }

    /** 成功（带数据和提示） */
    public static <T> Result<T> success(T data, String message) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    /** 失败（自定义码和消息） */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /** 失败（业务异常统一用 code=500） */
    public static <T> Result<T> fail(String message) {
        return fail(500, message);
    }

    /** 失败（未登录 401） */
    public static <T> Result<T> unauthorized(String message) {
        return fail(401, message);
    }
}
