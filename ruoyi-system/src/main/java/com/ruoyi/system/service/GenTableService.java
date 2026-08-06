package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.GenTableCreateReq;
import com.ruoyi.system.domain.dto.req.GenTableQueryReq;
import com.ruoyi.system.domain.dto.req.GenTableUpdateReq;
import com.ruoyi.system.domain.dto.req.GenSyncReq;
import com.ruoyi.system.domain.dto.resp.GenPreviewResp;
import com.ruoyi.system.domain.dto.resp.GenTableResp;

public interface GenTableService {

    PageResult<GenTableResp> getTablePage(GenTableQueryReq queryReq, Integer pageNum, Integer pageSize);

    GenTableResp getTableById(Long tableId);

    int createTable(GenTableCreateReq req);

    int updateTable(GenTableUpdateReq req);

    int deleteTableByIds(Long[] tableIds);

    void syncTables(GenSyncReq req);

    GenPreviewResp previewCode(Long tableId);

    byte[] generateCode(Long tableId);
}
