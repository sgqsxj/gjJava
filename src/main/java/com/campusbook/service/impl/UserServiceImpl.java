package com.campusbook.service.impl;

import com.campusbook.common.BizException;
import com.campusbook.dto.LoginDTO;
import com.campusbook.dto.RegisterDTO;
import com.campusbook.entity.User;
import com.campusbook.repository.UserMapper;
import com.campusbook.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 用户业务实现
 * @Service 注解注册到 Spring 容器
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User register(RegisterDTO dto) {
        // 1. 两次密码一致校验
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BizException("两次输入的密码不一致");
        }

        // 2. 用户名唯一性校验
        User exist = userMapper.selectByUsername(dto.getUsername());
        if (exist != null) {
            throw new BizException("用户名已被注册");
        }

        // 3. 构造用户实体，密码 MD5 加密存储
        User user = new User();
        user.setUsername(dto.getUsername());
        // 使用 Spring 自带 DigestUtils.md5DigestAsHex，等价于 MD5
        user.setPassword(md5(dto.getPassword()));
        user.setNickname(dto.getNickname() == null ? dto.getUsername() : dto.getNickname());
        user.setPhone(dto.getPhone());

        // 4. 插入
        userMapper.insert(user);

        // 5. 返回完整信息（含自增 ID）
        return userMapper.selectById(user.getId());
    }

    @Override
    public User login(LoginDTO dto) {
        // 1. 按用户名查询
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BizException("用户名不存在");
        }

        // 2. 密码比对（数据库存的是 MD5，所以对输入密码先 MD5 再比较）
        if (!user.getPassword().equals(md5(dto.getPassword()))) {
            throw new BizException("密码错误");
        }

        // 3. 清除密码后返回（防止密码泄漏到 Session / 前端）
        user.setPassword(null);
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    /**
     * MD5 加密
     */
    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
