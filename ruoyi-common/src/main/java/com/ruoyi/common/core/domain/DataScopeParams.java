package com.ruoyi.common.core.domain;

public class DataScopeParams {

    private static final ThreadLocal<DataScopeParams> THREAD_LOCAL = new ThreadLocal<>();

    /** SQL 条件片段，如 "AND (dept_id IN (...) OR user_id = 1000)" */
    private String condition;

    public DataScopeParams(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public static DataScopeParams getParams() {
        return THREAD_LOCAL.get();
    }

    public static void setParams(DataScopeParams params) {
        if (params == null) {
            THREAD_LOCAL.remove();
        } else {
            THREAD_LOCAL.set(params);
        }
    }
}
