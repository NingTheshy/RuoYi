package com.ruoyi.common.core.domain;

/**
 * 数据权限参数载体
 * <p>
 * 通过 ThreadLocal 在请求线程内传递数据权限 SQL 条件。
 * 生命周期由 {@link com.ruoyi.common.core.aspect.DataScopeAspect} 管理：
 * 方法执行前设置，执行后（finally 块）清除。
 * </p>
 *
 * <p>下游的 {@link com.ruoyi.common.core.handler.DataScopeInterceptor}
 * 在 SQL 执行前读取此参数，将条件注入到 SQL 的 WHERE 子句中。</p>
 *
 * @author NingTheshy
 */
public class DataScopeParams {

    /** ThreadLocal 存储，确保每个请求线程的数据权限条件互不干扰 */
    private static final ThreadLocal<DataScopeParams> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * SQL 条件片段
     * <p>格式示例："AND (dept_id IN (1,2,3) OR user_id = 1000)"</p>
     * <p>为 null 表示不过滤（全部数据权限）</p>
     */
    private String condition;

    public DataScopeParams(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    /**
     * 获取当前线程的数据权限参数
     *
     * @return DataScopeParams 实例，未设置时返回 null
     */
    public static DataScopeParams getParams() {
        return THREAD_LOCAL.get();
    }

    /**
     * 设置当前线程的数据权限参数
     * <p>传入 null 会清除 ThreadLocal，防止内存泄漏</p>
     *
     * @param params 数据权限参数，null 表示清除
     */
    public static void setParams(DataScopeParams params) {
        if (params == null) {
            THREAD_LOCAL.remove();
        } else {
            THREAD_LOCAL.set(params);
        }
    }
}
