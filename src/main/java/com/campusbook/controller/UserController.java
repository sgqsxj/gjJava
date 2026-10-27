package com.campusbook.controller;

import com.campusbook.common.Result;
import com.campusbook.dto.LoginDTO;
import com.campusbook.dto.RegisterDTO;
import com.campusbook.entity.User;
import com.campusbook.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 用户控制器
 * 负责注册、登录、登出、获取当前用户信息
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final String SESSION_USER_KEY = "currentUser";

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(dto);
        user.setPassword(null);
        return Result.success(user, "注册成功");
    }

    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody LoginDTO dto, HttpSession session) {
        User user = userService.login(dto);
        session.setAttribute(SESSION_USER_KEY, user);
        return Result.success(user, "登录成功");
    }

    /**
     * 用户登出
     * POST /api/user/logout
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.invalidate();
        return Result.success("登出成功");
    }

    /**
     * 获取当前登录用户信息
     * GET /api/user/current
     */
    @GetMapping("/current")
    public Result<User> current(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_KEY) == null) {
            return Result.unauthorized("未登录");
        }
        User user = (User) session.getAttribute(SESSION_USER_KEY);
        return Result.success(user);
    }
}
