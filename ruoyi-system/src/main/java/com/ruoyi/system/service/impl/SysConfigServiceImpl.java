package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysConfigConvert;
import com.ruoyi.system.domain.dto.req.SysConfigCreateReq;
import com.ruoyi.system.domain.dto.req.SysConfigQueryReq;
import com.ruoyi.system.domain.dto.req.SysConfigUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysConfigResp;
import com.ruoyi.system.domain.entity.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.SysConfigService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final String CONFIG_CACHE_KEY_PREFIX = "config:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    private final SysConfigConvert configConvert;
    private final RedisTemplate<String, Object> redisTemplate;

    public SysConfigServiceImpl(SysConfigConvert configConvert, RedisTemplate<String, Object> redisTemplate) {
        this.configConvert = configConvert;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageResult<SysConfigResp> getConfigPage(SysConfigQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysConfig query = configConvert.toEntity(queryReq);
        Page<SysConfig> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getConfigName()), SysConfig::getConfigName, query.getConfigName())
                .like(StringUtils.hasText(query.getConfigKey()), SysConfig::getConfigKey, query.getConfigKey())
                .eq(StringUtils.hasText(query.getConfigType()), SysConfig::getConfigType, query.getConfigType())
                .eq(StringUtils.hasText(query.getStatus()), SysConfig::getStatus, query.getStatus())
                .orderByAsc(SysConfig::getConfigId);
        Page<SysConfig> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(configConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public SysConfigResp getConfigById(Long configId) {
        SysConfig config = getById(configId);
        if (config == null) {
            throw new ServiceException(404, "参数配置不存在");
        }
        return configConvert.toResp(config);
    }

    @Override
    public String getConfigValueByKey(String configKey) {
        String cacheKey = CONFIG_CACHE_KEY_PREFIX + configKey;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached.toString();
        }
        SysConfig config = baseMapper.selectConfigByKey(configKey);
        String value = (config != null) ? config.getConfigValue() : "";
        redisTemplate.opsForValue().set(cacheKey, value, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return value;
    }

    @Override
    @Transactional
    public int createConfig(SysConfigCreateReq req) {
        SysConfig config = configConvert.toEntity(req);
        assertConfigKeyUnique(config.getConfigKey(), null);
        if (config.getStatus() == null) {
            config.setStatus(StatusFlag.NORMAL.getCode());
        }
        if (config.getConfigType() == null) {
            config.setConfigType("N");
        }
        boolean result = save(config);
        if (result) {
            clearConfigCache(config.getConfigKey());
        }
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateConfig(SysConfigUpdateReq req) {
        SysConfig existing = getById(req.getConfigId());
        if (existing == null) {
            throw new ServiceException(404, "参数配置不存在");
        }
        String oldKey = existing.getConfigKey();
        SysConfig config = configConvert.toEntity(req);
        assertConfigKeyUnique(config.getConfigKey(), config.getConfigId());
        boolean result = updateById(config);
        if (result) {
            clearConfigCache(oldKey);
            if (!oldKey.equals(config.getConfigKey())) {
                clearConfigCache(config.getConfigKey());
            }
        }
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = getById(configId);
            if (config != null) {
                if ("Y".equals(config.getConfigType())) {
                    throw new ServiceException("系统内置参数'" + config.getConfigKey() + "'不允许删除");
                }
                clearConfigCache(config.getConfigKey());
            }
        }
        return removeByIds(Arrays.asList(configIds)) ? configIds.length : 0;
    }

    @Override
    public void clearConfigCache(String configKey) {
        String cacheKey = CONFIG_CACHE_KEY_PREFIX + configKey;
        redisTemplate.delete(cacheKey);
    }

    private void assertConfigKeyUnique(String configKey, Long excludeConfigId) {
        SysConfig existing = baseMapper.selectConfigByKey(configKey);
        if (existing != null && !existing.getConfigId().equals(excludeConfigId)) {
            throw new ServiceException(400, "参数键名'" + configKey + "'已存在");
        }
    }
}
