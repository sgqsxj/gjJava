package com.campusbook.dto;

import com.campusbook.common.PageQuery;

/**
 * 订单列表查询 DTO
 * buyerId / sellerId 由 Service 从当前登录用户注入，避免前端越权查询他人订单
 */
public class OrderQuery extends PageQuery {

    /** 订单状态过滤（OrderStatus 枚举名） */
    private String status;

    private Long buyerId;

    private Long sellerId;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}
