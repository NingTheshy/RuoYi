package com.ruoyi.admin.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * OpenAPI 文档配置属性。
 */
@Validated
@ConfigurationProperties(prefix = "knife4j.openapi")
public class OpenApiProperties {

    @NotBlank(message = "knife4j.openapi.title 不能为空")
    private String title;

    @NotBlank(message = "knife4j.openapi.version 不能为空")
    private String version;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
