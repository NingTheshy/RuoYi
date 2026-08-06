package com.ruoyi.system.domain.dto.resp.monitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "磁盘信息")
public class DiskInfo {

    @Schema(description = "磁盘路径")
    private String diskPath;

    @Schema(description = "总磁盘空间（GB）")
    private double totalDisk;

    @Schema(description = "已用磁盘空间（GB）")
    private double usedDisk;

    @Schema(description = "剩余磁盘空间（GB）")
    private double freeDisk;

    @Schema(description = "磁盘使用率（%）")
    private double diskUsage;
}
