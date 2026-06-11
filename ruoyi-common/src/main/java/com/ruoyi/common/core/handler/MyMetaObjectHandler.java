package com.ruoyi.common.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * <p>
 * 在实体对象执行 INSERT 或 UPDATE 操作时，自动填充审计字段：
 * </p>
 * <ul>
 *   <li>INSERT 时填充：createBy、createTime、updateBy、updateTime</li>
 *   <li>UPDATE 时填充：updateBy、updateTime</li>
 * </ul>
 *
 * <p>当前用户从 Spring Security 的 SecurityContextHolder 中获取，
 * 未认证时默认填 "system"。</p>
 *
 * @author NingTheshy
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * INSERT 操作自动填充
     *
     * @param metaObject 元对象（包含实体的字段信息）
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        String currentUser = getCurrentUser();
        this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }

    /**
     * UPDATE 操作自动填充
     *
     * @param metaObject 元对象（包含实体的字段信息）
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        String currentUser = getCurrentUser();
        this.strictUpdateFill(metaObject, "updateBy", String.class, currentUser);
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }

    /**
     * 获取当前登录用户的用户名
     * <p>
     * 从 Spring Security 上下文中获取：
     * - authentication.getDetails() 存储的是用户名（由 JwtAuthenticationFilter 设置）
     * - authentication.getPrincipal() 存储的是 userId
     * 未认证时返回 "system"
     * </p>
     *
     * @return 当前用户名
     */
    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() != null) {
            return authentication.getDetails().toString();
        }
        return "system";
    }
}
