package com.ruoyi.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 标注在 Service 方法上，配合 {@link com.ruoyi.common.core.aspect.DataScopeAspect} 切面使用。
 * 切面会根据当前登录用户的角色 data_scope 值，动态生成 SQL 过滤条件，
 * 实现行级数据隔离，无需在业务代码中手动拼接 WHERE 子句。
 * </p>
 *
 * <p>数据权限级别（对应 sys_role.data_scope 字段）：</p>
 * <ul>
 *   <li>1 - 全部数据权限：不过滤</li>
 *   <li>2 - 自定义数据权限：只看 sys_role_dept 中配置的部门数据</li>
 *   <li>3 - 本部门数据权限：只看当前用户所在部门的数据</li>
 *   <li>4 - 本部门及以下数据权限：当前部门 + 所有子部门的数据</li>
 *   <li>5 - 仅本人数据权限：只看自己创建的数据</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

    /**
     * 表别名前缀，用于多表关联查询时指定列所属的表
     * <p>例如 "u." 表示 u.dept_id，空字符串表示不加前缀</p>
     */
    String alias() default "";

    /**
     * 用户ID列名，用于 scope=5（仅本人）时拼接条件
     * <p>默认 "user_id"，适用于 sys_user 表</p>
     */
    String userIdColumn() default "user_id";

    /**
     * 部门ID列名，用于 scope=2/3/4 时拼接条件
     * <p>默认 "dept_id"，适用于 sys_user/sys_dept 表</p>
     */
    String deptIdColumn() default "dept_id";
}
