package com.campusbook.service;

import com.campusbook.common.PageResult;
import com.campusbook.dto.BookQuery;
import com.campusbook.dto.PublishBookDTO;
import com.campusbook.entity.Book;

/**
 * 商品业务接口
 */
public interface BookService {

    /**
     * 发布商品
     * @param sellerId 卖家用户 ID（从 Session 取，不由前端传递）
     * @param dto     发布表单数据
     * @return 新建的 Book（含自增 ID）
     */
    Book publish(Long sellerId, PublishBookDTO dto);

    /**
     * 分页查询商品列表（配合 PageHelper）
     */
    PageResult<Book> getPage(BookQuery query);

    /**
     * 商品详情（浏览次数 +1 后返回）
     */
    Book getDetail(Long id);

    /**
     * 卖家视角：查自己发布的商品列表
     */
    PageResult<Book> getBySeller(Long sellerId, BookQuery query);

    /**
     * 上架 / 下架
     */
    void updateStatus(Long id, Integer status);

    /**
     * 按 ID 查询（不含关联，用于内部校验）
     */
    Book getById(Long id);
}
