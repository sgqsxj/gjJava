package com.campusbook.controller;

import com.campusbook.entity.Category;
import com.campusbook.entity.User;
import com.campusbook.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 页面控制器
 * 负责所有 Thymeleaf 模板渲染，统一注入当前登录用户和全部分类
 */
@Controller
public class PageController {

    @Resource
    private CategoryService categoryService;

    /** 登录页 */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /** 注册页 */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * 商品列表页（公开）
     * GET /book?keyword=xxx&categoryId=1
     */
    @GetMapping("/book")
    public String bookListPage(HttpSession session, Model model) {
        injectCommon(session, model);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "book-list";
    }

    /**
     * 商品详情页（公开）
     * GET /book/detail/{id}
     */
    @GetMapping("/book/detail/{id}")
    public String bookDetailPage(@PathVariable Long id,
                                 HttpSession session, Model model) {
        injectCommon(session, model);
        model.addAttribute("bookId", id);
        return "book-detail";
    }

    /**
     * 发布商品页（需登录，WebMvcConfig 已拦截）
     * GET /book/publish
     */
    @GetMapping("/book/publish")
    public String bookPublishPage(HttpSession session, Model model) {
        injectCommon(session, model);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);
        return "book-publish";
    }

    /**
     * 每个页面都需要：当前登录用户 + 全部分类（用于顶栏分类筛选）
     */
    private void injectCommon(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
    }
}
