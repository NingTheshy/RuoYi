package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysDictDataConvert;
import com.ruoyi.system.domain.dto.req.SysDictDataCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictDataQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictDataUpdateReq;
import com.ruoyi.system.domain.dto.resp.DictDataOptionResp;
import com.ruoyi.system.domain.dto.resp.SysDictDataResp;
import com.ruoyi.system.domain.entity.SysDictData;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.SysDictDataService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    private static final String DICT_CACHE_KEY_PREFIX = "dict:type:";
    private static final long CACHE_EXPIRE_HOURS = 1;

    private final SysDictDataConvert dictDataConvert;
    private final RedisTemplate<String, Object> redisTemplate;

    public SysDictDataServiceImpl(SysDictDataConvert dictDataConvert, RedisTemplate<String, Object> redisTemplate) {
        this.dictDataConvert = dictDataConvert;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageResult<SysDictDataResp> getDictDataPage(SysDictDataQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysDictData query = dictDataConvert.toEntity(queryReq);
        Page<SysDictData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getDictType()), SysDictData::getDictType, query.getDictType())
                .like(StringUtils.hasText(query.getDictLabel()), SysDictData::getDictLabel, query.getDictLabel())
                .eq(StringUtils.hasText(query.getStatus()), SysDictData::getStatus, query.getStatus())
                .orderByAsc(SysDictData::getDictSort)
                .orderByAsc(SysDictData::getDictCode);
        Page<SysDictData> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(dictDataConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public List<SysDictDataResp> getDictDataList(SysDictDataQueryReq queryReq) {
        SysDictData query = dictDataConvert.toEntity(queryReq);
        return dictDataConvert.toRespList(baseMapper.selectDictDataList(query));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DictDataOptionResp> getDictDataByType(String dictType) {
        String cacheKey = DICT_CACHE_KEY_PREFIX + dictType;
        List<DictDataOptionResp> cached = (List<DictDataOptionResp>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<SysDictData> dataList = baseMapper.selectDictDataByType(dictType);
        List<DictDataOptionResp> result = dictDataConvert.toOptionRespList(dataList);
        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return result;
    }

    @Override
    public SysDictDataResp getDictDataById(Long dictCode) {
        SysDictData dictData = getById(dictCode);
        if (dictData == null) {
            throw new ServiceException(404, "字典数据不存在");
        }
        return dictDataConvert.toResp(dictData);
    }

    @Override
    @Transactional
    public int createDictData(SysDictDataCreateReq req) {
        SysDictData dictData = dictDataConvert.toEntity(req);
        if (dictData.getStatus() == null) {
            dictData.setStatus(StatusFlag.NORMAL.getCode());
        }
        if (dictData.getIsDefault() == null) {
            dictData.setIsDefault("N");
        }
        if (dictData.getDictSort() == null) {
            dictData.setDictSort(0);
        }
        boolean result = save(dictData);
        if (result) {
            clearDictCache(dictData.getDictType());
        }
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateDictData(SysDictDataUpdateReq req) {
        SysDictData existing = getById(req.getDictCode());
        if (existing == null) {
            throw new ServiceException(404, "字典数据不存在");
        }
        String oldType = existing.getDictType();
        SysDictData dictData = dictDataConvert.toEntity(req);
        boolean result = updateById(dictData);
        if (result) {
            clearDictCache(oldType);
            if (!oldType.equals(dictData.getDictType())) {
                clearDictCache(dictData.getDictType());
            }
        }
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData dictData = getById(dictCode);
            if (dictData != null) {
                clearDictCache(dictData.getDictType());
            }
        }
        return removeByIds(Arrays.asList(dictCodes)) ? dictCodes.length : 0;
    }

    @Override
    public void clearDictCache(String dictType) {
        String cacheKey = DICT_CACHE_KEY_PREFIX + dictType;
        redisTemplate.delete(cacheKey);
    }

    @Override
    public long countDictDataByType(String dictType) {
        return baseMapper.countDictDataByType(dictType);
    }
}