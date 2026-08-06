package com.ruoyi.system.convert;

import com.ruoyi.system.domain.dto.resp.SysLoginLogResp;
import com.ruoyi.system.domain.entity.SysLoginLog;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructMapperConfig.class)
public interface SysLoginLogConvert {
    SysLoginLogResp toResp(SysLoginLog loginLog);
    List<SysLoginLogResp> toRespList(List<SysLoginLog> loginLogs);
}
