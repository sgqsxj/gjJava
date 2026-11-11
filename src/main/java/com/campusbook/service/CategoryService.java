package com.campusbook.service;

import com.campusbook.entity.Category;

import java.util.List;

/**
 * 分类业务接口
 */
public interface CategoryService {

    /**
     * 获取全部分类（按 sort 排序）
     */
    List<Category> getAll();
}
