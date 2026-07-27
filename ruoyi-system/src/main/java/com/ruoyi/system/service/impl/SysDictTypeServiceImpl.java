package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysDictTypeConvert;
import com.ruoyi.system.domain.dto.req.SysDictTypeCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDictTypeResp;
import com.ruoyi.system.domain.entity.SysDictType;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.service.SysDictDataService;
import com.ruoyi.system.service.SysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    private final SysDictTypeConvert dictTypeConvert;
    private final SysDictDataService dictDataService;

    public SysDictTypeServiceImpl(SysDictTypeConvert dictTypeConvert, SysDictDataService dictDataService) {
        this.dictTypeConvert = dictTypeConvert;
        this.dictDataService = dictDataService;
    }

    @Override
    public PageResult<SysDictTypeResp> getDictTypePage(SysDictTypeQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysDictType query = dictTypeConvert.toEntity(queryReq);
        Page<SysDictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getDictName()), SysDictType::getDictName, query.getDictName())
                .like(StringUtils.hasText(query.getDictType()), SysDictType::getDictType, query.getDictType())
                .eq(StringUtils.hasText(query.getStatus()), SysDictType::getStatus, query.getStatus())
                .orderByAsc(SysDictType::getDictId);
        Page<SysDictType> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(dictTypeConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public List<SysDictTypeResp> getDictTypeList(SysDictTypeQueryReq queryReq) {
        SysDictType query = dictTypeConvert.toEntity(queryReq);
        return dictTypeConvert.toRespList(baseMapper.selectDictTypeList(query));
    }

    @Override
    public SysDictTypeResp getDictTypeById(Long dictId) {
        SysDictType dictType = getById(dictId);
        if (dictType == null) {
            throw new ServiceException(404, "字典类型不存在");
        }
        return dictTypeConvert.toResp(dictType);
    }

    @Override
    @Transactional
    public int createDictType(SysDictTypeCreateReq req) {
        SysDictType dictType = dictTypeConvert.toEntity(req);
        assertDictTypeUnique(dictType.getDictType(), null);
        if (dictType.getStatus() == null) {
            dictType.setStatus(StatusFlag.NORMAL.getCode());
        }
        return save(dictType) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateDictType(SysDictTypeUpdateReq req) {
        SysDictType existing = getById(req.getDictId());
        if (existing == null) {
            throw new ServiceException(404, "字典类型不存在");
        }
        String oldType = existing.getDictType();
        SysDictType dictType = dictTypeConvert.toEntity(req);
        assertDictTypeUnique(dictType.getDictType(), dictType.getDictId());
        boolean result = updateById(dictType);
        if (!oldType.equals(dictType.getDictType())) {
            dictDataService.clearDictCache(oldType);
        }
        dictDataService.clearDictCache(dictType.getDictType());
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = getById(dictId);
            if (dictType != null) {
                long dataCount = dictDataService.countDictDataByType(dictType.getDictType());
                if (dataCount > 0) {
                    throw new ServiceException("存在关联的字典数据，不允许删除");
                }
                dictDataService.clearDictCache(dictType.getDictType());
            }
        }
        return removeByIds(Arrays.asList(dictIds)) ? dictIds.length : 0;
    }

    private void assertDictTypeUnique(String dictType, Long excludeDictId) {
        SysDictType existing = baseMapper.selectDictTypeByType(dictType);
        if (existing != null && !existing.getDictId().equals(excludeDictId)) {
            throw new ServiceException(400, "字典类型'" + dictType + "'已存在");
        }
    }
}