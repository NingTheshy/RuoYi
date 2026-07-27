package com.ruoyi.common.security.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目级安全相关配置属性。
 */
@Validated
@ConfigurationProperties(prefix = "ruoyi")
public class RuoyiProperties {

    @Valid
    private final Security security = new Security();

    @Valid
    private final Cors cors = new Cors();

    public Security getSecurity() {
        return security;
    }

    public Cors getCors() {
        return cors;
    }

    public static class Security {
        /**
         * Token 请求头名称。
         */
        @NotBlank(message = "ruoyi.security.token-header 不能为空")
        private String tokenHeader;

        /**
         * Token 前缀。
         */
        @NotBlank(message = "ruoyi.security.token-prefix 不能为空")
        private String tokenPrefix;

        public String getTokenHeader() {
            return tokenHeader;
        }

        public void setTokenHeader(String tokenHeader) {
            this.tokenHeader = tokenHeader;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }
    }

    public static class Cors {
        /**
         * 允许的跨域来源。
         */
        private List<String> allowedOrigins = new ArrayList<>();

        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }
}
