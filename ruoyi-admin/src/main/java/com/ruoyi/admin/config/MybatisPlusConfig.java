package com.ruoyi.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.ruoyi.common.mybatis.interceptor.DataScopeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 责任链顺序：数据权限拦截器 → 分页拦截器 → ...
 * 数据权限拦截器必须在分页之前执行，否则分页 SQL 的行数统计会遗漏 WHERE 条件。
 * </p>
 *
 * <p>注册的拦截器：</p>
 * <ul>
 *   <li>{@link DataScopeInterceptor} - 数据权限拦截器，根据当前用户角色动态追加 SQL 条件</li>
 *   <li>{@link PaginationInnerInterceptor} - 分页插件，MySQL 方言</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器链
     *
     * @param dataScopeInterceptor 由 Spring 管理的数据权限拦截器
     * @return MybatisPlusInterceptor 包含数据权限和分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(DataScopeInterceptor dataScopeInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限过滤（必须在分页之前）
        interceptor.addInnerInterceptor(dataScopeInterceptor);
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
