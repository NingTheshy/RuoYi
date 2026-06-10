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

public class DataScopeInterceptor implements InnerInterceptor {

    private static final Pattern WHERE_PATTERN = Pattern.compile(
            "\\bWHERE\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        DataScopeParams params = DataScopeParams.getParams();
        if (params == null || params.getCondition() == null || params.getCondition().isBlank()) {
            return;
        }

        String originalSql = boundSql.getSql();
        String newSql = buildFilteredSql(originalSql, params.getCondition());
        setBoundSql(boundSql, newSql);
    }

    private String buildFilteredSql(String sql, String condition) {
        if (WHERE_PATTERN.matcher(sql).find()) {
            // SQL 已有 WHERE 子句，在末尾追加条件
            // 条件以 "AND " 开头，直接追加
            return sql + " " + condition;
        } else {
            // SQL 没有 WHERE 子句，插入 WHERE 1=1
            // 尝试在 ORDER BY / GROUP BY / LIMIT 之前插入
            String upperSql = sql.toUpperCase();
            int insertPos = findInsertPosition(upperSql);
            if (insertPos > 0) {
                return sql.substring(0, insertPos) + "WHERE 1=1 " + condition + " " + sql.substring(insertPos);
            }
            return sql + " WHERE 1=1 " + condition;
        }
    }

    private int findInsertPosition(String upperSql) {
        // 在 ORDER BY / GROUP BY / LIMIT / HAVING 之前插入
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
