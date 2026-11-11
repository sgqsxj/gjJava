package com.campusbook.repository;

import com.campusbook.dto.BookQuery;
import com.campusbook.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 Mapper 接口
 */
@Mapper
public interface BookMapper {

    /**
     * 多条件动态查询（配合 PageHelper 使用）
     * keyword 模糊匹配 title 或 author，categoryId/status/sellerId 精确匹配
     */
    List<Book> selectWithCondition(BookQuery query);

    /**
     * 按 ID 查询（不含关联信息）
     */
    Book selectById(@Param("id") Long id);

    /**
     * 按 ID 查询（LEFT JOIN user + category，回填 sellerName / categoryName）
     */
    Book selectByIdWithSeller(@Param("id") Long id);

    /**
     * 浏览次数 +1
     */
    int updateViewCount(@Param("id") Long id);

    /**
     * 新增商品（useGeneratedKeys 回填自增主键到 book.id）
     */
    int insert(Book book);

    /**
     * 更新商品（<if> 动态 SQL，只更新非 null 字段）
     */
    int updateById(Book book);

    /**
     * 按 ID 上架 / 下架
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
