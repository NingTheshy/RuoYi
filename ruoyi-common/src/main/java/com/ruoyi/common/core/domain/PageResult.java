package com.ruoyi.common.core.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果封装
 * <p>
 * 统一的分页响应结构，包含数据列表和总记录数。
 * 前端根据 {@code total} 计算总页数，实现分页导航。
 * </p>
 *
 * @param <T> 数据行的类型
 * @author NingTheshy
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页数据列表 */
    private List<T> rows;

    /** 总记录数 */
    private long total;

    public PageResult() {}

    /**
     * 构造分页结果
     *
     * @param rows  当前页数据列表
     * @param total 总记录数
     */
    public PageResult(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }
}
