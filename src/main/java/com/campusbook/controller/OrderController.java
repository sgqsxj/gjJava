package com.campusbook.controller;

import com.campusbook.common.PageResult;
import com.campusbook.common.Result;
import com.campusbook.dto.CreateOrderDTO;
import com.campusbook.dto.OrderQuery;
import com.campusbook.entity.Order;
import com.campusbook.entity.User;
import com.campusbook.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 订单 API 控制器
 * 全部接口需登录（WebMvcConfig 已拦截 /api/order/**）
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单（买家）
     * POST /api/order/create  body: {"bookId":1,"quantity":1,"address":"..."}
     */
    @PostMapping("/create")
    public Result<Order> create(@Valid @RequestBody CreateOrderDTO dto, HttpSession session) {
        User currentUser = currentUser(session);
        return Result.success(orderService.create(currentUser.getId(), dto));
    }

    /**
     * 订单列表
     * GET /api/order/list?role=buy&status=PENDING_PAYMENT&pageNum=1&pageSize=10
     * role: buy-我买到的（默认） / sell-我卖出的
     */
    @GetMapping("/list")
    public Result<PageResult<Order>> list(@RequestParam(value = "role", defaultValue = "buy") String role,
                                          OrderQuery query,
                                          HttpSession session) {
        User currentUser = currentUser(session);
        return Result.success(orderService.getPage(currentUser.getId(), role, query));
    }

    /**
     * 订单详情（仅买卖双方）
     */
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id, HttpSession session) {
        User currentUser = currentUser(session);
        return Result.success(orderService.getDetail(id, currentUser.getId()));
    }

    /**
     * 支付（买家，待支付 -> 待发货）
     */
    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, HttpSession session) {
        orderService.pay(id, currentUser(session).getId());
        return Result.success();
    }

    /**
     * 发货（卖家，待发货 -> 待收货）
     */
    @PostMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id, HttpSession session) {
        orderService.ship(id, currentUser(session).getId());
        return Result.success();
    }

    /**
     * 确认收货（买家，待收货 -> 已完成）
     */
    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id, HttpSession session) {
        orderService.confirm(id, currentUser(session).getId());
        return Result.success();
    }

    /**
     * 取消订单（买家，仅待支付，回补库存）
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpSession session) {
        orderService.cancel(id, currentUser(session).getId());
        return Result.success();
    }

    /**
     * 从 Session 取当前登录用户（拦截器已保证存在）
     */
    private User currentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}
