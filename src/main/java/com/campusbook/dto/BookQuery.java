package com.campusbook.dto;

import com.campusbook.common.PageQuery;

/**
 * 商品列表查询 DTO
 * 继承 PageQuery，自动携带 pageNum / pageSize
 */
public class BookQuery extends PageQuery {

    /** 关键字（模糊匹配 title 或 author） */
    private String keyword;

    /** 分类 ID */
    private Long categoryId;

    /** 商品状态：1-上架 0-下架，默认只查上架 */
    private Integer status = 1;

    /** 卖家 ID（个人中心查自己发布的商品用） */
    private Long sellerId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
}
