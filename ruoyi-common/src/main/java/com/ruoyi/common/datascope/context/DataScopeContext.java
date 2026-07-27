package com.ruoyi.common.datascope.context;

/**
 * 数据权限上下文
 * <p>
 * 通过 ThreadLocal 在请求线程内传递数据权限 SQL 条件。
 * </p>
 *
 * @author NingTheshy
 */
public class DataScopeContext {

    private static final ThreadLocal<DataScopeContext> THREAD_LOCAL = new ThreadLocal<>();

    private final boolean allowAll;

    private final boolean denyAll;

    private final String condition;

    private DataScopeContext(boolean allowAll, boolean denyAll, String condition) {
        this.allowAll = allowAll;
        this.denyAll = denyAll;
        this.condition = condition;
    }

    public static DataScopeContext allowAll() {
        return new DataScopeContext(true, false, null);
    }

    public static DataScopeContext denyAll() {
        return new DataScopeContext(false, true, null);
    }

    public static DataScopeContext ofCondition(String condition) {
        return new DataScopeContext(false, false, condition);
    }

    public boolean isAllowAll() {
        return allowAll;
    }

    public boolean isDenyAll() {
        return denyAll;
    }

    public String getCondition() {
        return condition;
    }

    public static DataScopeContext get() {
        return THREAD_LOCAL.get();
    }

    public static void set(DataScopeContext context) {
        if (context == null) {
            THREAD_LOCAL.remove();
        } else {
            THREAD_LOCAL.set(context);
        }
    }
}
