package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysDictType;

import java.util.List;

public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    List<SysDictType> selectDictTypeList(SysDictType dictType);

    SysDictType selectDictTypeByType(String dictType);
}