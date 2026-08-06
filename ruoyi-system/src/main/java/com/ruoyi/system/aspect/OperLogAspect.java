package com.ruoyi.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.annotation.OperLog;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.entity.SysOperLog;
import com.ruoyi.system.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 * <p>拦截标注了 {@link OperLog} 的方法，自动记录操作日志</p>
 */
@Aspect
@Component
public class OperLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperLogAspect.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SysOperLogService operLogService;

    public OperLogAspect(SysOperLogService operLogService) {
        this.operLogService = operLogService;
    }

    @Around("@annotation(operLogAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLogAnnotation) {
        SysOperLog operLog = buildOperLog(joinPoint, operLogAnnotation);
        Object result;
        try {
            result = joinPoint.proceed();
            operLog.setStatus(0);
            operLog.setErrorMsg("");
            if (operLogAnnotation.isSaveResponseData()) {
                operLog.setJsonResult(truncate(toJson(result), 2000));
            }
            return result;
        } catch (Throwable ex) {
            operLog.setStatus(1);
            operLog.setErrorMsg(truncate(ex.getMessage(), 2000));
            operLog.setJsonResult("");
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new RuntimeException(ex);
        } finally {
            operLog.setOperTime(LocalDateTime.now());
            asyncSaveLog(operLog);
        }
    }

    private SysOperLog buildOperLog(ProceedingJoinPoint joinPoint, OperLog annotation) {
        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(annotation.title());
        operLog.setBusinessType(annotation.businessType());
        operLog.setOperatorType(annotation.operatorType());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        operLog.setMethod(className + "." + methodName + "()");

        HttpServletRequest request = getRequest();
        if (request != null) {
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperUrl(request.getRequestURI());
            operLog.setOperIp(getIpAddress(request));
        }

        operLog.setOperName(SecurityUtils.getCurrentUsernameOrAnonymous());

        if (annotation.isSaveRequestData()) {
            operLog.setOperParam(truncate(toJson(joinPoint.getArgs()), 2000));
        }

        return operLog;
    }

    @Async
    public void asyncSaveLog(SysOperLog operLog) {
        try {
            operLogService.saveOperLog(operLog);
        } catch (Exception e) {
            log.error("[操作日志] 保存失败: {}", e.getMessage(), e);
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
