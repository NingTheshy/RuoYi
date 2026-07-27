package com.ruoyi.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 配置 {@link RedisTemplate} 的序列化策略：
 * </p>
 * <ul>
 *   <li>Key 使用 {@link StringRedisSerializer} - 可读的字符串键</li>
 *   <li>Value 使用 {@link GenericJackson2JsonRedisSerializer} - JSON 格式存储</li>
 * </ul>
 *
 * <p>安全说明：使用 {@link BasicPolymorphicTypeValidator} 限制反序列化的类型范围，
 * 仅允许 Object 基类型的多态反序列化，防止恶意类注入攻击。</p>
 *
 * @author NingTheshy
 */
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate，设置 Key/Value 的序列化方式
     *
     * @param factory Redis 连接工厂（由 Spring Boot 自动配置）
     * @param objectMapper Spring 全局 Jackson 配置对应的 ObjectMapper
     * @return 配置好的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory,
                                                       ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 基于 Spring 全局配置复制一份，避免 Redis 序列化风格和接口 JSON 输出脱节
        ObjectMapper redisObjectMapper = objectMapper.copy();
        redisObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 安全的多态类型验证器，仅允许 Object 基类型
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();
        redisObjectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        // copy() 会继承全局模块，这里补注册一次以保证独立使用时也支持 Java 时间类型
        redisObjectMapper.registerModule(new JavaTimeModule());

        // JSON 序列化器（值使用）
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);
        // 字符串序列化器（键使用）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // Key 序列化方式
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        // Value 序列化方式
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
