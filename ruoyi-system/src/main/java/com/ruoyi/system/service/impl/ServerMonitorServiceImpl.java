package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.dto.resp.monitor.*;
import com.ruoyi.system.service.ServerMonitorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServerMonitorServiceImpl implements ServerMonitorService {

    @Value("${spring.application.name:ruoyi}")
    private String applicationName;

    private static final double MB = 1024.0 * 1024.0;
    private static final double GB = 1024.0 * 1024.0 * 1024.0;

    @Override
    public ServerInfoResp getServerInfo() {
        ServerInfoResp resp = new ServerInfoResp();
        resp.setServerInfo(buildServerDetail());
        resp.setCpuInfo(buildCpuInfo());
        resp.setMemoryInfo(buildMemoryInfo());
        resp.setDiskInfo(buildDiskInfo());
        resp.setJvmInfo(buildJvmInfo());
        return resp;
    }

    private ServerInfoResp.ServerDetail buildServerDetail() {
        ServerInfoResp.ServerDetail detail = new ServerInfoResp.ServerDetail();
        try {
            detail.setServerName(applicationName);
            InetAddress inetAddress = InetAddress.getLocalHost();
            detail.setServerIp(inetAddress.getHostAddress());
            detail.setServerPort(8080);

            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            long startTimeMillis = runtimeMXBean.getStartTime();
            LocalDateTime startTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(startTimeMillis), ZoneId.systemDefault());
            detail.setStartTime(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            Duration duration = Duration.between(startTime, LocalDateTime.now());
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes();
            long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
            detail.setRunTime(hours + "小时" + minutes + "分钟" + seconds + "秒");
        } catch (Exception e) {
            detail.setServerName(applicationName);
            detail.setServerIp("unknown");
        }
        return detail;
    }

    private CpuInfo buildCpuInfo() {
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.setCpuCores(Runtime.getRuntime().availableProcessors());
        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osBean.getSystemCpuLoad() * 100;
        if (cpuUsage < 0) {
            cpuUsage = 0;
        }
        cpuInfo.setCpuUsage(Math.round(cpuUsage * 100.0) / 100.0);
        return cpuInfo;
    }

    private MemoryInfo buildMemoryInfo() {
        MemoryInfo memoryInfo = new MemoryInfo();
        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalPhysical = osBean.getTotalPhysicalMemorySize();
        long freePhysical = osBean.getFreePhysicalMemorySize();
        double total = totalPhysical / GB;
        double free = freePhysical / GB;
        double used = total - free;
        memoryInfo.setTotalMemory(Math.round(total * 100.0) / 100.0);
        memoryInfo.setUsedMemory(Math.round(used * 100.0) / 100.0);
        memoryInfo.setFreeMemory(Math.round(free * 100.0) / 100.0);
        memoryInfo.setMemoryUsage(Math.round((used / total * 100) * 100.0) / 100.0);
        return memoryInfo;
    }

    private List<DiskInfo> buildDiskInfo() {
        List<DiskInfo> diskInfoList = new ArrayList<>();
        File[] roots = File.listRoots();
        for (File root : roots) {
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setDiskPath(root.getAbsolutePath());
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            double total = totalSpace / GB;
            double used = usedSpace / GB;
            double free = freeSpace / GB;
            diskInfo.setTotalDisk(Math.round(total * 100.0) / 100.0);
            diskInfo.setUsedDisk(Math.round(used * 100.0) / 100.0);
            diskInfo.setFreeDisk(Math.round(free * 100.0) / 100.0);
            if (total > 0) {
                diskInfo.setDiskUsage(Math.round((used / total * 100) * 100.0) / 100.0);
            } else {
                diskInfo.setDiskUsage(0);
            }
            diskInfoList.add(diskInfo);
        }
        return diskInfoList;
    }

    private JvmInfo buildJvmInfo() {
        JvmInfo jvmInfo = new JvmInfo();
        Runtime runtime = Runtime.getRuntime();
        double maxMemory = runtime.maxMemory() / GB;
        double totalMemory = runtime.totalMemory() / GB;
        double freeMemory = runtime.freeMemory() / GB;
        double usedMemory = totalMemory - freeMemory;
        jvmInfo.setMaxMemory(Math.round(maxMemory * 100.0) / 100.0);
        jvmInfo.setTotalMemory(Math.round(totalMemory * 100.0) / 100.0);
        jvmInfo.setUsedMemory(Math.round(usedMemory * 100.0) / 100.0);
        jvmInfo.setFreeMemory(Math.round(freeMemory * 100.0) / 100.0);
        if (totalMemory > 0) {
            jvmInfo.setMemoryUsage(Math.round((usedMemory / totalMemory * 100) * 100.0) / 100.0);
        } else {
            jvmInfo.setMemoryUsage(0);
        }

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        jvmInfo.setJvmVersion(System.getProperty("java.version"));
        jvmInfo.setJvmName(System.getProperty("java.vm.name"));
        return jvmInfo;
    }
}
