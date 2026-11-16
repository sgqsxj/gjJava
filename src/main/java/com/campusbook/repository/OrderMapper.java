package com.campusbook.repository;

import com.campusbook.dto.OrderQuery;
import com.campusbook.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface OrderMapper {

    /**
     * 新增订单（useGeneratedKeys 回填自增主键）
     */
    int insert(Order order);

    /**
     * 按 ID 查询（不含关联）
     */
    Order selectById(@Param("id") Long id);

    /**
     * 按 ID 查询（LEFT JOIN 买家 / 卖家，回填 buyerName / sellerName）
     */
    Order selectByIdWithUsers(@Param("id") Long id);

    /**
     * 多条件动态分页查询（buyerId / sellerId / status）
     */
    List<Order> selectWithCondition(OrderQuery query);

    /**
     * 状态流转（CAS 乐观并发控制）
     * 只有数据库当前状态等于 expectStatus 时才更新，返回 0 表示状态已被并发修改
     */
    int updateStatusIfMatch(@Param("id") Long id,
                            @Param("expectStatus") String expectStatus,
                            @Param("targetStatus") String targetStatus,
                            @Param("payTime") LocalDateTime payTime,
                            @Param("shipTime") LocalDateTime shipTime,
                            @Param("finishTime") LocalDateTime finishTime);
}
