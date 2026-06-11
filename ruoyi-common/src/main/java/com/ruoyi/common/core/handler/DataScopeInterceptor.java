package com.ruoyi.common.core.handler;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.ruoyi.common.core.domain.DataScopeParams;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * 数据权限 SQL 拦截器
 * <p>
 * MyBatis-Plus 的 InnerInterceptor 实现，在 SQL 执行前读取 {@link DataScopeParams} 中的条件，
 * 动态注入到 SQL 的 WHERE 子句中，实现行级数据隔离。
 * </p>
 *
 * <p>SQL 注入策略：</p>
 * <ul>
 *   <li>如果 SQL 已有 WHERE 子句 → 在末尾追加 AND 条件</li>
 *   <li>如果没有 WHERE 子句 → 在 ORDER BY/GROUP BY/LIMIT/HAVING 之前插入 WHERE 1=1 AND 条件</li>
 *   <li>如果都没有 → 在 SQL 末尾追加 WHERE 1=1 AND 条件</li>
 * </ul>
 *
 * <p>注意：此拦截器必须在分页插件之前注册，否则分页的行数统计会遗漏条件。</p>
 *
 * @author NingTheshy
 */
public class DataScopeInterceptor implements InnerInterceptor {

    /** SQL WHERE 关键字的正则匹配（不区分大小写） */
    private static final Pattern WHERE_PATTERN = Pattern.compile(
            "\\bWHERE\\b", Pattern.CASE_INSENSITIVE);

    /**
     * 在查询执行前拦截 SQL，注入数据权限条件
     *
     * @param executor       MyBatis 执行器
     * @param ms             MappedStatement（映射的 SQL 语句）
     * @param parameter      SQL 参数
     * @param rowBounds      行边界
     * @param resultHandler  结果处理器
     * @param boundSql       绑定的 SQL 对象
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        // 获取当前线程的数据权限参数
        DataScopeParams params = DataScopeParams.getParams();
        if (params == null || params.getCondition() == null || params.getCondition().isBlank()) {
            return;
        }

        String originalSql = boundSql.getSql();
        String newSql = buildFilteredSql(originalSql, params.getCondition());
        // 通过反射修改 BoundSql 中的 SQL
        setBoundSql(boundSql, newSql);
    }

    /**
     * 构建注入数据权限条件后的 SQL
     *
     * <p>注入策略：始终在 ORDER BY / GROUP BY / LIMIT / HAVING 之前插入条件，
     * 确保数据权限条件不会被错误地拼接到 ORDER BY 子句中。</p>
     *
     * @param sql       原始 SQL
     * @param condition 数据权限条件（以 "AND " 开头）
     * @return 注入条件后的 SQL
     */
    private String buildFilteredSql(String sql, String condition) {
        String upperSql = sql.toUpperCase();
        int insertPos = findInsertPosition(upperSql);

        if (WHERE_PATTERN.matcher(sql).find()) {
            // SQL 已有 WHERE 子句，在 ORDER BY/GROUP BY/LIMIT/HAVING 之前插入 AND 条件
            if (insertPos > 0) {
                return sql.substring(0, insertPos) + condition + " " + sql.substring(insertPos);
            }
            // 没有 ORDER BY 等子句，直接在末尾追加
            return sql + " " + condition;
        } else {
            // SQL 没有 WHERE 子句，在 ORDER BY/GROUP BY/LIMIT/HAVING 之前插入 WHERE 1=1 AND 条件
            if (insertPos > 0) {
                return sql.substring(0, insertPos) + "WHERE 1=1 " + condition + " " + sql.substring(insertPos);
            }
            return sql + " WHERE 1=1 " + condition;
        }
    }

    /**
     * 查找 SQL 中 ORDER BY / GROUP BY / LIMIT / HAVING 关键字的最早位置
     * <p>用于确定 WHERE 条件的插入点</p>
     *
     * @param upperSql 大写的 SQL 字符串
     * @return 插入位置，-1 表示未找到（追加到末尾）
     */
    private int findInsertPosition(String upperSql) {
        String[] keywords = {"ORDER BY", "GROUP BY", "LIMIT", "HAVING"};
        int minPos = Integer.MAX_VALUE;
        for (String keyword : keywords) {
            int pos = upperSql.indexOf(keyword);
            if (pos > 0 && pos < minPos) {
                minPos = pos;
            }
        }
        return minPos < Integer.MAX_VALUE ? minPos : -1;
    }

    /**
     * 通过反射修改 BoundSql 中的 SQL 字符串
     * <p>BoundSql.sql 是 private final 字段，只能通过反射修改</p>
     *
     * @param boundSql BoundSql 实例
     * @param sql      新的 SQL 字符串
     */
    private void setBoundSql(BoundSql boundSql, String sql) {
        try {
            Field field = BoundSql.class.getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, sql);
        } catch (Exception e) {
            throw new RuntimeException("DataScopeInterceptor 修改 SQL 失败", e);
        }
    }
}
