package com.ruoyi.system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * system 模块 MyBatis 扫描配置
 */
@Configuration
@MapperScan("com.ruoyi.system.mapper")
public class SystemMybatisConfig {
}
