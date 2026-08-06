# Stage 5 实现计划：代码生成器 + 服务监控

## Context

Stage 1-4 已完成并提交（commit f992126）。Stage 5 是功能补齐计划的最后阶段，包含两个高级功能模块：
- **代码生成器**：根据数据库表结构自动生成前后端代码（Velocity 模板引擎）
- **服务监控**：监控系统运行状态（CPU、内存、磁盘、JVM）

Stage 5 之前曾部分实现后被删除，现需完整重新实现。设计文档已存在于 `md/stage5/` 目录。

---

## 关键信息确认

| 项目 | 确认结果 |
|------|---------|
| Controller 目录 | web/tool/（已存在，空）、web/monitor/（已有 Stage 4 内容） |
| 菜单 ID | Stage 4 用至 1121；Stage 5 使用 113（服务监控）、114（代码生成）及其子菜单 |
| JdbcTemplate | DataSource 已配置，Spring Boot 自动配置 JdbcTemplate，可直接注入 |
| R/PageResult | `com.ruoyi.common.core.domain.R`、`PageResult`，R.ok()/R.ok(data)/R.fail() |
| Velocity | 需在 ruoyi-system/pom.xml 添加 velocity-engine-core 2.3 依赖 |

---

## 实现步骤

### 任务5-1：代码生成器

#### 步骤1：数据库表 + 实体类 + DTO
- `sql/schema.sql`：添加 sys_gen_table、sys_gen_table_column 表
- `ruoyi-system/.../domain/entity/GenTable.java`（继承 BaseEntity）
- `ruoyi-system/.../domain/entity/GenTableColumn.java`
- `ruoyi-system/.../domain/dto/req/GenTableQueryReq.java`
- `ruoyi-system/.../domain/dto/req/GenTableCreateReq.java`
- `ruoyi-system/.../domain/dto/req/GenTableUpdateReq.java`
- `ruoyi-system/.../domain/dto/req/GenSyncReq.java`
- `ruoyi-system/.../domain/dto/resp/GenTableResp.java`
- `ruoyi-system/.../domain/dto/resp/GenTableColumnResp.java`
- `ruoyi-system/.../domain/dto/resp/GenPreviewResp.java`

#### 步骤2：Mapper + Service + Convert
- `ruoyi-system/.../mapper/GenTableMapper.java`（extends BaseMapper）
- `ruoyi-system/.../mapper/GenTableColumnMapper.java`
- `ruoyi-system/src/main/resources/mapper/GenTableMapper.xml`
- `ruoyi-system/src/main/resources/mapper/GenTableColumnMapper.xml`
- `ruoyi-system/.../convert/GenTableConvert.java`（MapStruct）
- `ruoyi-system/.../service/GenTableService.java`
- `ruoyi-system/.../service/impl/GenTableServiceImpl.java`

#### 步骤3：代码模板 + GenUtils 工具类
- `ruoyi-system/pom.xml`：添加 velocity-engine-core 2.3 依赖
- `ruoyi-system/.../service/impl/GenUtils.java`（@Component，注入 JdbcTemplate）
- 11 个 Velocity 模板文件（`ruoyi-system/src/main/resources/template/`）：
  - entity.java.vm、queryReq.java.vm、createReq.java.vm、updateReq.java.vm
  - resp.java.vm、mapper.java.vm、mapper.xml.vm
  - service.java.vm、serviceImpl.java.vm、convert.java.vm、controller.java.vm

#### 步骤4：Controller + 权限配置 + 编译验证
- `ruoyi-admin/.../web/tool/GenController.java`
- `sql/data.sql`：添加菜单（ID 114）和按钮权限
- 执行 `mvn compile` 验证

### 任务5-2：服务监控

#### 步骤5：DTO + Service + Controller
- `ruoyi-system/.../domain/dto/resp/monitor/ServerInfoResp.java`
- `ruoyi-system/.../domain/dto/resp/monitor/CpuInfo.java`
- `ruoyi-system/.../domain/dto/resp/monitor/MemoryInfo.java`
- `ruoyi-system/.../domain/dto/resp/monitor/DiskInfo.java`
- `ruoyi-system/.../domain/dto/resp/monitor/JvmInfo.java`
- `ruoyi-system/.../service/ServerMonitorService.java`
- `ruoyi-system/.../service/impl/ServerMonitorServiceImpl.java`（使用 Runtime、ManagementFactory）
- `ruoyi-admin/.../web/monitor/SysMonitorController.java`

#### 步骤6：权限配置 + 编译验证
- `sql/data.sql`：添加服务监控菜单（ID 113）
- 执行 `mvn compile` 最终验证

---

## 设计要点

1. **GenUtils** 使用 JdbcTemplate 查询 INFORMATION_SCHEMA 获取表结构，使用 VelocityEngine 渲染模板
2. **服务监控** 使用 Java 的 Runtime、OperatingSystemMXBean、ManagementFactory 获取系统信息，无需额外依赖
3. **菜单 ID 分配**：
   - 113 = 服务监控（monitor 目录下，parent_id=2）
   - 114 = 代码生成（system 目录下，parent_id=1）
4. 所有 Controller 遵循现有模式：@PreAuthorize 权限控制、@OperLog 操作日志、R 统一响应

---

## 验证方式

1. `mvn compile` 编译通过
2. 检查所有文件创建完整
3. 检查 data.sql 菜单和权限配置正确
