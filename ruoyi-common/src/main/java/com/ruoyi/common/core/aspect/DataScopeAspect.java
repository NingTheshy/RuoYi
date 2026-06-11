package com.ruoyi.common.core.aspect;

import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.domain.DataScopeParams;
import com.ruoyi.common.security.service.DataScopeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据权限切面
 * <p>
 * 拦截所有标注了 {@link DataScope} 注解的 Service 方法，
 * 在方法执行前通过 {@link DataScopeService} 构建数据权限 SQL 条件，
 * 并存储到 {@link DataScopeParams} 的 ThreadLocal 中，
 * 供下游 {@link com.ruoyi.common.core.handler.DataScopeInterceptor} 在 SQL 执行前注入条件。
 * </p>
 *
 * <p>执行流程：</p>
 * <ol>
 *   <li>保存上一次的 DataScopeParams（支持嵌套调用）</li>
 *   <li>根据当前用户角色构建新的数据权限条件</li>
 *   <li>将条件存入 ThreadLocal</li>
 *   <li>执行目标方法</li>
 *   <li>恢复上一次的 DataScopeParams（finally 块保证异常时也能恢复）</li>
 * </ol>
 *
 * @author NingTheshy
 */
@Aspect
@Component
public class DataScopeAspect {

    @Autowired
    private DataScopeService dataScopeService;

    /**
     * 环绕通知：在目标方法执行前后注入数据权限逻辑
     *
     * @param joinPoint  连接点（被拦截的方法）
     * @param dataScope  数据权限注解实例，包含 alias、userIdColumn、deptIdColumn 属性
     * @return 目标方法的返回值
     * @throws Throwable 目标方法可能抛出的异常
     */
    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint joinPoint, DataScope dataScope) throws Throwable {
        // 保存上一次的参数，支持嵌套调用场景
        DataScopeParams previous = DataScopeParams.getParams();
        try {
            // 根据当前用户的角色 data_scope 构建 SQL 条件
            DataScopeParams params = dataScopeService.buildDataScopeCondition(
                    dataScope.alias(), dataScope.userIdColumn(), dataScope.deptIdColumn());
            DataScopeParams.setParams(params);
            return joinPoint.proceed();
        } finally {
            // 恢复上一次的参数，避免 ThreadLocal 泄漏
            DataScopeParams.setParams(previous);
        }
    }
}
