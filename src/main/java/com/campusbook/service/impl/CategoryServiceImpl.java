package com.campusbook.service.impl;

import com.campusbook.entity.Category;
import com.campusbook.repository.CategoryMapper;
import com.campusbook.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类业务实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAll() {
        return categoryMapper.selectAllBySort();
    }
}
