package com.campusbook.service;

import com.campusbook.common.PageResult;
import com.campusbook.dto.CreateOrderDTO;
import com.campusbook.dto.OrderQuery;
import com.campusbook.entity.Order;

/**
 * 订单业务接口
 * 状态机：待支付 -> 待发货 -> 待收货 -> 已完成，待支付可取消
 */
public interface OrderService {

    /**
     * 创建订单（扣减库存，生成订单快照）
     *
     * @param buyerId 买家 ID（从 Session 取）
     */
    Order create(Long buyerId, CreateOrderDTO dto);

    /**
     * 分页查询订单
     *
     * @param userId 当前登录用户 ID
     * @param role   buy-我买到的 / sell-我卖出的
     */
    PageResult<Order> getPage(Long userId, String role, OrderQuery query);

    /**
     * 订单详情（仅买卖双方可见）
     */
    Order getDetail(Long orderId, Long userId);

    /**
     * 支付（买家，待支付 -> 待发货）
     */
    void pay(Long orderId, Long userId);

    /**
     * 发货（卖家，待发货 -> 待收货）
     */
    void ship(Long orderId, Long userId);

    /**
     * 确认收货（买家，待收货 -> 已完成）
     */
    void confirm(Long orderId, Long userId);

    /**
     * 取消订单（买家，仅待支付，回补库存）
     */
    void cancel(Long orderId, Long userId);
}
