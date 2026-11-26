package com.campusbook.service.impl;

import com.campusbook.common.BizException;
import com.campusbook.common.OrderStatus;
import com.campusbook.common.PageResult;
import com.campusbook.dto.CreateOrderDTO;
import com.campusbook.dto.OrderQuery;
import com.campusbook.entity.Book;
import com.campusbook.entity.Order;
import com.campusbook.entity.User;
import com.campusbook.repository.BookMapper;
import com.campusbook.repository.OrderMapper;
import com.campusbook.repository.UserMapper;
import com.campusbook.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单业务实现
 * 核心考核点：
 * 1. 订单状态机（OrderStatus 枚举 + canTransitionTo 校验）
 * 2. 乐观锁防超卖（book.stock >= quantity 的 CAS 扣减）
 * 3. 订单状态 CAS 更新（WHERE status = 期望状态），防止并发重复流转
 * 4. 订单商品快照（物品名称 / 单价入订单，后续商品改价不影响历史订单）
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter ORDER_NO_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(Long buyerId, CreateOrderDTO dto) {
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) {
            throw new BizException("商品不存在");
        }
        if (book.getStatus() == null || book.getStatus() != 1) {
            throw new BizException("商品已下架，无法购买");
        }
        if (buyerId.equals(book.getSellerId())) {
            throw new BizException("不能购买自己发布的商品");
        }

        int quantity = (dto.getQuantity() == null || dto.getQuantity() < 1) ? 1 : dto.getQuantity();

        // 乐观锁扣减库存：stock >= quantity 作为 CAS 条件，并发下单只有一人成功
        int rows = bookMapper.decreaseStock(book.getId(), quantity);
        if (rows == 0) {
            throw new BizException("库存不足，下单失败");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(book.getSellerId());
        order.setBookId(book.getId());
        // 商品快照：下单时的物品名称与单价
        order.setBookTitle(book.getTitle());
        order.setBookPrice(book.getPrice());
        order.setQuantity(quantity);
        order.setTotalAmount(book.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setAddress(resolveAddress(buyerId, dto.getAddress()));
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());

        orderMapper.insert(order);
        order.setStatusLabel(OrderStatus.PENDING_PAYMENT.getLabel());
        return order;
    }

    @Override
    public PageResult<Order> getPage(Long userId, String role, OrderQuery query) {
        // 强制绑定当前登录用户，避免前端传参越权查询他人订单
        if ("sell".equalsIgnoreCase(role)) {
            query.setSellerId(userId);
            query.setBuyerId(null);
        } else {
            query.setBuyerId(userId);
            query.setSellerId(null);
        }

        PageHelper.startPage(query.getSafePageNum(), query.getSafePageSize());
        List<Order> list = orderMapper.selectWithCondition(query);
        PageInfo<Order> info = new PageInfo<>(list);
        fillStatusLabel(list);

        return new PageResult<>(list, info.getTotal(), info.getPageNum(), info.getPageSize());
    }

    @Override
    public Order getDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectByIdWithUsers(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            throw new BizException("无权查看该订单");
        }
        order.setStatusLabel(OrderStatus.of(order.getStatus()).getLabel());
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pay(Long orderId, Long userId) {
        Order order = requireOrder(orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new BizException("只有买家才能支付该订单");
        }
        transit(order, OrderStatus.PAID, LocalDateTime.now(), null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(Long orderId, Long userId) {
        Order order = requireOrder(orderId);
        if (!userId.equals(order.getSellerId())) {
            throw new BizException("只有卖家才能发货");
        }
        transit(order, OrderStatus.SHIPPED, null, LocalDateTime.now(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(Long orderId, Long userId) {
        Order order = requireOrder(orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new BizException("只有买家才能确认收货");
        }
        transit(order, OrderStatus.COMPLETED, null, null, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long orderId, Long userId) {
        Order order = requireOrder(orderId);
        if (!userId.equals(order.getBuyerId())) {
            throw new BizException("只有买家才能取消订单");
        }
        transit(order, OrderStatus.CANCELLED, null, null, null);
        // 取消成功后回补库存
        bookMapper.increaseStock(order.getBookId(), order.getQuantity());
    }

    // ==================== 私有方法 ====================

    /**
     * 状态流转统一入口：状态机校验 + CAS 更新
     */
    private void transit(Order order, OrderStatus target,
                         LocalDateTime payTime, LocalDateTime shipTime, LocalDateTime finishTime) {
        OrderStatus current = OrderStatus.of(order.getStatus());
        if (!current.canTransitionTo(target)) {
            throw new BizException("当前状态【" + current.getLabel()
                    + "】不允许变更为【" + target.getLabel() + "】");
        }
        // CAS：只有数据库状态仍等于当前状态才更新，避免并发重复流转
        int rows = orderMapper.updateStatusIfMatch(
                order.getId(), current.name(), target.name(), payTime, shipTime, finishTime);
        if (rows == 0) {
            throw new BizException("订单状态已被变更，请刷新后重试");
        }
    }

    private Order requireOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        return order;
    }

    /**
     * 收货地址：优先用下单时填写的，否则取用户默认地址
     */
    private String resolveAddress(Long buyerId, String addressFromForm) {
        if (addressFromForm != null && !addressFromForm.trim().isEmpty()) {
            return addressFromForm.trim();
        }
        User buyer = userMapper.selectById(buyerId);
        return buyer == null ? null : buyer.getAddress();
    }

    /**
     * 订单号：yyyyMMddHHmmss + 4 位随机数
     */
    private String generateOrderNo() {
        return LocalDateTime.now().format(ORDER_NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private void fillStatusLabel(List<Order> list) {
        for (Order o : list) {
            o.setStatusLabel(OrderStatus.of(o.getStatus()).getLabel());
        }
    }
}
