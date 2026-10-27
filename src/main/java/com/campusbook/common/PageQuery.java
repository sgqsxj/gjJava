package com.campusbook.common;

import java.io.Serializable;

/**
 * 分页查询基类
 * 所有需要分页的请求 DTO 继承此类
 */
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码，默认 1 */
    private Integer pageNum = 1;

    /** 每页大小，默认 10 */
    private Integer pageSize = 10;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    /** 获取安全的 pageNum（避免 <=0） */
    public Integer getSafePageNum() {
        return (pageNum == null || pageNum < 1) ? 1 : pageNum;
    }

    /** 获取安全的 pageSize（限制最大 100） */
    public Integer getSafePageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return pageSize > 100 ? 100 : pageSize;
    }
}
