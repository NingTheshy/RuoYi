package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysDictTypeCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDictTypeResp;

import java.util.List;

public interface SysDictTypeService {
    PageResult<SysDictTypeResp> getDictTypePage(SysDictTypeQueryReq queryReq, Integer pageNum, Integer pageSize);
    List<SysDictTypeResp> getDictTypeList(SysDictTypeQueryReq queryReq);
    SysDictTypeResp getDictTypeById(Long dictId);
    int createDictType(SysDictTypeCreateReq req);
    int updateDictType(SysDictTypeUpdateReq req);
    int deleteDictTypeByIds(Long[] dictIds);
}