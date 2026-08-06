package com.ruoyi.system.domain.dto.resp.monitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "JVM信息")
public class JvmInfo {

    @Schema(description = "JVM最大内存（GB）")
    private double maxMemory;

    @Schema(description = "JVM总内存（GB）")
    private double totalMemory;

    @Schema(description = "JVM已用内存（GB）")
    private double usedMemory;

    @Schema(description = "JVM剩余内存（GB）")
    private double freeMemory;

    @Schema(description = "JVM内存使用率（%）")
    private double memoryUsage;

    @Schema(description = "JVM版本")
    private String jvmVersion;

    @Schema(description = "JVM名称")
    private String jvmName;
}
