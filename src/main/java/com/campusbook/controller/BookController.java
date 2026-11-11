package com.campusbook.controller;

import com.campusbook.common.PageResult;
import com.campusbook.common.Result;
import com.campusbook.dto.BookQuery;
import com.campusbook.dto.PublishBookDTO;
import com.campusbook.entity.Book;
import com.campusbook.entity.User;
import com.campusbook.service.BookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 商品 API 控制器
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Resource
    private BookService bookService;

    /**
     * 分页查询商品列表（公开，不需要登录）
     * GET /api/book/list?keyword=xxx&categoryId=1&pageNum=1&pageSize=6
     */
    @GetMapping("/list")
    public Result<PageResult<Book>> list(BookQuery query) {
        PageResult<Book> page = bookService.getPage(query);
        return Result.success(page);
    }

    /**
     * 商品详情（公开，浏览次数 +1）
     * GET /api/book/detail?id=1
     */
    @GetMapping("/detail")
    public Result<Book> detail(@RequestParam("id") Long id) {
        Book book = bookService.getDetail(id);
        return Result.success(book);
    }

    /**
     * 发布商品（需登录）
     * POST /api/book/publish   body: JSON
     */
    @PostMapping("/publish")
    public Result<Book> publish(@Valid @RequestBody PublishBookDTO dto,
                                HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        Book book = bookService.publish(currentUser.getId(), dto);
        return Result.success(book);
    }

    /**
     * 上架 / 下架（需登录 + 自己发布的商品，后续步骤扩展）
     */
    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam("status") Integer status) {
        bookService.updateStatus(id, status);
        return Result.success();
    }
}
