# RuoYi RBAC 权限管理系统 -- 开发文档

> 本文档面向初级开发者，目标是让你从零开始理解并跑通整个项目。如果你遇到任何问题，请先查阅 [13. 常见问题与排查](#13-常见问题与排查)。

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 前置知识](#2-前置知识)
- [3. 技术选型](#3-技术选型)
- [4. 环境准备](#4-环境准备)
- [5. 项目结构](#5-项目结构)
- [6. 数据库设计](#6-数据库设计)
- [7. 快速启动](#7-快速启动)
- [8. 核心架构](#8-核心架构)
- [9. 认证与授权流程](#9-认证与授权流程)
- [10. 数据权限（数据范围过滤）](#10-数据权限数据范围过滤)
- [11. API 接口文档](#11-api-接口文档)
- [12. 关键配置说明](#12-关键配置说明)
- [13. 常见问题与排查](#13-常见问题与排查)

---

## 1. 项目概述

### 1.1 这个项目是什么？

这是一个**后台管理系统的权限模块**。就像公司里的门禁系统一样：

- **用户（User）** = 公司员工，每个人有自己的工牌（账号密码）
- **角色（Role）** = 工牌类型，比如"管理员"、"普通员工"
- **菜单（Menu）** = 公司里的各个房间，比如"财务室"、"会议室"
- **权限（Permission）** = 工牌能刷开哪些房间的门

**RBAC**（Role-Based Access Control，基于角色的访问控制）的核心思想是：**不直接给用户分配权限，而是通过角色间接分配**。这样管理起来更方便——只需要管理角色的权限，然后给用户分配角色就行了。

### 1.2 这个项目能做什么？

假设你是一家公司的 IT 管理员，这个系统可以让你：

1. **管理用户**：创建账号、禁用账号、重置密码
2. **管理角色**：创建"财务"、"人事"等角色，给每个角色分配不同的菜单权限
3. **管理菜单**：设置系统有哪些页面和按钮，谁能看到
4. **管理部门**：设置公司的组织架构（总公司→分公司→部门）
5. **数据隔离**：让不同部门的用户只能看到自己部门的数据

### 1.3 项目核心功能

| 功能 | 说明 | 类比 |
|------|------|------|
| 用户管理 | 增删改查用户、密码重置、状态切换 | 管理员工花名册 |
| 角色管理 | 增删改查角色、分配菜单权限 | 管理工牌类型 |
| 菜单管理 | 树形结构，目录/菜单/按钮三级 | 管理公司房间和门禁 |
| 部门管理 | 树形结构，维护上下级关系 | 管理组织架构 |
| 登录认证 | JWT Token + Redis，支持 24 小时免登录 | 刷工牌进门 |
| 权限校验 | 方法级别的精细控制（`@PreAuthorize`） | 每个房间单独的门禁 |
| 数据权限 | 行级数据过滤，不同角色看不同数据 | 每个房间里的文件柜也上锁 |

---

## 2. 前置知识

在开始之前，你需要了解以下概念（如果已经熟悉可以跳过）。

### 2.1 Java 基础

- **JDK**（Java Development Kit）：Java 开发工具包，编译和运行 Java 程序必需。本项目要求 JDK 17。
- **Maven**：Java 项目的构建工具，类似 npm（前端）或 pip（Python）。它负责下载依赖、编译代码、打包项目。
- **IDEA**（IntelliJ IDEA）：Java 开发最常用的编辑器，推荐使用社区版（免费）。

### 2.2 后端框架

| 概念 | 一句话解释 | 类比 |
|------|-----------|------|
| **Spring Boot** | Java Web 应用的快速开发框架，自动配置大部分东西 | 脚手架，帮你搭好房子的框架 |
| **MyBatis-Plus** | 数据库操作框架，让你用 Java 代码操作数据库，不用写原生 SQL | 翻译官，把 Java 语言翻译成数据库语言 |
| **Spring Security** | 认证和授权框架，处理登录、权限校验 | 门卫，检查你有没有权限进入 |
| **JWT** | 一种 Token 格式，用户登录后服务器发一个"通行证" | 你的工牌，上面写着你的身份信息 |
| **Redis** | 内存数据库，读写速度极快 | 临时存储柜，放一些需要快速读取的东西 |

### 2.3 数据库

- **MySQL**：最常用的关系型数据库，存储用户、角色、菜单等数据。本项目使用 MySQL 8.0+。

### 2.4 什么是 RBAC？

RBAC 的核心是 5 张表的关系：

```
用户（sys_user）
    │
    ├── M:N ──→ 角色（sys_role）    通过 sys_user_role 关联
    │               │
    │               └── M:N ──→ 菜单（sys_menu）  通过 sys_role_menu 关联
    │
    └── N:1 ──→ 部门（sys_dept）    通过 dept_id 字段关联
```

- 一个用户可以有**多个角色**（比如既是"项目经理"又是"开发人员"）
- 一个角色可以有**多个菜单权限**（比如"项目经理"可以看"项目管理"和"人员管理"）
- 一个菜单可以被**多个角色**共享

## 3. 技术选型

| 技术 | 版本 | 一句话说明 | 为什么选它 |
|------|------|-----------|-----------|
| JDK | 17 | Java 运行环境 | Spring Boot 3 要求 JDK 17+ |
| Spring Boot | 3.2.5 | Web 应用框架 | 自动配置，开箱即用 |
| MyBatis-Plus | 3.5.5 | 数据库操作框架 | 简化 CRUD，不用写重复 SQL |
| Spring Security | 6.x | 认证授权框架 | Spring 生态标配，功能强大 |
| jjwt | 0.12.5 | JWT 工具库 | 生成和校验登录 Token |
| Redis | 6.0+ | 内存数据库 | 存储登录 Token，支持快速校验 |
| MySQL | 8.0+ | 关系型数据库 | 存储业务数据 |
| Knife4j | 4.5.0 | API 文档工具 | 自动生成接口文档，方便调试 |
| Lombok | -- | 代码简化 | 自动生成 getter/setter 等样板代码 |
| Maven | 3.8+ | 构建工具 | 管理依赖和构建流程 |

---

## 4. 环境准备

在跑通项目之前，你需要先安装好以下工具。如果你已经安装过，可以跳过对应的小节。

### 4.1 安装 JDK 17

**为什么需要 JDK 17？** 本项目基于 Spring Boot 3.x，它要求 JDK 17 或更高版本。如果你用 JDK 8 或 11，项目根本无法编译。

**安装步骤：**

1. 下载 JDK 17（推荐 [Adoptium](https://adoptium.net/)，选择 LTS 版本）
2. 安装时记住安装路径（如 `C:\Program Files\Eclipse Adoptium\jdk-17`）
3. 配置环境变量：
   - 新建系统变量 `JAVA_HOME`，值为 JDK 安装路径
   - 在 `Path` 中添加 `%JAVA_HOME%\bin`
4. 验证安装：

```bash
java -version
# 应输出类似：openjdk version "17.0.x" ...
```

> **常见坑**：如果 `java -version` 显示的不是 17，说明系统中可能有多个 JDK 版本。检查 `JAVA_HOME` 是否指向正确路径，或者在 IDEA 中确认 Project SDK 设置。

### 4.2 安装 Maven

**为什么需要 Maven？** Maven 负责下载项目依赖、编译代码、打包 JAR。没有它，你无法构建项目。

**安装步骤：**

1. 下载 [Maven 3.8+](https://maven.apache.org/download.cgi)
2. 解压到一个目录（如 `D:\apache-maven-3.9.6`）
3. 配置环境变量：
   - 新建系统变量 `MAVEN_HOME`，值为 Maven 解压路径
   - 在 `Path` 中添加 `%MAVEN_HOME%\bin`
4. 验证安装：

```bash
mvn -version
# 应输出 Apache Maven 版本号
```

> **常见坑**：如果提示 `mvn` 不是内部命令，检查 `MAVEN_HOME` 和 `Path` 配置。Windows 用户注意路径不要有中文或空格。

### 4.3 安装 MySQL 8.0+

**为什么需要 MySQL？** 本项目的所有业务数据（用户、角色、菜单、部门）都存储在 MySQL 中。

**安装步骤：**

1. 下载 [MySQL 8.0+](https://dev.mysql.com/downloads/mysql/)（推荐 MSI 安装包，简化配置）
2. 安装时设置 root 密码（建议设为 `root`，方便开发）
3. 确认 MySQL 服务已启动：

```bash
mysql -u root -p
# 输入密码后进入 MySQL 命令行即表示成功
```

> **常见坑**：如果 MySQL 无法启动，检查端口 3306 是否被占用（`netstat -ano | findstr :3306`）。

### 4.4 安装 Redis

**为什么需要 Redis？** 本项目用 Redis 存储用户的登录 Token。用户登录后，服务器会把 Token 放进 Redis，用于支持主动登出（Token 失效）和 Token 唯一性。注意：每次请求时只校验 JWT 签名和有效期，**不会**去 Redis 查询。

**安装步骤（Windows）：**

1. 下载 [Redis for Windows](https://github.com/microsoftarchive/redis/releases)（微软官方维护的版本）
2. 解压并运行 `redis-server.exe`
3. 验证连接：

```bash
redis-cli ping
# 应返回 PONG
```

**安装步骤（Linux/Mac）：**

```bash
# Ubuntu/Debian
sudo apt install redis-server
sudo systemctl start redis

# Mac (Homebrew)
brew install redis
brew services start redis
```

> **常见坑**：如果 `redis-cli ping` 没有返回 PONG，说明 Redis 服务未启动。Windows 用户需要先运行 `redis-server.exe`。

### 4.5 初始化数据库

数据库安装好后，你需要创建项目所需的数据库并导入表结构和初始数据。

**步骤 1：登录 MySQL**

```bash
mysql -u root -p
# 输入你的 MySQL 密码
```

**步骤 2：创建数据库**

```sql
CREATE DATABASE `ry` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ry`;
```

**步骤 3：导入表结构**

```bash
# 在 MySQL 命令行中执行（注意路径替换为你自己的项目路径）
source D:/java/project/RuoYi/ruoyi/sql/schema.sql;
```

这条命令会创建 7 张表：`sys_user`、`sys_role`、`sys_menu`、`sys_dept`、`sys_user_role`、`sys_role_menu`、`sys_role_dept`。

**步骤 4：导入初始数据**

```bash
source D:/java/project/RuoYi/ruoyi/sql/data.sql;
```

这条命令会导入：5 个部门、2 个用户（admin/ryou）、2 个角色、菜单权限等初始数据。

> **验证**：执行 `SHOW TABLES;` 应该看到 7 张表；执行 `SELECT * FROM sys_user;` 应该看到 2 条用户记录。

### 4.6 启动 Redis 服务

确保 Redis 在后台运行。项目默认连接 `localhost:6379`，密码为 `root`。

```bash
# 检查 Redis 是否运行
redis-cli ping
# 返回 PONG 表示正常
```

如果你的 Redis 没有设置密码，需要修改 `application.yml` 中的 `spring.data.redis.password` 为空字符串 `""`。

### 4.7 修改配置文件

编辑 `ruoyi-admin/src/main/resources/application.yml`，根据你的实际环境修改以下配置：

```yaml
spring:
  datasource:
    # 数据库连接地址中的 ry 要与你创建的数据库名一致
    url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&rewriteBatchedStatements=true
    username: root        # ← 改成你的 MySQL 用户名
    password: root        # ← 改成你的 MySQL 密码
  data:
    redis:
      host: localhost
      port: 6379
      password: root      # ← 改成你的 Redis 密码，无密码则填 ""

jwt:
  secret: YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough123456  # ← 开发环境可先不改
```

**必须检查的配置项：**

| 配置项 | 位置 | 说明 |
|--------|------|------|
| `spring.datasource.url` | 数据库连接 | 确认数据库名 `ry` 与你创建的一致 |
| `spring.datasource.username` | 数据库用户名 | 通常是 `root` |
| `spring.datasource.password` | 数据库密码 | 你安装 MySQL 时设置的密码 |
| `spring.data.redis.password` | Redis 密码 | 你的 Redis 密码，无密码填 `""` |

---

## 5. 项目结构

本项目采用 Maven 多模块架构，就像一栋大楼被分成不同楼层，每层有不同职责。

### 5.1 模块划分

```
ruoyi/                              # 父 POM（聚合模块）
├── ruoyi-common/                     # 公共模块：安全、工具、配置
├── ruoyi-system/                     # 系统模块：业务代码
├── ruoyi-admin/                      # 启动模块：配置、入口
└── sql/                              # 数据库脚本
```

**三个模块的职责（类比）：**

| 模块 | 职责 | 类比 |
|------|------|------|
| `ruoyi-common` | 安全框架、工具类、统一响应、异常处理 | 大楼的基础设施（电梯、消防系统） |
| `ruoyi-system` | 具体业务逻辑（用户、角色、菜单、部门管理） | 大楼的各个房间（办公室、会议室） |
| `ruoyi-admin` | 启动入口、配置文件、拦截器注册 | 大楼的门卫和总控室 |

### 5.2 模块依赖关系

```
ruoyi-admin  ──→  ruoyi-system  ──→  ruoyi-common
```

**禁止反向依赖**：`ruoyi-common` 不能依赖 `ruoyi-system`（会形成循环依赖）。因此 `PermissionService` 和 `DataScopeService` 使用 `JdbcTemplate` 直接查询数据库。

### 5.3 详细包结构

```
com.ruoyi
├── admin                              # ruoyi-admin 模块
│   ├── RuoYiAdminApplication.java     # 启动类
│   └── config/
│       └── MybatisPlusConfig.java     # MyBatis-Plus 拦截器配置
│
├── common                             # ruoyi-common 模块
│   ├── core
│   │   ├── annotation/
│   │   │   └── DataScope.java         # 数据权限注解
│   │   ├── aspect/
│   │   │   └── DataScopeAspect.java   # 数据权限 AOP 切面
│   │   ├── constant/
│   │   │   └── Constants.java         # 全局常量
│   │   ├── domain/
│   │   │   ├── R.java                 # 统一响应封装
│   │   │   ├── PageResult.java        # 分页响应封装
│   │   │   ├── BaseEntity.java        # 实体基类（审计字段）
│   │   │   └── DataScopeParams.java   # ThreadLocal 载体
│   │   ├── exception/
│   │   │   ├── ServiceException.java  # 业务异常
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   └── handler/
│   │       ├── MyMetaObjectHandler.java    # 自动填充（审计字段）
│   │       └── DataScopeInterceptor.java   # SQL 拦截器（数据权限）
│   ├── redis
│   │   └── config/
│   │       └── RedisConfig.java       # Redis 序列化配置
│   └── security
│       ├── config/
│       │   └── SecurityConfig.java    # Spring Security 配置
│       ├── filter/
│       │   └── JwtAuthenticationFilter.java  # JWT 认证过滤器
│       ├── service/
│       │   ├── TokenService.java      # Token 管理（Redis）
│       │   ├── LoginService.java      # 登录/登出服务
│       │   ├── PermissionService.java # 权限查询（JdbcTemplate）
│       │   └── DataScopeService.java  # 数据权限条件构建
│       └── utils/
│           └── JwtTokenProvider.java  # JWT 工具类
│
└── system                             # ruoyi-system 模块
    ├── controller
    │   ├── AuthController.java        # 认证接口
    │   ├── SysUserController.java     # 用户管理
    │   ├── SysRoleController.java     # 角色管理
    │   ├── SysMenuController.java     # 菜单管理
    │   └── SysDeptController.java     # 部门管理
    ├── domain
    │   ├── entity
    │   │   ├── SysUser.java           # 用户实体
    │   │   ├── SysRole.java           # 角色实体
    │   │   ├── SysMenu.java           # 菜单实体
    │   │   └── SysDept.java           # 部门实体
    │   └── vo
    │       ├── LoginBody.java         # 登录请求体
    │       ├── LoginUser.java         # 登录响应体
    │       ├── RegisterBody.java      # 注册请求体
    │       └── MenuTree.java          # 菜单树节点
    ├── mapper
    │   ├── SysUserMapper.java         # + XML
    │   ├── SysRoleMapper.java         # + XML
    │   ├── SysMenuMapper.java         # + XML
    │   └── SysDeptMapper.java         # + XML
    └── service
        ├── ISysUserService.java
        ├── ISysRoleService.java
        ├── ISysMenuService.java
        ├── ISysDeptService.java
        └── impl
            ├── SysUserServiceImpl.java
            ├── SysRoleServiceImpl.java
            ├── SysMenuServiceImpl.java
            └── SysDeptServiceImpl.java
```

---

## 6. 数据库设计

本项目使用 7 张表来实现 RBAC 权限模型。简单来说，就是通过"用户-角色-菜单"的关联关系，控制谁能访问什么。

### 6.1 ER 图

```
sys_user ──M:N──> sys_role ──M:N──> sys_menu
    │                  │
    │                  └──M:N──> sys_dept
    │
    └──> sys_dept (dept_id 关联)
```

### 6.2 表结构

#### sys_user（用户表）

| 列名 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT PK AUTO_INCREMENT | 用户 ID |
| dept_id | BIGINT | 部门 ID（关联 sys_dept） |
| user_name | VARCHAR(30) NOT NULL | 用户账号 |
| nick_name | VARCHAR(30) NOT NULL | 用户昵称 |
| email | VARCHAR(50) | 邮箱 |
| phonenumber | VARCHAR(11) | 手机号 |
| sex | CHAR(1) | 性别（0=男 1=女 2=未知） |
| avatar | VARCHAR(100) | 头像地址 |
| password | VARCHAR(100) NOT NULL | 密码（BCrypt 加密） |
| status | CHAR(1) | 状态（0=正常 1=停用） |
| del_flag | CHAR(1) DEFAULT '0' | 删除标志（0=存在 2=已删除） |
| login_ip | VARCHAR(128) | 最后登录 IP |
| login_date | DATETIME | 最后登录时间 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

#### sys_role（角色表）

| 列名 | 类型 | 说明 |
|------|------|------|
| role_id | BIGINT PK AUTO_INCREMENT | 角色 ID |
| role_name | VARCHAR(30) NOT NULL | 角色名称 |
| role_key | VARCHAR(100) NOT NULL | 角色标识（如 admin、common） |
| role_sort | INT NOT NULL | 显示顺序 |
| data_scope | CHAR(1) DEFAULT '1' | 数据范围（1=全部 2=自定义 3=本部门 4=本部门及以下 5=仅本人） |
| menu_check_strictly | TINYINT(1) DEFAULT 1 | 菜单树关联时是否父子不关联 |
| dept_check_strictly | TINYINT(1) DEFAULT 1 | 部门树关联时是否父子不关联 |
| status | CHAR(1) NOT NULL | 状态（0=正常 1=停用） |
| del_flag | CHAR(1) DEFAULT '0' | 删除标志 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

#### sys_menu（菜单权限表）

| 列名 | 类型 | 说明 |
|------|------|------|
| menu_id | BIGINT PK AUTO_INCREMENT | 菜单 ID |
| menu_name | VARCHAR(50) NOT NULL | 菜单名称 |
| parent_id | BIGINT DEFAULT 0 | 父菜单 ID（0=根节点） |
| order_num | INT DEFAULT 0 | 显示顺序 |
| path | VARCHAR(200) | 路由地址 |
| component | VARCHAR(255) | 组件路径 |
| query | VARCHAR(255) | 路由参数 |
| is_frame | INT DEFAULT 1 | 是否外链（0=是 1=否） |
| is_cache | INT DEFAULT 0 | 是否缓存（0=缓存 1=不缓存） |
| menu_type | CHAR(1) | 菜单类型（M=目录 C=菜单 F=按钮） |
| visible | CHAR(1) DEFAULT '0' | 是否显示（0=显示 1=隐藏） |
| status | CHAR(1) DEFAULT '0' | 状态（0=正常 1=停用） |
| perms | VARCHAR(100) | 权限标识（如 system:user:list） |
| icon | VARCHAR(100) DEFAULT '#' | 菜单图标 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

#### sys_dept（部门表）

| 列名 | 类型 | 说明 |
|------|------|------|
| dept_id | BIGINT PK AUTO_INCREMENT | 部门 ID |
| parent_id | BIGINT DEFAULT 0 | 父部门 ID |
| ancestors | VARCHAR(500) | 祖级列表（如 0,100,101） |
| dept_name | VARCHAR(30) NOT NULL | 部门名称 |
| order_num | INT DEFAULT 0 | 显示顺序 |
| leader | VARCHAR(20) | 负责人 |
| phone | VARCHAR(11) | 联系电话 |
| email | VARCHAR(50) | 邮箱 |
| status | CHAR(1) DEFAULT '0' | 状态（0=正常 1=停用） |
| del_flag | CHAR(1) DEFAULT '0' | 删除标志 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

#### sys_user_role（用户角色关联表）

| 列名 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT NOT NULL | 用户 ID |
| role_id | BIGINT NOT NULL | 角色 ID |

**联合主键**：`(user_id, role_id)`

#### sys_role_menu（角色菜单关联表）

| 列名 | 类型 | 说明 |
|------|------|------|
| role_id | BIGINT NOT NULL | 角色 ID |
| menu_id | BIGINT NOT NULL | 菜单 ID |

**联合主键**：`(role_id, menu_id)`

#### sys_role_dept（角色部门关联表）

| 列名 | 类型 | 说明 |
|------|------|------|
| role_id | BIGINT NOT NULL | 角色 ID |
| dept_id | BIGINT NOT NULL | 部门 ID |

**联合主键**：`(role_id, dept_id)`，用于 `data_scope=2`（自定义数据范围）时指定角色可访问的部门。

### 6.3 初始数据

| 表 | 数据 |
|----|------|
| sys_dept | 5 个部门（若依科技 100、深圳总公司 101、长沙分公司 102、研发部门 103、市场部门 104） |
| sys_user | 2 个用户（admin/ryou，默认密码均为 `admin123`） |
| sys_role | 2 个角色（超级管理员 admin/普通角色 common） |
| sys_user_role | admin→超级管理员，ryou→普通角色 |
| sys_menu | 1 个目录 + 4 个菜单 + 17 个按钮权限（用户管理 5 个 + 角色/菜单/部门管理各 4 个） |
| sys_role_menu | admin 角色拥有所有菜单权限，common 角色仅拥有 list 和 query 权限 |

---

## 7. 快速启动

环境准备好后，按照以下步骤启动项目。整个过程大约需要 5-10 分钟。

### 7.1 构建项目

```bash
cd D:/java/project/RuoYi/ruoyi
mvn clean package -DskipTests
```

**常见构建错误**：如果 `mvn` 提示找不到命令，需确认已配置 Maven 环境变量，或使用 IDE 内置 Maven。

构建成功输出：`BUILD SUCCESS`

### 7.2 启动顺序

1. 启动 MySQL，确保 `ry` 数据库已创建并导入数据
2. 启动 Redis，确保 6379 端口可用
3. 启动应用（选择其一）：

#### 方式 A：命令行启动

```bash
java -jar ruoyi-admin/target/ruoyi-admin-1.0.0.jar
```

#### 方式 B：IDEA 启动（推荐初学者使用）

**第 1 步：导入项目**

1. 打开 IntelliJ IDEA
2. 选择 `File → Open`
3. 找到项目根目录 `D:\java\project\RuoYi\ruoyi`，选择 `pom.xml` 文件
4. 弹出窗口选择 `Open as Project`
5. 等待右下角 Maven 同步完成（进度条消失）

> **如果 Maven 同步失败**：点击右侧 `Maven` 面板 → 点击刷新按钮（🔄），或选择 `File → Invalidate Caches → Invalidate and Restart`

**第 2 步：配置 JDK**

1. `File → Project Structure → Project`
2. `Project SDK` 选择 JDK 17（如果没有，点击 `Add SDK → Download JDK`，选择 version 17）
3. `Project language level` 选择 `17`

**第 3 步：运行项目**

1. 找到 `ruoyi-admin/src/main/java/com/ruoyi/admin/RuoYiAdminApplication.java`
2. 右键点击文件 → `Run 'RuoYiAdminApplication'`
3. 等待控制台输出启动成功信息

> **常见问题**：如果提示 `Cannot resolve symbol`，右键项目根目录 → `Maven → Reload Project`

### 7.3 验证启动

启动成功后控制台输出：
```
(♥◠‿◠)ﾉﾞ  若依RBAC权限系统启动成功   ﾞ(♥◠‿◠)ﾉﾞ
```

访问 API 文档：`http://localhost:8080/doc.html`（Knife4j）

### 7.4 测试登录

```bash
# 登录获取 Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应示例：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "userName": "admin",
    "nickName": "若依管理员",
    "token": "eyJhbGciOiJIUz...",
    "loginTime": 1718000000000,
    "expireTime": 1718086400000,
    "ip": "127.0.0.1",
    "address": "127.0.0.1",
    "permissions": ["*:*:*"],
    "roles": ["admin"]
  }
}
```

使用返回的 Token 访问受保护接口：
```bash
curl http://localhost:8080/api/auth/info \
  -H "Authorization: Bearer eyJhbGciOiJIUz..."
```

---

## 8. 核心架构

这一节介绍项目的核心工作原理。如果你是初学者，可以先跳过，等跑通项目后再回来看。

### 8.1 请求处理流程

```
客户端请求
    │
    ▼
┌──────────────────────────────────┐
│  Spring Security Filter Chain    │
│  ┌────────────────────────────┐  │
│  │ CorsFilter                 │  │  ← CORS 预检处理
│  ├────────────────────────────┤  │
│  │ JwtAuthenticationFilter    │  │  ← 解析 JWT，加载权限到 SecurityContext
│  ├────────────────────────────┤  │
│  │ UsernamePasswordAuth...    │  │  ← Spring Security 内置
│  ├────────────────────────────┤  │
│  │ AuthorizationFilter        │  │  ← @PreAuthorize 校验
│  └────────────────────────────┘  │
└──────────────────────────────────┘
    │
    ▼
┌──────────────────────────────────┐
│  Controller                      │
│  └── @PreAuthorize 校验          │
└──────────────────────────────────┘
    │
    ▼
┌──────────────────────────────────┐
│  Service Layer                   │
│  └── @DataScope 切面拦截         │  ← AOP 写入数据权限条件到 ThreadLocal
└──────────────────────────────────┘
    │
    ▼
┌──────────────────────────────────┐
│  MyBatis-Plus InnerInterceptor   │
│  ┌────────────────────────────┐  │
│  │ DataScopeInterceptor       │  │  ← 读取 ThreadLocal，修改 SQL
│  ├────────────────────────────┤  │
│  │ PaginationInnerInterceptor │  │  ← 分页处理
│  └────────────────────────────┘  │
└──────────────────────────────────┘
    │
    ▼
  执行 SQL → 返回结果
```

### 8.1.1 启动类关键注解

`RuoYiAdminApplication` 上有两个重要注解：

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})  // 排除自动数据源配置
@ComponentScan(basePackages = {"com.ruoyi"})                           // 扫描所有子模块
```

- `exclude = DataSourceAutoConfiguration.class`：手动配置数据源，避免自动配置冲突
- `@ComponentScan(basePackages = {"com.ruoyi"})`：扫描 `com.ruoyi` 下所有包，确保 `ruoyi-common` 和 `ruoyi-system` 的 Bean 都能被发现

`MybatisPlusConfig` 中包含 `@MapperScan("com.ruoyi.system.mapper")`，指定 MyBatis-Plus 扫描 Mapper 接口的包路径。

### 8.2 统一响应格式

所有接口返回 `R<T>` 对象：

```json
{
  "code": 200,       // 200=成功，500=失败
  "msg": "操作成功",
  "data": { ... }    // 业务数据（可选）
}
```

分页接口额外使用 `PageResult<T>`：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "rows": [ ... ],     // 数据列表
    "total": 50           // 总记录数
  }
}
```

### 8.3 软删除机制

四个核心实体（`SysUser`、`SysRole`、`SysMenu`、`SysDept`）均使用 MyBatis-Plus `@TableLogic` 实现软删除：

- `del_flag = '0'`：正常数据（默认查询条件自动过滤）
- `del_flag = '2'`：已删除数据

使用 MyBatis-Plus 的 `deleteById()` / `removeById()` 方法时，会自动执行 `UPDATE ... SET del_flag='2'` 而非 `DELETE`。

### 8.4 审计字段自动填充

`MyMetaObjectHandler` 在数据插入和更新时自动填充：

| 字段 | INSERT 时 | UPDATE 时 | 值来源 |
|------|-----------|-----------|--------|
| createBy | 填充 | -- | `SecurityContextHolder` 中的当前用户 ID |
| createTime | 填充 | -- | `new Date()`，JSON 格式 `yyyy-MM-dd HH:mm:ss` |
| updateBy | 填充 | 填充 | `SecurityContextHolder` 中的当前用户 ID |
| updateTime | 填充 | 填充 | `new Date()`，JSON 格式 `yyyy-MM-dd HH:mm:ss` |

> 当 SecurityContext 中无用户信息时（如定时任务），回退为 `"system"`。
>
> **注意**：`getCurrentUser()` 实际获取的是 `authentication.getPrincipal().toString()`，在 JWT 过滤器中 principal 被设置为 userId 字符串（如 `"1"`），因此 `createBy`/`updateBy` 存储的是用户 ID 而非用户名。

---

## 9. 认证与授权流程

这一节详细解释用户登录后发生了什么，以及每次请求是如何被验证的。

### 9.1 登录认证

```
POST /api/auth/login  {"username":"admin","password":"admin123"}
    │
    ▼
AuthController.login()
    │
    ├── SysUserMapper.selectUserByUserName("admin")  → 查找用户
    │
    ├── 用户不存在？ → 返回 R.fail(500, "用户不存在")
    │
    ├── LoginService.login(username, password, userId, realPassword, status)
    │   ├── 用户状态是否正常（status='0'）？否则抛出 "用户已被停用"
    │   └── 密码是否匹配（BCrypt）？否则抛出 "密码错误"
    │
    ├── TokenService.createToken(userId)
    │   ├── JwtTokenProvider.createToken({"userId": 1})  → 生成 JWT
    │   └── Redis SET login_tokens:1  "eyJ..."  EX 86400  → 存储 Token
    │
    └── 返回 LoginUser（含 userId, userName, nickName, token, loginTime, expireTime, ip, address, permissions, roles）
```

### 9.2 请求认证（每次请求）

```
请求到达 → JwtAuthenticationFilter.doFilterInternal()
    │
    ├── 从 Header 提取 Bearer Token
    │   └── Authorization: Bearer eyJhbGci...
    │
    ├── TokenService.validateToken(token)
    │   ├── JwtTokenProvider.validateToken(token)
    │   │   ├── 解析 JWT Claims
    │   │   └── 检查是否过期
    │   └── 注意：过滤器中仅校验 JWT 签名和有效期，不检查 Redis
    │
    ├── PermissionService.getPermsByUserId(userId)     → JDBC 查询权限
    │   └── SELECT DISTINCT m.perms FROM sys_menu m
    │       INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id
    │       INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
    │       WHERE ur.user_id = ?
    │         AND m.perms IS NOT NULL AND m.perms != '' AND m.status = '0'
    │       ⚠ 注意：未过滤 m.del_flag，已软删除的菜单权限仍可能被加载
    │
    ├── PermissionService.getRoleKeysByUserId(userId)  → JDBC 查询角色标识
    │   └── SELECT DISTINCT r.role_key FROM sys_role r
    │       INNER JOIN sys_user_role ur ON r.role_id = ur.role_id
    │       WHERE ur.user_id = ? AND r.status = '0'
    │       ⚠ 注意：未过滤 r.del_flag，已软删除的角色标识仍可能被加载
    │
    ├── 构建 authorities：
    │   ├── 权限标识 → SimpleGrantedAuthority("system:user:list")
    │   └── 角色标识 → SimpleGrantedAuthority("ROLE_admin")  （加 ROLE_ 前缀）
    │
    └── SecurityContextHolder.setAuthentication(authentication)
```

### 9.3 方法级权限校验

Controller 方法通过 `@PreAuthorize` 注解声明所需权限：

```java
@PreAuthorize("hasAuthority('system:user:list')")
@GetMapping("/list")
public R<PageResult<SysUser>> list(PageQuery pageQuery, SysUser query) { ... }
```

Spring Security 的 `MethodSecurityInterceptor` 检查当前 `Authentication` 中的 `authorities` 是否包含指定权限。

**权限命名规范**：`{模块}:{实体}:{操作}`

| 权限标识 | 说明 |
|----------|------|
| system:user:list | 用户管理-列表查询 |
| system:user:query | 用户管理-详情查询 |
| system:user:add | 用户管理-新增 |
| system:user:edit | 用户管理-编辑 |
| system:user:remove | 用户管理-删除 |
| system:user:resetPwd | 用户管理-重置密码 |
| system:role:list | 角色管理-列表查询 |
| system:role:add | 角色管理-新增 |
| system:role:edit | 角色管理-编辑 |
| system:role:remove | 角色管理-删除 |
| system:menu:list | 菜单管理-列表查询 |
| system:menu:add | 菜单管理-新增 |
| system:menu:edit | 菜单管理-编辑 |
| system:menu:remove | 菜单管理-删除 |
| system:dept:list | 部门管理-列表查询 |
| system:dept:add | 部门管理-新增 |
| system:dept:edit | 部门管理-编辑 |
| system:dept:remove | 部门管理-删除 |
| \*:\*:\* | 超级管理员权限（admin 角色） |

### 9.4 用户注册

```
POST /api/auth/register  {"username":"newuser","password":"pass","nickname":"昵称"}
    │
    ▼
AuthController.register()
    │
    ├── 校验：用户名、密码、昵称非空
    │
    ├── SysUserServiceImpl.insertUser(user)
    │   ├── 检查用户名是否已存在 → 已存在则抛出 ServiceException
    │   ├── BCrypt 加密密码
    │   └── 保存用户（status='0', del_flag='0'）
    │
    ├── SysUserMapper.insertUserRole(userId, 2L)  → 分配默认角色（普通角色）
    │
    └── 返回 R.ok()
```

### 9.5 获取当前用户信息

```
GET /api/auth/info
    │
    ├── 从 SecurityContextHolder 获取当前 userId
    ├── 查询用户信息、角色列表、权限列表
    └── 返回 Map<String, Object>：
        ├── "user": SysUser 对象（不含密码）
        ├── "roles": ["admin"]（角色标识列表）
        └── "permissions": ["*:*:*"]（权限标识列表）
```

### 9.6 登出

```
POST /api/auth/logout
    │
    ├── 从 Header 提取 Token
    ├── 解析 userId
    └── Redis DEL login_tokens:{userId}  → 删除 Token
```

---

## 10. 数据权限（数据范围过滤）

数据权限是本项目的一个高级功能。简单来说，它能自动在 SQL 查询中追加条件，让不同用户只能看到自己有权看的数据。

**举个例子**：假设公司有"深圳总公司"和"长沙分公司"两个部门，如果一个员工的数据权限设置为"本部门"，那他查询用户列表时，只能看到深圳总公司的员工，看不到长沙分公司的。

### 10.1 数据权限类型

| 值 | 名称 | 说明 | SQL 条件 |
|----|------|------|----------|
| 1 | 全部数据 | 不做过滤 | `null`（不追加任何条件） |
| 2 | 自定义数据 | 仅指定部门的数据 | `dept_id IN (SELECT rd.dept_id FROM sys_role_dept rd WHERE rd.role_id IN (SELECT ur2.role_id FROM sys_user_role ur2 INNER JOIN sys_role r2 ON ur2.role_id = r2.role_id WHERE ur2.user_id = {userId} AND r2.data_scope = '2' AND r2.status = '0' AND r2.del_flag = '0'))` |
| 3 | 本部门数据 | 仅本部门的数据 | `dept_id = {userDeptId}` |
| 4 | 本部门及以下 | 本部门及所有子部门 | `dept_id IN (SELECT dept_id FROM sys_dept WHERE FIND_IN_SET({userDeptId}, ancestors))` |
| 5 | 仅本人数据 | 仅自己的记录 | `user_id = {userId}` |

### 10.2 实现架构

```
@DataScope 注解（标记需要过滤的方法）
    │
    ▼
DataScopeAspect（AOP 切面）
    ├── 调用 DataScopeService.buildDataScopeCondition()
    │   ├── 查询当前用户的所有角色 data_scope 值
    │   ├── 如果任一角色 scope='1'，返回 null（不过滤）
    │   ├── 按各角色的 scope 类型构建 SQL 条件
    │   ├── 多角色条件用 OR 合并
    │   └── 出错返回 AND 1=0（fail closed，不泄露数据）
    │
    ├── 将条件写入 DataScopeParams（ThreadLocal）
    └── 执行目标方法 → finally 恢复/清理 ThreadLocal
    │
    ▼
DataScopeInterceptor（MyBatis-Plus InnerInterceptor）
    ├── 读取 DataScopeParams.getCondition()
    ├── 检查 SQL 是否有 WHERE 子句
    │   ├── 有 → 在 WHERE 后追加条件
    │   └── 无 → 插入 WHERE 1=1 后追加条件
    └── 通过反射修改 BoundSql.sql 字段
```

### 10.3 使用方式

在 Service 方法上添加 `@DataScope` 注解即可自动生效：

```java
@DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
public List<SysUser> selectUserList(SysUser user) {
    return baseMapper.selectUserList(user);
}
```

当前已启用数据权限的方法：
- `SysUserServiceImpl.selectUserList()` — 用户列表查询，注解参数：`@DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")`
- `SysUserServiceImpl.selectUserPage()` — 用户分页查询，注解参数同上
- `SysDeptServiceImpl.selectDeptList()` — 部门列表查询，注解参数：`@DataScope(alias = "", userIdColumn = "dept_id", deptIdColumn = "dept_id")`

> **注意**：`SysDeptServiceImpl` 的 `userIdColumn` 设置为 `"dept_id"`，这意味着 scope=5（仅本人数据）会生成 `dept_id = {userId}` 条件。实际场景中很少有部门表使用 scope=5，此配置主要为预留扩展。

### 10.4 注意事项

1. **拦截器顺序**：`DataScopeInterceptor` 必须在 `PaginationInnerInterceptor` 之前注册，否则分页 COUNT 查询不会包含数据权限条件，导致分页总数不准确。
2. **多角色合并**：用户拥有多个角色时，各角色的数据权限条件用 `OR` 合并。任一角色有 `scope=1`（全部），则不做任何过滤。
3. **Fail-Closed**：`DataScopeService` 出错时返回 `AND 1=0`，确保不泄露数据。
4. **ThreadLocal 清理**：`DataScopeAspect` 在 `finally` 块中保存并恢复前一个 ThreadLocal 值（而非简单 remove），支持嵌套调用。
5. **SQL 拼接**：`DataScopeService` 通过字符串拼接构建 SQL 条件（非参数化查询）。userId 来自 JWT Token（已验证），但仍需注意 SQL 注入风险。生产环境建议对 userId 做类型校验。
6. **Scope 2 子查询**：自定义数据权限的 SQL 实际包含嵌套子查询，同时过滤 `status='0'` 和 `del_flag='0'`，确保只匹配有效的角色-部门关联。

---

## 11. API 接口文档

本项目的所有接口都可以通过 Knife4j 在线文档进行测试。启动项目后访问 `http://localhost:8080/doc.html` 即可。

### 11.1 认证接口

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| POST | `/api/auth/login` | 无需认证 | 用户登录 |
| POST | `/api/auth/logout` | 无需认证 | 用户登出 |
| POST | `/api/auth/register` | 无需认证 | 用户注册 |
| GET | `/api/auth/info` | 已登录（仅需认证） | 获取当前用户信息 |

### 11.2 用户管理接口

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/api/system/user/list` | `system:user:list` | 用户列表（分页，数据权限过滤） |
| GET | `/api/system/user/{userId}` | `system:user:query` | 用户详情 |
| POST | `/api/system/user` | `system:user:add` | 新增用户 |
| PUT | `/api/system/user` | `system:user:edit` | 编辑用户 |
| DELETE | `/api/system/user/{userIds}` | `system:user:remove` | 删除用户（逗号分隔多个 ID） |
| PUT | `/api/system/user/resetPwd` | `system:user:resetPwd` | 重置密码 |
| PUT | `/api/system/user/changeStatus` | `system:user:edit` | 修改用户状态 |

### 11.3 角色管理接口

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/api/system/role/list` | `system:role:list` | 角色列表（分页） |
| GET | `/api/system/role/{roleId}` | `system:role:query` | 角色详情 |
| POST | `/api/system/role` | `system:role:add` | 新增角色（含菜单权限） |
| PUT | `/api/system/role` | `system:role:edit` | 编辑角色（含菜单权限） |
| DELETE | `/api/system/role/{roleIds}` | `system:role:remove` | 删除角色（逗号分隔多个 ID） |
| GET | `/api/system/role/roleMenuTreeselect/{roleId}` | 已登录（仅需认证） | 获取角色已分配的菜单 ID 列表 |

### 11.4 菜单管理接口

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/api/system/menu/list` | `system:menu:list` | 菜单列表 |
| GET | `/api/system/menu/{menuId}` | `system:menu:query` | 菜单详情 |
| POST | `/api/system/menu` | `system:menu:add` | 新增菜单 |
| PUT | `/api/system/menu` | `system:menu:edit` | 编辑菜单 |
| DELETE | `/api/system/menu/{menuId}` | `system:menu:remove` | 删除菜单 |
| GET | `/api/system/menu/treeselect` | 已登录（仅需认证） | 获取完整菜单树 |
| GET | `/api/system/menu/roleMenuTreeselect/{roleId}` | 已登录（仅需认证） | 获取角色已分配的菜单树 |

### 11.5 部门管理接口

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | `/api/system/dept/list` | `system:dept:list` | 部门列表（数据权限过滤） |
| GET | `/api/system/dept/{deptId}` | `system:dept:query` | 部门详情 |
| POST | `/api/system/dept` | `system:dept:add` | 新增部门 |
| PUT | `/api/system/dept` | `system:dept:edit` | 编辑部门 |
| DELETE | `/api/system/dept/{deptId}` | `system:dept:remove` | 删除部门 |

### 11.6 接口测试工具

你可以使用以下工具测试接口：

#### 方式一：Knife4j 在线文档（推荐）

启动项目后访问 `http://localhost:8080/doc.html`，Knife4j 提供了在线测试界面：

1. 找到要测试的接口（如"认证接口 → 登录"）
2. 点击 `调试` 标签页
3. 填写请求参数
4. 点击 `发送` 按钮
5. 查看响应结果

**如何设置 Token**：点击页面上方的 `Authorization` 输入框，填入 `Bearer {你的token}`，之后所有请求都会自动携带 Token。

#### 方式二：Postman

1. **登录获取 Token**：
   - 新建请求 → `POST` → URL: `http://localhost:8080/api/auth/login`
   - `Body` → `raw` → `JSON` → 填写：
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```
   - 点击 `Send`，从响应中复制 `token` 字段值

2. **使用 Token 访问接口**：
   - 新建请求 → 选择方法和 URL
   - `Authorization` → `Type` → `Bearer Token` → 粘贴 Token 值
   - 点击 `Send`

#### 方式三：Apifox / Apipost

操作与 Postman 类似，步骤如下：

1. 新建项目 → 新建接口
2. 填写请求方法、URL、参数
3. 登录接口：`Body` → `JSON` → 填写 username 和 password
4. 保存 Token 后续复用

#### 方式四：curl 命令行

```bash
# 登录
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.data.token')

# 使用 Token 查询用户列表
curl http://localhost:8080/api/system/user/list \
  -H "Authorization: Bearer $TOKEN"
```

### 11.7 接口调用示例

**新增用户**：
```bash
curl -X POST http://localhost:8080/api/system/user \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "userName": "testuser",
    "nickName": "测试用户",
    "password": "test123",
    "deptId": 103,
    "status": "0"
  }'
```

**重置密码**：
```bash
curl -X PUT http://localhost:8080/api/system/user/resetPwd \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"userId": 2, "password": "newpass123"}'
```

**用户注册**：
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "newpass123",
    "nickname": "新用户",
    "email": "new@example.com",
    "phonenumber": "13800138000"
  }'
```

注册流程：
1. 校验用户名、密码、昵称非空
2. 检查用户名是否已存在
3. BCrypt 加密密码
4. 创建用户（默认 `status='0'`，`del_flag='0'`）
5. 自动分配默认角色（普通角色 `roleId=2`）

**新增角色（带菜单权限）**：
```bash
# menuIds 是 @RequestParam（查询参数），不是请求体的一部分
curl -X POST "http://localhost:8080/api/system/role?menuIds=100&menuIds=101&menuIds=102&menuIds=103&menuIds=104" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "roleName": "测试角色",
    "roleKey": "test",
    "roleSort": 3,
    "dataScope": "1",
    "status": "0"
  }'
```

---

## 12. 关键配置说明

这一节列出 `application.yml` 的完整配置和关键配置项说明，方便你根据自己的环境进行调整。

### 12.1 application.yml 完整配置

```yaml
server:
  port: 8080

spring:
  application:
    name: ruoyi
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&rewriteBatchedStatements=true
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      password: root
      database: 0
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

mybatis-plus:
  type-aliases-package: com.ruoyi.system.domain
  mapper-locations: classpath*:mapper/**/*Mapper.xml

jwt:
  secret: YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough123456  # JWT 签名密钥，至少 256 位
  expiration: 86400000  # Token 过期时间（毫秒），默认 24 小时

ruoyi:
  security:
    tokenHeader: Authorization  # Token 请求头名称
    tokenPrefix: "Bearer "      # Token 前缀（注意空格）

knife4j:
  enable: true
  openapi:
    title: RuoYi API
    version: 1.0.0
```

### 12.2 关键配置项说明

| 配置项 | 说明 | 生产建议 |
|--------|------|----------|
| `jwt.secret` | JWT 签名密钥 | **必须修改**，使用随机生成的长字符串 |
| `spring.datasource.password` | 数据库密码 | 使用环境变量 |
| `spring.data.redis.password` | Redis 密码 | 使用环境变量 |
| `jwt.expiration` | Token 过期时间 | 可根据安全需求调整 |

---

## 13. 常见问题与排查

如果你在启动或使用过程中遇到问题，可以在这里查找解决方案。建议按顺序排查：先检查环境（MySQL、Redis），再检查配置文件，最后看日志。

### 13.1 启动失败

#### 数据库连接失败

**现象**：`Communications link failure` 或 `Access denied for user`

**排查步骤**：
1. 确认 MySQL 服务已启动：`mysql -u root -p`
2. 确认数据库 `ry` 已创建
3. 确认 `application.yml` 中的用户名、密码正确
4. 检查 MySQL 是否允许 `localhost` 连接

#### Redis 连接失败

**现象**：`Unable to connect to Redis`

**排查步骤**：
1. 确认 Redis 服务已启动：`redis-cli ping`（应返回 PONG）
2. 确认密码配置正确
3. 如 Redis 无密码，需将 `spring.data.redis.password` 设为空字符串

#### 端口被占用

**现象**：`Web server failed to start. Port 8080 was already in use`

**解决**：修改 `application.yml` 中的 `server.port`，或终止占用进程：
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID {进程ID} /F
```

### 13.2 登录失败

#### 密码错误

**现象**：返回 `密码错误`

**排查**：
1. 确认数据库 `sys_user` 表中该用户的密码是 BCrypt 加密格式
2. 初始数据中的密码 `admin123` 对应的 BCrypt 值已预置在 `data.sql` 中
3. 如果手动插入数据，必须使用 BCrypt 加密：

```java
// 在 Java 代码中生成 BCrypt 密码
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
System.out.println(encoder.encode("admin123"));
```

#### 用户被停用

**现象**：返回 `用户已被停用`

**排查**：检查 `sys_user` 表中 `status` 字段是否为 `'0'`（正常）。

### 13.3 接口 403

**现象**：访问接口返回 `403 Forbidden`

**排查步骤**：
1. 确认请求头携带了有效的 Token：`Authorization: Bearer {token}`
2. 确认 Token 未过期（默认 24 小时）
3. 确认当前用户拥有所需权限：
   - 查询用户角色：`SELECT * FROM sys_user_role WHERE user_id = ?`
   - 查询角色菜单权限：`SELECT * FROM sys_role_menu WHERE role_id = ?`
   - 确认对应菜单的 `perms` 字段值与 `@PreAuthorize` 中声明的权限一致

> **注意**：本项目的 JWT 过滤器只校验 JWT 签名和有效期，不查 Redis。因此即使 Redis 中没有 Token，只要 JWT 本身有效，请求仍能通过认证（但会被 Spring Security 的权限校验拦截）。

#### 常见遗漏

- `selectUserList` 和 `selectUserPage` 均标注了 `@DataScope` 注解，且 `DataScopeInterceptor` 作为 MyBatis-Plus `InnerInterceptor` 会拦截**所有**查询（包括 `LambdaQueryWrapper` 构建的 SQL），因此两者的数据权限过滤均生效。

### 13.4 数据权限不生效

**现象**：普通角色（data_scope=2）可以看到所有数据

**排查步骤**：
1. 确认 Service 方法上标注了 `@DataScope` 注解
2. 确认 `MybatisPlusConfig` 中注册了 `DataScopeInterceptor`（且在 `PaginationInnerInterceptor` 之前）
3. 确认 `DataScopeService` 中查询到了正确的 `data_scope` 值
4. 在 SQL 中验证：`SELECT data_scope FROM sys_role WHERE role_id = ?`
5. 检查 `sys_role_dept` 表中是否有关联数据（scope=2 时需要）

### 13.5 权限标识说明

权限标识命名规范：`{模块}:{实体}:{操作}`

```
system:user:list       → 系统管理-用户管理-列表查询
system:user:query      → 系统管理-用户管理-详情查询
system:user:add        → 系统管理-用户管理-新增
system:user:edit       → 系统管理-用户管理-编辑
system:user:remove     → 系统管理-用户管理-删除
system:user:resetPwd   → 系统管理-用户管理-重置密码
```

超级管理员角色使用通配符 `*:*:*`，匹配所有权限。

### 13.6 部门树查询

部门表使用 `ancestors` 字段维护祖先路径（逗号分隔），支持高效查询子部门：

```sql
-- 查询某部门的所有子部门（包含自身）
SELECT * FROM sys_dept
WHERE FIND_IN_SET('101', ancestors)
-- ancestors 示例值："0,100,101"，FIND_IN_SET 会匹配包含 101 的记录
```

### 13.7 构建相关

```bash
# 完整构建
mvn clean package -DskipTests

# 仅编译（不打包）
mvn clean compile

# 跳过测试构建
mvn clean package -DskipTests

# 强制更新依赖
mvn clean package -DskipTests -U
```

> **重要**：确保在 `ruoyi` 根目录下执行 Maven 命令，不要在子模块或父目录执行。

#### 常见构建错误

**错误 1：`Non-resolvable parent POM`**

原因：Maven 无法从远程仓库下载父 POM。

解决：
1. 检查网络连接
2. 配置 Maven 镜像源（推荐阿里云）：编辑 `~/.m2/settings.xml`，在 `<mirrors>` 中添加：
   ```xml
   <mirror>
     <id>aliyun</id>
     <mirrorOf>central</mirrorOf>
     <name>Aliyun Maven</name>
     <url>https://maven.aliyun.com/repository/public</url>
   </mirror>
   ```
3. 重新执行构建命令

**错误 2：`Could not find or load main class`**

原因：模块依赖未正确构建。

解决：
1. 确保在项目根目录执行 `mvn clean package -DskipTests`
2. 检查 `ruoyi-admin/pom.xml` 中是否正确依赖了 `ruoyi-system`
3. IDEA 中：右键项目 → `Maven → Reload Project`

**错误 3：`java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver`**

原因：MySQL 驱动未正确加载。

解决：
1. 检查 `ruoyi-admin/pom.xml` 中是否包含 `mysql-connector-java` 依赖
2. 执行 `mvn clean package -DskipTests -U` 强制更新依赖

**错误 4：`Port 8080 was already in use`**

原因：8080 端口被其他进程占用。

解决：
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID {进程ID} /F

# Linux/Mac
lsof -i :8080
kill -9 {进程ID}
```

**错误 5：IDEA 中 `Cannot resolve symbol`**

原因：Maven 依赖未同步。

解决：
1. 右键项目根目录 → `Maven → Reload Project`
2. 或 `File → Invalidate Caches → Invalidate and Restart`
3. 检查 `File → Project Structure → Modules` 中所有模块是否正确识别

---

## 附录 A：关键类索引

| 类 | 路径 | 职责 |
|----|------|------|
| `R<T>` | `ruoyi-common/.../core/domain/R.java` | 统一响应封装 |
| `BaseEntity` | `ruoyi-common/.../core/domain/BaseEntity.java` | 实体基类（审计字段） |
| `Constants` | `ruoyi-common/.../core/constant/Constants.java` | 全局常量定义 |
| `GlobalExceptionHandler` | `ruoyi-common/.../core/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `MyMetaObjectHandler` | `ruoyi-common/.../core/handler/MyMetaObjectHandler.java` | 审计字段自动填充 |
| `DataScope` | `ruoyi-common/.../core/annotation/DataScope.java` | 数据权限注解 |
| `DataScopeAspect` | `ruoyi-common/.../core/aspect/DataScopeAspect.java` | 数据权限 AOP 切面 |
| `DataScopeParams` | `ruoyi-common/.../core/domain/DataScopeParams.java` | ThreadLocal 数据载体 |
| `DataScopeInterceptor` | `ruoyi-common/.../core/handler/DataScopeInterceptor.java` | SQL 拦截器 |
| `SecurityConfig` | `ruoyi-common/.../security/config/SecurityConfig.java` | Spring Security 配置 |
| `JwtAuthenticationFilter` | `ruoyi-common/.../security/filter/JwtAuthenticationFilter.java` | JWT 认证过滤器 |
| `JwtTokenProvider` | `ruoyi-common/.../security/utils/JwtTokenProvider.java` | JWT 工具类 |
| `TokenService` | `ruoyi-common/.../security/service/TokenService.java` | Token 管理 |
| `LoginService` | `ruoyi-common/.../security/service/LoginService.java` | 登录/登出服务 |
| `PermissionService` | `ruoyi-common/.../security/service/PermissionService.java` | 权限查询 |
| `DataScopeService` | `ruoyi-common/.../security/service/DataScopeService.java` | 数据权限条件构建 |
| `RedisConfig` | `ruoyi-common/.../redis/config/RedisConfig.java` | Redis 序列化配置 |
| `MybatisPlusConfig` | `ruoyi-admin/.../config/MybatisPlusConfig.java` | MyBatis-Plus 拦截器注册 |
| `RuoYiAdminApplication` | `ruoyi-admin/.../RuoYiAdminApplication.java` | 应用启动类 |
| `AuthController` | `ruoyi-system/.../controller/AuthController.java` | 认证接口 |
| `SysUserController` | `ruoyi-system/.../controller/SysUserController.java` | 用户管理接口 |
| `SysRoleController` | `ruoyi-system/.../controller/SysRoleController.java` | 角色管理接口 |
| `SysMenuController` | `ruoyi-system/.../controller/SysMenuController.java` | 菜单管理接口 |
| `SysDeptController` | `ruoyi-system/.../controller/SysDeptController.java` | 部门管理接口 |

## 附录 B：技术决策说明

### 为什么 PermissionService 和 DataScopeService 使用 JdbcTemplate？

`ruoyi-common` 是公共模块，`ruoyi-system` 依赖 `ruoyi-common`。如果 `ruoyi-common` 中的 `PermissionService` 依赖 `ISysMenuService`（在 `ruoyi-system` 中），就会形成循环依赖。因此使用 `JdbcTemplate` 直接执行 SQL 查询，绕过依赖限制。

### 为什么 DataScopeInterceptor 使用反射修改 BoundSql？

MyBatis-Plus 的 `InnerInterceptor` 接口只暴露了 `beforeQuery` 方法，无法直接替换 `BoundSql` 对象。通过反射修改 `BoundSql.sql` 字段是业界标准做法，MyBatis 官方文档也推荐此方式。

### 为什么 Token 同时存在 JWT 和 Redis 中？

JWT 本身是无状态的，但本项目需要支持主动登出（Token 失效）和 Token 唯一性。Redis 存储实现了：
1. 登出时立即删除 Token（JWT 本身无法主动失效）
2. 服务端控制 Token 有效期（JWT 的 exp 字段为辅助）
3. 同一用户只保留最新 Token（新登录覆盖旧 Token）

### Redis 序列化配置

`RedisConfig` 使用 `GenericJackson2JsonRedisSerializer`（配置了 `JavaTimeModule` 支持日期类型）作为值序列化器，`StringRedisSerializer` 作为键序列化器。这意味着：
- Redis 中存储的 Token 值是 JSON 格式字符串
- 调试时可直接用 `redis-cli GET login_tokens:{userId}` 查看

### PermissionService 查询一致性说明

`PermissionService.getRoleKeysByUserId()` 查询条件包含 `r.status = '0'` 但**未过滤** `r.del_flag = '0'`。而 `SysRoleMapper.xml` 中的同名查询同时过滤了 `status` 和 `del_flag`。这意味着 JWT 过滤器中可能加载已软删除角色的 role_key。由于 `@PreAuthorize` 校验的是 role_key（如 `ROLE_admin`），而软删除的角色通常不会影响业务逻辑，此差异在实际使用中影响较小。如需严格一致，可在 `PermissionService` 的 SQL 中补充 `AND r.del_flag = '0'` 条件。
