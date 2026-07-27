package com.ruoyi.common.mybatis.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.ruoyi.common.datascope.context.DataScopeContext;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据权限 SQL 拦截器
 * <p>
 * MyBatis-Plus 的 InnerInterceptor 实现，在 SQL 执行前读取 {@link DataScopeContext} 中的条件，
 * 动态注入到 SQL 的 WHERE 子句中，实现行级数据隔离。
 * </p>
 *
 * <p>SQL 处理策略：</p>
 * <ul>
 *   <li>基于 JSqlParser 解析 SELECT 语句，而不是直接拼接字符串</li>
 *   <li>如果已有 WHERE 子句 → 合并为 {@code (原条件) AND (数据权限条件)}</li>
 *   <li>如果没有 WHERE 子句 → 直接设置数据权限条件</li>
 *   <li>解析失败时记录告警并回退原始 SQL，避免影响主查询执行</li>
 * </ul>
 *
 * <p>注意：此拦截器必须在分页插件之前注册，否则分页的行数统计会遗漏条件。</p>
 *
 * @author NingTheshy
 */
@Component
public class DataScopeInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DataScopeInterceptor.class);

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
        DataScopeContext context = DataScopeContext.get();
        if (context == null || context.isAllowAll()) {
            return;
        }

        String condition = context.isDenyAll() ? "1=0" : context.getCondition();
        if (!StringUtils.hasText(condition)) {
            return;
        }

        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        String originalSql = normalizeSql(mpBoundSql.sql());
        try {
            mpBoundSql.sql(parserSingle(originalSql, condition));
        } catch (Exception e) {
            log.warn("数据权限 SQL 解析失败，回退原始 SQL: msId={}, sql={}", ms.getId(), originalSql, e);
        }
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        String condition = (String) obj;
        if (!StringUtils.hasText(condition)) {
            return;
        }
        processSelectBody(select.getSelectBody(), condition);
        List<WithItem> withItems = select.getWithItemsList();
        if (withItems != null) {
            withItems.forEach(withItem -> processSelectBody(withItem, condition));
        }
    }

    private void processSelectBody(SelectBody selectBody, String condition) {
        if (selectBody instanceof PlainSelect plainSelect) {
            setWhere(plainSelect, condition);
            return;
        }
        if (selectBody instanceof SetOperationList setOperationList) {
            for (SelectBody child : setOperationList.getSelects()) {
                if (child instanceof PlainSelect plainSelect) {
                    setWhere(plainSelect, condition);
                }
            }
        }
    }

    private void setWhere(PlainSelect plainSelect, String condition) {
        Expression mergedCondition = buildMergedCondition(plainSelect.getWhere(), condition);
        if (mergedCondition != null) {
            plainSelect.setWhere(mergedCondition);
        }
    }

    private Expression buildMergedCondition(Expression originalWhere, String condition) {
        try {
            if (originalWhere == null) {
                return CCJSqlParserUtil.parseCondExpression(condition);
            }
            return CCJSqlParserUtil.parseCondExpression("(" + originalWhere + ") AND (" + condition + ")");
        } catch (JSQLParserException e) {
            throw new IllegalStateException("构建数据权限条件失败", e);
        }
    }

    private String normalizeSql(String sql) {
        return sql == null ? null : sql.replaceAll("[\\t\\n\\r]+", " ").replaceAll(" {2,}", " ").trim();
    }
}
