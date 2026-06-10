package com.ruoyi.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    /** 表别名前缀，如 "u." 或 "" */
    String alias() default "";

    /** 用户ID列名（scope 5 仅本人用） */
    String userIdColumn() default "user_id";

    /** 部门ID列名（scope 2/3/4 用） */
    String deptIdColumn() default "dept_id";
}
