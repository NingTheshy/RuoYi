package com.ruoyi.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 若依RBAC权限管理系统 - 启动入口
 * <p>
 * 本类是 Spring Boot 应用的主启动类，负责引导整个应用程序的初始化。
 * 通过 {@code @ComponentScan} 显式指定扫描的基础包，确保各模块的 Bean 能被正确注册到 IoC 容器中。
 * </p>
 *
 * <p>模块结构：</p>
 * <ul>
 *   <li>{@code com.ruoyi.admin} - 管理后台启动模块（配置、入口）</li>
 *   <li>{@code com.ruoyi.common} - 公共模块（安全、Redis、异常处理、工具类）</li>
 *   <li>{@code com.ruoyi.system} - 系统业务模块（用户、角色、部门、菜单）</li>
 * </ul>
 *
 * @author NingTheshy
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ruoyi.admin", "com.ruoyi.common", "com.ruoyi.system"})
public class RuoYiAdminApplication {

    /**
     * 应用程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(RuoYiAdminApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依RBAC权限系统启动成功   ﾞ(♥◠‿◠)ﾉﾞ");
    }
}
