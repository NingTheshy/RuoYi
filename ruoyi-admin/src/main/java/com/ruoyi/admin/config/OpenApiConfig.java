package com.ruoyi.admin.config;

import com.ruoyi.common.security.config.RuoyiProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文档配置。
 */
@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(OpenApiProperties openApiProperties, RuoyiProperties ruoyiProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title(openApiProperties.getTitle())
                        .version(openApiProperties.getVersion())
                        .description("RuoYi RBAC 权限管理系统接口文档"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name(ruoyiProperties.getSecurity().getTokenHeader())
                                .description("在请求头中填写 Bearer Token，例如："
                                        + ruoyiProperties.getSecurity().getTokenPrefix() + "<token>")));
    }
}
