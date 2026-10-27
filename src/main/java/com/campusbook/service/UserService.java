package com.campusbook.service;

import com.campusbook.dto.LoginDTO;
import com.campusbook.dto.RegisterDTO;
import com.campusbook.entity.User;

/**
 * 用户业务接口
 * Controller 只依赖此接口，不直接接触实现类
 */
public interface UserService {

    /** 用户注册 */
    User register(RegisterDTO dto);

    /** 用户登录（返回完整用户信息供 Controller 存入 Session） */
    User login(LoginDTO dto);

    /** 按 ID 查询 */
    User getById(Long id);

    /** 按用户名查询 */
    User getByUsername(String username);
}
