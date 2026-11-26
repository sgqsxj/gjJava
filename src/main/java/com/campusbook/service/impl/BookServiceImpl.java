package com.campusbook.service.impl;

import com.campusbook.common.BizException;
import com.campusbook.common.PageResult;
import com.campusbook.dto.BookQuery;
import com.campusbook.dto.PublishBookDTO;
import com.campusbook.entity.Book;
import com.campusbook.repository.BookMapper;
import com.campusbook.service.BookService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品业务实现
 */
@Service
public class BookServiceImpl implements BookService {

    @Resource
    private BookMapper bookMapper;

    @Override
    public Book publish(Long sellerId, PublishBookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPublisher(dto.getPublisher());
        book.setIsbn(dto.getIsbn());
        book.setCategoryId(dto.getCategoryId());
        book.setOriginalPrice(dto.getOriginalPrice());
        book.setPrice(dto.getPrice());
        book.setCondition(dto.getCondition());
        book.setDescription(dto.getDescription());
        book.setCoverImage(dto.getCoverImage());
        book.setStock(1);        // 二手物品默认库存 1（乐观锁预留）
        book.setStatus(1);        // 默认上架
        book.setViewCount(0);
        book.setSellerId(sellerId);

        bookMapper.insert(book);
        return book;
    }

    @Override
    public PageResult<Book> getPage(BookQuery query) {
        // PageHelper.startPage 必须紧跟 mapper 调用，中间不能有其他查询
        PageHelper.startPage(query.getSafePageNum(), query.getSafePageSize());
        List<Book> list = bookMapper.selectWithCondition(query);
        PageInfo<Book> info = new PageInfo<>(list);
        return new PageResult<>(
                list,
                info.getTotal(),
                info.getPageNum(),
                info.getPageSize()
        );
    }

    @Override
    public Book getDetail(Long id) {
        // 先浏览次数 +1，再查详情（两次独立查询，互不干扰）
        bookMapper.updateViewCount(id);
        Book book = bookMapper.selectByIdWithSeller(id);
        if (book == null) {
            throw new BizException("商品不存在");
        }
        return book;
    }

    @Override
    public PageResult<Book> getBySeller(Long sellerId, BookQuery query) {
        query.setSellerId(sellerId);
        return getPage(query);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BizException("商品不存在");
        }
        bookMapper.updateStatus(id, status);
    }

    @Override
    public Book getById(Long id) {
        return bookMapper.selectById(id);
    }
}
