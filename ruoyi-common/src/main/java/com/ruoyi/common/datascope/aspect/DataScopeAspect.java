package com.ruoyi.common.datascope.aspect;

import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.common.datascope.context.DataScopeContext;
import com.ruoyi.common.datascope.service.DataScopeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 数据权限切面
 *
 * @author NingTheshy
 */
@Aspect
@Component
public class DataScopeAspect {

    private final DataScopeService dataScopeService;

    public DataScopeAspect(DataScopeService dataScopeService) {
        this.dataScopeService = dataScopeService;
    }

    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint joinPoint, DataScope dataScope) throws Throwable {
        DataScopeContext previous = DataScopeContext.get();
        try {
            DataScopeContext context = dataScopeService.buildDataScopeCondition(
                    dataScope.alias(),
                    dataScope.userIdColumn(),
                    dataScope.deptIdColumn(),
                    dataScope.enableUserScope());
            DataScopeContext.set(context);
            return joinPoint.proceed();
        } finally {
            DataScopeContext.set(previous);
        }
    }
}
