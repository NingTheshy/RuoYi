package com.ruoyi.common.core.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * <p>标注在 Controller 方法上，由 {@code OperLogAspect} 切面自动记录操作日志</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /** 操作模块（如"用户管理"） */
    String title() default "";

    /** 业务类型（0其他 1新增 2修改 3删除） */
    String businessType() default "0";

    /** 操作类别（0其他 1后台用户 2手机端用户） */
    String operatorType() default "0";

    /** 是否保存请求参数 */
    boolean isSaveRequestData() default true;

    /** 是否保存响应数据 */
    boolean isSaveResponseData() default true;
}
