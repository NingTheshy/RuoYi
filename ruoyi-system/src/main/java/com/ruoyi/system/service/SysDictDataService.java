package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysDictDataCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictDataQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictDataUpdateReq;
import com.ruoyi.system.domain.dto.resp.DictDataOptionResp;
import com.ruoyi.system.domain.dto.resp.SysDictDataResp;

import java.util.List;

public interface SysDictDataService {
    PageResult<SysDictDataResp> getDictDataPage(SysDictDataQueryReq queryReq, Integer pageNum, Integer pageSize);
    List<SysDictDataResp> getDictDataList(SysDictDataQueryReq queryReq);
    List<DictDataOptionResp> getDictDataByType(String dictType);
    SysDictDataResp getDictDataById(Long dictCode);
    int createDictData(SysDictDataCreateReq req);
    int updateDictData(SysDictDataUpdateReq req);
    int deleteDictDataByIds(Long[] dictCodes);
    void clearDictCache(String dictType);
    long countDictDataByType(String dictType);
}