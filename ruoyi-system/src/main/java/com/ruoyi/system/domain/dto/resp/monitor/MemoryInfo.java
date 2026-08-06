package com.ruoyi.system.domain.dto.resp.monitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "内存信息")
public class MemoryInfo {

    @Schema(description = "总内存（GB）")
    private double totalMemory;

    @Schema(description = "已用内存（GB）")
    private double usedMemory;

    @Schema(description = "剩余内存（GB）")
    private double freeMemory;

    @Schema(description = "内存使用率（%）")
    private double memoryUsage;
}
