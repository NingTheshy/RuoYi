package com.ruoyi.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.ruoyi"})
public class RuoYiAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuoYiAdminApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  若依RBAC权限系统启动成功   ﾞ(♥◠‿◠)ﾉﾞ");
    }
}
