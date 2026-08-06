package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.resp.SysJobLogResp;
import com.ruoyi.system.domain.entity.SysJobLog;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysJobLogConvert {
    SysJobLogResp toResp(SysJobLog jobLog);
    List<SysJobLogResp> toRespList(List<SysJobLog> jobLogs);
}
