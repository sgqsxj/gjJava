package com.campusbook.repository;

import com.campusbook.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类 Mapper 接口
 */
@Mapper
public interface CategoryMapper {

    /**
     * 按 sort 升序查询全部分类
     */
    List<Category> selectAllBySort();
}
