package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysDictData;

import java.util.List;

public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    List<SysDictData> selectDictDataList(SysDictData dictData);

    List<SysDictData> selectDictDataByType(String dictType);

    long countDictDataByType(String dictType);
}