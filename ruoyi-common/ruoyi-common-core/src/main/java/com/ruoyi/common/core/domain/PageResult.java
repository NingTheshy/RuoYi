package com.ruoyi.common.core.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> rows;
    private long total;

    public PageResult() {}

    public PageResult(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }
}
