package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.resp.SysOperLogResp;
import com.ruoyi.system.domain.entity.SysOperLog;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysOperLogConvert {
    SysOperLogResp toResp(SysOperLog operLog);
    List<SysOperLogResp> toRespList(List<SysOperLog> operLogs);
}
