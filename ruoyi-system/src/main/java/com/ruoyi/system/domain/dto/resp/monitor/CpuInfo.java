package com.ruoyi.system.domain.dto.resp.monitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "CPU信息")
public class CpuInfo {

    @Schema(description = "CPU核心数")
    private int cpuCores;

    @Schema(description = "CPU使用率（%）")
    private double cpuUsage;
}
