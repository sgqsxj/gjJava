package com.campusbook.repository;

import com.campusbook.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问接口
 * MyBatis Mapper，由 @MapperScan 扫描注册到 Spring 容器
 */
@Mapper
public interface UserMapper {

    /** 按 ID 查询 */
    User selectById(@Param("id") Long id);

    /** 按用户名查询（登录 + 注册唯一性校验） */
    User selectByUsername(@Param("username") String username);

    /** 插入新用户 */
    int insert(User user);

    /** 更新用户信息 */
    int updateById(User user);
}
