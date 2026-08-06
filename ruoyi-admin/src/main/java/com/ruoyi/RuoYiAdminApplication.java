package com.ruoyi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 若依 RBAC 权限管理系统启动入口
 */
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.ruoyi")
@EnableAsync

    public class RuoYiAdminApplication {
    private static final Logger log = LoggerFactory.getLogger(RuoYiAdminApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RuoYiAdminApplication.class, args);
        log.info("若依 RBAC 权限系统启动成功");
    }
}
