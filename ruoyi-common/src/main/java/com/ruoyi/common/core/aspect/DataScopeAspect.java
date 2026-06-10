package com.ruoyi.common.core.aspect;

import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.domain.DataScopeParams;
import com.ruoyi.common.security.service.DataScopeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataScopeAspect {

    @Autowired
    private DataScopeService dataScopeService;

    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint joinPoint, DataScope dataScope) throws Throwable {
        DataScopeParams previous = DataScopeParams.getParams();
        try {
            DataScopeParams params = dataScopeService.buildDataScopeCondition(
                    dataScope.alias(), dataScope.userIdColumn(), dataScope.deptIdColumn());
            DataScopeParams.setParams(params);
            return joinPoint.proceed();
        } finally {
            DataScopeParams.setParams(previous);
        }
    }
}
