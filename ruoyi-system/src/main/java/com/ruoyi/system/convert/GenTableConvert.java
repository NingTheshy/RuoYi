package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.req.GenTableCreateReq;
import com.ruoyi.system.domain.dto.req.GenTableQueryReq;
import com.ruoyi.system.domain.dto.req.GenTableUpdateReq;
import com.ruoyi.system.domain.dto.resp.GenTableColumnResp;
import com.ruoyi.system.domain.dto.resp.GenTableResp;
import com.ruoyi.system.domain.entity.GenTable;
import com.ruoyi.system.domain.entity.GenTableColumn;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface GenTableConvert {

    GenTable toEntity(GenTableCreateReq req);

    GenTable toEntity(GenTableUpdateReq req);

    GenTable toEntity(GenTableQueryReq req);

    GenTableResp toResp(GenTable entity);

    List<GenTableResp> toRespList(List<GenTable> entities);

    GenTableColumnResp toColumnResp(GenTableColumn entity);

    List<GenTableColumnResp> toColumnRespList(List<GenTableColumn> entities);
}
