package com.ruoyi.system.domain.dto.resp.monitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "服务器信息响应")
public class ServerInfoResp {

    @Schema(description = "服务器信息")
    private ServerDetail serverInfo;

    @Schema(description = "CPU信息")
    private CpuInfo cpuInfo;

    @Schema(description = "内存信息")
    private MemoryInfo memoryInfo;

    @Schema(description = "磁盘信息列表")
    private List<DiskInfo> diskInfo;

    @Schema(description = "JVM信息")
    private JvmInfo jvmInfo;

    @Data
    @Schema(description = "服务器详情")
    public static class ServerDetail {
        @Schema(description = "服务器名称")
        private String serverName;

        @Schema(description = "服务器IP")
        private String serverIp;

        @Schema(description = "服务端口")
        private int serverPort;

        @Schema(description = "启动时间")
        private String startTime;

        @Schema(description = "运行时长")
        private String runTime;
    }
}
