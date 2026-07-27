package com.ruoyi.common.datascope.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 标注在 Service 方法上，配合 {@link com.ruoyi.common.datascope.aspect.DataScopeAspect} 切面使用。
 * </p>
 *
 * @author NingTheshy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    String alias() default "";

    String userIdColumn() default "user_id";

    String deptIdColumn() default "dept_id";

    boolean enableUserScope() default true;
}
