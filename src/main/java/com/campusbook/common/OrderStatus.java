package com.campusbook.common;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 订单状态枚举 + 状态机
 * 合法流转：待支付 -> 已支付(待发货) -> 已发货(待收货) -> 已完成
 * 待支付可取消；已完成 / 已取消 为终态
 */
public enum OrderStatus {

    PENDING_PAYMENT("待支付"),
    PAID("待发货"),
    SHIPPED("待收货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 状态机转移表：key 当前状态，value 允许流转到的目标状态集合 */
    private static final Map<OrderStatus, Set<OrderStatus>> TRANSITIONS;

    static {
        Map<OrderStatus, Set<OrderStatus>> map = new EnumMap<>(OrderStatus.class);
        map.put(PENDING_PAYMENT, EnumSet.of(PAID, CANCELLED));
        map.put(PAID, EnumSet.of(SHIPPED));
        map.put(SHIPPED, EnumSet.of(COMPLETED));
        map.put(COMPLETED, Collections.emptySet());
        map.put(CANCELLED, Collections.emptySet());
        TRANSITIONS = Collections.unmodifiableMap(map);
    }

    /**
     * 判断当前状态能否流转到目标状态
     */
    public boolean canTransitionTo(OrderStatus target) {
        Set<OrderStatus> allowed = TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }

    /**
     * 是否终态（已完成 / 已取消）
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * 按枚举名解析，非法值抛业务异常
     */
    public static OrderStatus of(String name) {
        for (OrderStatus s : values()) {
            if (s.name().equals(name)) {
                return s;
            }
        }
        throw new BizException("非法的订单状态：" + name);
    }
}
