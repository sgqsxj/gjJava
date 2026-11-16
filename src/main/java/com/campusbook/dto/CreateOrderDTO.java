package com.campusbook.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建订单请求 DTO
 */
public class CreateOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品ID不能为空")
    private Long bookId;

    @Min(value = 1, message = "购买数量至少为 1")
    private Integer quantity = 1;

    /** 收货地址（为空时使用用户默认地址） */
    private String address;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
