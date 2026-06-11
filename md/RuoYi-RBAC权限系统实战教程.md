# RuoYi RBAC 权限管理系统 —— 从零到一实战教程

> **技术栈**: Spring Boot 3.2.5 + Spring Security 6 + JWT + MyBatis-Plus 3.5.5 + MySQL 8 + Redis
> **适用人群**: 有 Java 基础，想学习 RBAC 权限系统设计与实现的开发者
> **项目特点**: 纯后端项目，提供 RESTful API，前端和微信小程序通过 JSON 接口联调

---

## 目录

- [第一章 项目概述与环境搭建](#第一章-项目概述与环境搭建)
  - [1.1 项目简介](#11-项目简介)
  - [1.2 技术选型](#12-技术选型)
  - [1.3 项目结构](#13-项目结构)
  - [1.4 环境准备](#14-环境准备)
  - [1.5 启动项目](#15-启动项目)
  - [1.6 验证启动](#16-验证启动)
- [第二章 RBAC 权限模型设计](#第二章-rbac-权限模型设计)
  - [2.1 什么是 RBAC](#21-什么是-rbac)
  - [2.2 本项目的 RBAC 模型](#22-本项目的-rbac-模型)
  - [2.3 数据库表结构设计](#23-数据库表结构设计)
  - [2.4 数据流转过程](#24-数据流转过程)
- [第三章 数据库层实现](#第三章-数据库层实现)
  - [3.1 实体类设计](#31-实体类设计)
  - [3.2 MyBatis-Plus 配置](#32-mybatis-plus-配置)
  - [3.3 Mapper 接口与 XML](#33-mapper-接口与-xml)
- [第四章 认证流程（登录 + JWT）](#第四章-认证流程登录--jwt)
  - [4.1 整体认证流程](#41-整体认证流程)
  - [4.2 登录接口实现](#42-登录接口实现)
  - [4.3 JWT Token 生成与验证](#43-jwt-token-生成与验证)
  - [4.4 登录认证服务](#44-登录认证服务)
  - [4.5 前端如何使用 Token](#45-前端如何使用-token)
- [第五章 Spring Security 核心配置](#第五章-spring-security-核心配置)
  - [5.1 Security 配置详解](#51-security-配置详解)
  - [5.2 JWT 认证过滤器](#52-jwt-认证过滤器)
  - [5.3 权限校验：@PreAuthorize](#53-权限校验preauthorize)
  - [5.4 自定义 401/403 响应](#54-自定义-401403-响应)
- [第六章 接口鉴权完整链路](#第六章-接口鉴权完整链路)
  - [6.1 一次请求的完整鉴权过程](#61-一次请求的完整鉴权过程)
  - [6.2 代码分层架构](#62-代码分层架构)
  - [6.3 前端权限控制策略](#63-前端权限控制策略)
- [第七章 用户管理模块实战](#第七章-用户管理模块实战)
  - [7.1 DTO/VO 设计模式](#71-dtovo-设计模式)
  - [7.2 用户 CRUD 接口](#72-用户-crud-接口)
  - [7.3 密码加密存储](#73-密码加密存储)
  - [7.4 逻辑删除](#74-逻辑删除)
- [第八章 角色与菜单管理](#第八章-角色与菜单管理)
  - [8.1 角色管理](#81-角色管理)
  - [8.2 菜单管理](#82-菜单管理)
  - [8.3 角色-菜单关联](#83-角色-菜单关联)
- [第九章 部门管理与数据权限](#第九章-部门管理与数据权限)
  - [9.1 部门树形结构](#91-部门树形结构)
  - [9.2 数据权限设计](#92-数据权限设计)
- [第十章 常见问题与解决方案](#第十章-常见问题与解决方案)
  - [10.1 环境与依赖问题](#101-环境与依赖问题)
  - [10.2 Spring Security 问题](#102-spring-security-问题)
  - [10.3 JWT 与 Token 问题](#103-jwt-与-token-问题)
  - [10.4 MyBatis-Plus 问题](#104-mybatis-plus-问题)
  - [10.5 MySQL 与 Redis 问题](#105-mysql-与-redis-问题)
  - [10.6 跨域（CORS）问题](#106-跨域cors问题)
  - [10.7 日志与调试](#107-日志与调试)
- [第十一章 APIFox 接口测试指南](#第十一章-apifox-接口测试指南)
  - [11.1 创建项目与环境配置](#111-创建项目与环境配置)
  - [11.2 配置全局认证](#112-配置全局认证)
  - [11.3 测试登录接口](#113-测试登录接口)
  - [11.4 测试需认证的接口](#114-测试需认证的接口)
  - [11.5 测试权限控制（403 场景）](#115-测试权限控制403-场景)
  - [11.6 批量测试所有接口](#116-批量测试所有接口)
  - [11.7 APIFox 常见问题](#117-apifox-常见问题)

---

## 第一章 项目概述与环境搭建

### 1.1 项目简介

本项目是一个基于 **RBAC（基于角色的访问控制）** 模型的后端权限管理系统，提供完整的用户管理、角色管理、菜单管理、部门管理等 RESTful API 接口。

**核心能力**:
- 用户名 + 密码登录，返回 JWT Token
- 基于角色的权限控制（RBAC），精确到按钮级别
- 部门树形管理 + 数据权限过滤
- 统一的响应格式、异常处理、日志记录

### 1.2 技术选型

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.5 | 应用框架 |
| Spring Security | 6.x | 认证授权框架 |
| JWT (jjwt) | 0.12.5 | Token 生成与验证 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | Token 缓存 |
| Lombok | - | 简化实体类代码 |
| Knife4j | 4.5.0 | API 文档（Swagger） |

### 1.3 项目结构

采用 **多模块 Maven** 架构，职责清晰：

```
RuoYi/
├── ruoyi-admin/          # 启动模块（配置文件、启动类）
│   └── src/main/resources/
│       ├── application.yml          # 主配置
│       └── application-dev.yml      # 开发环境配置
│
├── ruoyi-common/         # 公共模块（安全、工具、异常处理）
│   └── src/main/java/com/ruoyi/common/
│       ├── core/                    # 核心组件
│       │   ├── domain/R.java        # 统一响应封装
│       │   ├── domain/BaseEntity.java # 实体基类
│       │   ├── constant/Constants.java # 全局常量
│       │   ├── exception/           # 全局异常处理
│       │   └── filter/RequestLogFilter.java # 请求日志
│       └── security/                # 安全组件
│           ├── config/SecurityConfig.java   # Security 配置
│           ├── filter/JwtAuthenticationFilter.java # JWT 过滤器
│           ├── service/TokenService.java     # Token 服务
│           ├── service/LoginService.java     # 登录服务
│           └── handler/                      # 401/403 处理
│
└── ruoyi-system/         # 业务模块（用户、角色、菜单、部门）
    └── src/main/java/com/ruoyi/system/
        ├── controller/              # 控制器层
        ├── service/                 # 业务逻辑层
        ├── mapper/                  # 数据访问层
        └── domain/
            ├── entity/              # 数据库实体
            ├── dto/                 # 请求传输对象
            └── vo/                  # 响应视图对象
```

**模块依赖关系**:

```
ruoyi-admin → ruoyi-system → ruoyi-common
```

### 1.4 环境准备

**必装软件**:

| 软件 | 版本要求 | 用途 |
|------|----------|------|
| JDK | 17+ | Java 运行环境 |
| Maven | 3.8+ | 项目构建 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | Token 缓存 |
| IDE | IntelliJ IDEA | 开发工具（推荐） |

**第一步：创建数据库**

```sql
CREATE DATABASE `ry` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

**第二步：导入表结构和初始数据**

执行项目中的 `sql/schema.sql`，它会创建以下表并插入初始数据：

| 表名 | 说明 | 初始数据 |
|------|------|----------|
| sys_user | 用户表 | admin 超级管理员 |
| sys_role | 角色表 | admin(超级管理员)、common(普通角色) |
| sys_menu | 菜单表 | 系统管理菜单 + 按钮权限 |
| sys_dept | 部门表 | 若依科技及其子部门 |
| sys_user_role | 用户-角色关联 | admin → 超级管理员角色 |
| sys_role_menu | 角色-菜单关联 | 超级管理员 → 所有菜单 |

**第三步：启动 Redis**

```bash
# Windows
redis-server

# Linux/Mac
sudo systemctl start redis
```

**第四步：修改数据库连接**

编辑 `ruoyi-admin/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root          # 改成你的 MySQL 用户名
    password: root          # 改成你的 MySQL 密码
  data:
    redis:
      host: localhost
      port: 6379
      password: root        # 改成你的 Redis 密码（无密码则留空）
```

### 1.5 启动项目

**方式一：IDEA 启动**

1. 用 IDEA 打开项目根目录 `RuoYi`
2. 等待 Maven 依赖下载完成
3. 运行 `ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java`

**方式二：命令行启动**

```bash
cd D:\dev\code\RuoYi\RuoYi
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin-1.0.0.jar
```

### 1.6 验证启动

看到以下日志说明启动成功：

```
Tomcat started on port 8080 (http)
Started RuoYiApplication in 3.xxx seconds
```

**使用 APIFox 测试登录接口**:

1. 打开 APIFox，新建项目 `RuoYi RBAC 权限系统`
2. 新建请求：`POST http://localhost:8080/auth/login`
3. 选择 `Body` → `raw` → `JSON`，输入：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

4. 点击发送，成功响应：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "userName": "admin",
    "nickName": "超级管理员",
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "roles": ["admin"],
    "permissions": ["system:user:list", "system:user:add", "..."]
  }
}
```

> **详细 APIFox 配置步骤请参考** [第十一章 APIFox 接口测试指南](#第十一章-apifox-接口测试指南)

---

## 第二章 RBAC 权限模型设计

### 2.1 什么是 RBAC

RBAC（Role-Based Access Control，基于角色的访问控制）是一种广泛使用的权限管理模型。

**核心思想**: 用户不直接关联权限，而是通过**角色**间接获取权限。

```
用户 → 角色 → 权限（菜单/按钮）
```

**优势**:
- 用户和权限解耦，通过角色桥接
- 角色变化时，只需修改角色-权限关联，无需逐个修改用户
- 支持一个用户拥有多个角色

### 2.2 本项目的 RBAC 模型

```
┌─────────────────────────────────────────────────────────┐
│                    RBAC 权限模型                          │
│                                                          │
│   ┌──────┐    ┌──────────────┐    ┌──────┐              │
│   │ 用户  │───→│ 用户-角色关联  │←───│ 角色  │              │
│   └──────┘    └──────────────┘    └──────┘              │
│                                      │                   │
│                                      ↓                   │
│                              ┌──────────────┐            │
│                              │ 角色-菜单关联  │            │
│                              └──────────────┘            │
│                                      │                   │
│                                      ↓                   │
│                                ┌──────┐                  │
│                                │ 菜单  │                  │
│                                └──────┘                  │
│                                                          │
│   菜单类型：                                               │
│   ├── M（目录）  → 一级菜单容器                              │
│   ├── C（菜单）  → 页面路由                                 │
│   └── F（按钮）  → 操作权限标识（如 system:user:add）        │
└─────────────────────────────────────────────────────────┘
```

### 2.3 数据库表结构设计

**核心表及关系**:

```sql
-- 用户表
CREATE TABLE sys_user (
  user_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  dept_id      BIGINT          COMMENT '部门ID',
  user_name    VARCHAR(30)     COMMENT '登录账号',
  nick_name    VARCHAR(30)     COMMENT '显示昵称',
  password     VARCHAR(100)    COMMENT 'BCrypt加密密码',
  status       CHAR(1) DEFAULT '0'  COMMENT '0=正常 1=停用',
  del_flag     CHAR(1) DEFAULT '0'  COMMENT '0=正常 2=删除',
  login_ip     VARCHAR(128)    COMMENT '最后登录IP',
  login_date   DATETIME        COMMENT '最后登录时间',
  create_by    VARCHAR(64)     COMMENT '创建者',
  create_time  DATETIME        COMMENT '创建时间',
  update_by    VARCHAR(64)     COMMENT '更新者',
  update_time  DATETIME        COMMENT '更新时间'
);

-- 角色表
CREATE TABLE sys_role (
  role_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_name    VARCHAR(30)     COMMENT '角色名称',
  role_key     VARCHAR(100)    COMMENT '角色标识（如admin）',
  role_sort    INT             COMMENT '显示顺序',
  data_scope   CHAR(1) DEFAULT '1'  COMMENT '数据范围 1=全部 2=自定义 3=本部门 4=本部门及以下 5=仅本人',
  status       CHAR(1) DEFAULT '0'  COMMENT '0=正常 1=停用',
  del_flag     CHAR(1) DEFAULT '0'  COMMENT '0=正常 2=删除'
);

-- 菜单表
CREATE TABLE sys_menu (
  menu_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  menu_name    VARCHAR(50)     COMMENT '菜单名称',
  parent_id    BIGINT DEFAULT 0    COMMENT '父菜单ID（0=顶级）',
  order_num    INT DEFAULT 0       COMMENT '显示顺序',
  path         VARCHAR(200)    COMMENT '路由地址',
  component    VARCHAR(255)    COMMENT '组件路径',
  menu_type    CHAR(1)         COMMENT 'M=目录 C=菜单 F=按钮',
  perms        VARCHAR(100)    COMMENT '权限标识（如system:user:list）',
  icon         VARCHAR(100)    COMMENT '菜单图标',
  visible      CHAR(1) DEFAULT '0' COMMENT '0=显示 1=隐藏',
  status       CHAR(1) DEFAULT '0' COMMENT '0=正常 1=停用',
  del_flag     CHAR(1) DEFAULT '0' COMMENT '0=正常 2=删除'
);

-- 部门表
CREATE TABLE sys_dept (
  dept_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  parent_id    BIGINT DEFAULT 0    COMMENT '父部门ID（0=顶级）',
  ancestors    VARCHAR(500)    COMMENT '祖级列表（如0,100,101）',
  dept_name    VARCHAR(30)     COMMENT '部门名称',
  order_num    INT DEFAULT 0       COMMENT '显示顺序',
  leader       VARCHAR(20)     COMMENT '负责人',
  status       CHAR(1) DEFAULT '0' COMMENT '0=正常 1=停用',
  del_flag     CHAR(1) DEFAULT '0' COMMENT '0=正常 2=删除'
);

-- 用户-角色关联表（多对多）
CREATE TABLE sys_user_role (
  user_id  BIGINT NOT NULL COMMENT '用户ID',
  role_id  BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id)
);

-- 角色-菜单关联表（多对多）
CREATE TABLE sys_role_menu (
  role_id  BIGINT NOT NULL COMMENT '角色ID',
  menu_id  BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (role_id, menu_id)
);
```

**初始数据说明**:

| 用户 | 角色 | 权限 |
|------|------|------|
| admin（admin123） | 超级管理员（admin） | 所有菜单和按钮权限 |
| 注册用户 | 普通角色（common） | 基础查看权限 |

### 2.4 数据流转过程

以"admin 用户登录后访问用户列表"为例：

```
1. 用户提交 admin/admin123 → POST /auth/login
2. 后端校验密码 → 生成 JWT Token（包含 userId=1）
3. 前端存储 Token，后续请求携带 Authorization: Bearer <token>
4. 前端请求 GET /system/user/list
5. JWT 过滤器解析 Token → 获取 userId=1
6. 查询 userId=1 的角色 → [admin]
7. 查询角色 admin 的权限 → [system:user:list, system:user:add, ...]
8. @PreAuthorize("hasAuthority('system:user:list')") 校验通过
9. 执行业务逻辑，返回用户列表
```

---

## 第三章 数据库层实现

### 3.1 实体类设计

**实体基类 BaseEntity** — 所有实体的公共父类：

```java
@Data
public class BaseEntity implements Serializable {
    @TableField(fill = FieldFill.INSERT)        // 插入时自动填充
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)  // 插入和更新时自动填充
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private String remark;
}
```

**用户实体 SysUser** — 核心实体：

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private Long deptId;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String sex;
    private String avatar;

    @JsonIgnore                          // JSON序列化时忽略，防止密码泄露
    private String password;

    private String status;
    @TableLogic(value = "0", delval = "2")  // 逻辑删除：0=正常, 2=已删除
    private String delFlag;
    private String loginIp;
    private Date loginDate;

    @TableField(exist = false)           // 非数据库字段
    private List<SysRole> roles;

    @TableField(exist = false)
    private String beginTime;  // 查询条件用
    @TableField(exist = false)
    private String endTime;    // 查询条件用
}
```

**角色实体 SysRole**:

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long roleId;
    private String roleName;
    private String roleKey;       // 角色标识，如 "admin"
    private Integer roleSort;
    private String dataScope;     // 数据权限范围（1-5）
    private Integer menuCheckStrictly;
    private Integer deptCheckStrictly;
    private String status;
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
}
```

**菜单实体 SysMenu** — 权限的核心载体：

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long menuId;
    private String menuName;
    private Long parentId;
    private Integer orderNum;
    private String path;
    private String component;
    private String query;
    private Integer isFrame;
    private Integer isCache;
    private String menuType;      // M=目录, C=菜单, F=按钮
    private String visible;
    private String status;
    private String perms;         // 权限标识，如 "system:user:list"
    private String icon;
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();
}
```

### 3.2 MyBatis-Plus 配置

**关键配置** (`application.yml`):

```yaml
mybatis-plus:
  type-aliases-package: com.ruoyi.system.domain   # 实体类包路径
  mapper-locations: classpath*:mapper/**/*Mapper.xml  # XML映射文件位置
```

**自动填充处理器** — 自动设置 createBy/updateBy/createTime/updateTime：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        // createBy/updateBy 从 SecurityContext 中获取当前用户名
        String username = SecurityUtils.getUsername();
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        String username = SecurityUtils.getUsername();
        this.strictUpdateFill(metaObject, "updateBy", String.class, username);
    }
}
```

### 3.3 Mapper 接口与 XML

**Mapper 接口**继承 MyBatis-Plus 的 BaseMapper，获得基础 CRUD 能力：

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // BaseMapper 提供：insert, deleteById, updateById, selectById, selectList 等
    // 自定义方法在 XML 中实现
}
```

**XML 示例** — 用户列表查询（关联部门名称）：

```xml
<mapper namespace="com.ruoyi.system.mapper.SysUserMapper">
    <select id="selectUserList" resultType="SysUser">
        SELECT u.* FROM sys_user u
        <where>
            <if test="userName != null and userName != ''">
                AND u.user_name LIKE CONCAT('%', #{userName}, '%')
            </if>
            <if test="status != null and status != ''">
                AND u.status = #{status}
            </if>
            <if test="deptId != null">
                AND u.dept_id = #{deptId}
            </if>
            <if test="beginTime != null and beginTime != ''">
                AND u.create_time &gt;= #{beginTime}
            </if>
            <if test="endTime != null and endTime != ''">
                AND u.create_time &lt;= #{endTime}
            </if>
        </where>
        ORDER BY u.create_time DESC
    </select>
</mapper>
```

**XML 示例** — 根据用户ID查询权限标识：

```xml
<select id="selectMenuPermsByUserId" resultType="String">
    SELECT DISTINCT m.perms
    FROM sys_menu m
    INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id
    INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
    WHERE ur.user_id = #{userId}
      AND m.status = '0'
      AND m.perms IS NOT NULL
      AND m.perms != ''
</select>
```

---

## 第四章 认证流程（登录 + JWT）

### 4.1 整体认证流程

```
前端                          后端
  │                             │
  │  POST /auth/login           │
  │  {username, password}       │
  │ ──────────────────────────→ │
  │                             │  1. 查询用户（getUserByUserName）
  │                             │  2. 校验状态（是否停用）
  │                             │  3. 校验密码（BCrypt matches）
  │                             │  4. 生成 JWT Token（包含 userId）
  │                             │  5. Token 存入 Redis（设置过期时间）
  │                             │  6. 加载角色和权限列表
  │  {token, user, roles,       │
  │   permissions}              │
  │ ←────────────────────────── │
  │                             │
  │  GET /auth/info             │
  │  Authorization: Bearer xxx  │
  │ ──────────────────────────→ │
  │                             │  1. 解析 Token → 获取 userId
  │                             │  2. 查询用户详细信息
  │                             │  3. 加载角色和权限
  │  {user, roles, permissions} │
  │ ←────────────────────────── │
```

### 4.2 登录接口实现

```java
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private ISysMenuService menuService;

    @Value("${jwt.expiration:86400000}")
    private long tokenExpiration;

    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO loginBody,
                            HttpServletRequest request) {
        // 1. 查询用户
        SysUser user = userService.getUserByUserName(loginBody.getUsername());
        if (user == null) {
            return R.fail(401, "用户不存在或密码错误");
        }

        // 2. 登录认证（校验状态 + 密码 → 生成Token）
        String token = loginService.login(
            loginBody.getUsername(),
            loginBody.getPassword(),
            String.valueOf(user.getUserId()),
            user.getPassword(),    // 数据库中的BCrypt哈希
            user.getStatus()
        );

        // 3. 构建响应
        LoginVO loginUser = new LoginVO();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + tokenExpiration);

        // 4. 加载角色和权限
        Set<String> roleKeys = roleService.getRoleKeysByUserId(user.getUserId());
        Set<String> perms = menuService.getMenuPermsByUserId(user.getUserId());
        loginUser.setRoles(new ArrayList<>(roleKeys));
        loginUser.setPermissions(new ArrayList<>(perms));

        // 5. 更新登录信息
        userService.updateUserLoginInfo(user.getUserId(), request.getRemoteAddr());

        return R.ok(loginUser);
    }
}
```

**LoginDTO** — 登录请求体：

```java
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

**LoginVO** — 登录响应体：

```java
@Data
public class LoginVO {
    private Long userId;
    private String userName;
    private String nickName;
    private String token;          // JWT Token
    private Long loginTime;        // 登录时间戳
    private Long expireTime;       // 过期时间戳
    private String ip;
    private String address;
    private List<String> permissions;  // 权限标识列表
    private List<String> roles;        // 角色标识列表
}
```

### 4.3 JWT Token 生成与验证

**TokenService** 是 Token 管理的核心，负责生成、验证、解析 Token：

```java
@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long tokenExpiration;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成 JWT Token 并缓存到 Redis
     */
    public String createToken(String userId, String username) {
        // 构建 JWT，claims 中存储 userId 和 username
        String token = Jwts.builder()
            .subject(userId)
            .claim("username", username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .compact();

        // 缓存到 Redis，key = "login:token:{userId}"
        redisTemplate.opsForValue().set(
            "login:token:" + userId,
            token,
            tokenExpiration,
            TimeUnit.MILLISECONDS
        );
        return token;
    }

    /**
     * 从 Token 中解析 userId
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 Token 是否有效（未过期 + Redis 中存在）
     */
    public boolean validateToken(String token) {
        String userId = getUserIdFromToken(token);
        if (userId == null) return false;

        // 检查 Redis 中是否存在
        String cachedToken = redisTemplate.opsForValue().get("login:token:" + userId);
        return token.equals(cachedToken);
    }

    /**
     * 删除 Token（登出时使用）
     */
    public boolean removeToken(String userId) {
        return Boolean.TRUE.equals(
            redisTemplate.delete("login:token:" + userId)
        );
    }
}
```

### 4.4 登录认证服务

```java
@Service
public class LoginService {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String username, String password,
                        String userId, String realPassword, String status) {
        // 1. 检查用户状态
        if ("1".equals(status)) {
            throw new ServiceException("用户已被停用");
        }

        // 2. 校验密码（BCrypt 安全比较）
        if (!passwordEncoder.matches(password, realPassword)) {
            throw new ServiceException("密码错误");
        }

        // 3. 生成 Token
        return tokenService.createToken(userId, username);
    }

    public void logout(String userId) {
        tokenService.removeToken(userId);
    }
}
```

> **关键点**: `passwordEncoder.matches()` 是 BCrypt 的安全比较，不会泄露哈希值信息。

### 4.5 前端如何使用 Token

登录成功后，前端需要：

```javascript
// 1. 存储 Token
localStorage.setItem('token', response.data.token)

// 2. 后续请求携带 Token
axios.get('/system/user/list', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token')
  }
})

// 3. 或使用请求拦截器统一添加
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})
```

---

## 第五章 Spring Security 核心配置

### 5.1 Security 配置详解

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity    // 启用 @PreAuthorize 注解
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private RequestLogFilter requestLogFilter;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. 禁用 CSRF（REST API 使用 JWT，无需 CSRF）
            .csrf(AbstractHttpConfigurer::disable)

            // 2. 启用 CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. 无状态会话（不使用 HttpSession）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4. URL 权限配置
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/auth/logout", "/auth/register").permitAll()
                .requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**").permitAll()
                .anyRequest().authenticated()
            )

            // 5. 自定义 401/403 响应
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            // 6. 添加过滤器
            .addFilterBefore(requestLogFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**配置要点**:

| 配置项 | 说明 |
|--------|------|
| `csrf().disable()` | REST API 使用 JWT 认证，不需要 CSRF 保护 |
| `sessionManagement(STATELESS)` | 不创建 HttpSession，完全依赖 JWT |
| `permitAll()` | 登录/注册/登出接口无需认证 |
| `@EnableMethodSecurity` | 启用方法级权限控制（@PreAuthorize） |
| `addFilterBefore(jwtFilter)` | 在 UsernamePasswordAuthenticationFilter 之前执行 JWT 校验 |

### 5.2 JWT 认证过滤器

每个请求都会经过这个过滤器，它负责解析 Token 并设置认证信息：

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从 Header 中提取 Token
        String token = extractToken(request);

        if (token != null) {
            try {
                // 2. 验证 Token
                if (tokenService.validateToken(token)) {
                    // 3. 解析 userId
                    String userId = tokenService.getUserIdFromToken(token);

                    // 4. 创建认证对象，放入 SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userId,    // principal（后续通过 getPrincipal() 获取）
                            null,      // credentials
                            Collections.emptyList()  // authorities（权限在 @PreAuthorize 中动态查询）
                        );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token 无效，不设置认证，后续会返回 401
                logger.error("Token validation failed", e);
            }
        }

        // 5. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

**流程图**:

```
请求进入
  │
  ├─ 提取 Authorization Header
  │
  ├─ Token 为空？ ──→ 跳过，继续执行（后续返回 401）
  │
  ├─ 验证 Token（Redis 中存在 + 未过期）
  │   ├─ 有效 → 解析 userId → 设置 SecurityContext → 继续执行
  │   └─ 无效 → 不设置 SecurityContext → 继续执行（后续返回 401）
  │
  └─ filterChain.doFilter()
```

### 5.3 权限校验：@PreAuthorize

Controller 方法上的权限注解，在方法执行**之前**进行权限校验：

```java
// 需要 system:user:list 权限才能访问
@PreAuthorize("hasAuthority('system:user:list')")
@GetMapping("/list")
public R<PageResult<SysUserVO>> list(...) { ... }

// 需要 system:user:add 权限才能访问
@PreAuthorize("hasAuthority('system:user:add')")
@PostMapping
public R<Void> add(@Validated @RequestBody SysUserDTO dto) { ... }
```

**hasAuthority 的工作原理**:

```
1. @PreAuthorize("hasAuthority('system:user:list')") 触发
2. Spring Security 从 SecurityContext 获取当前用户的 Authentication
3. 调用 Authentication.getAuthorities() 获取权限列表
4. 检查列表中是否包含 "system:user:list"
5. 包含 → 允许访问；不包含 → 抛出 AccessDeniedException
```

> **注意**: 本项目中 authorities 列表为空（在 JWT 过滤器中设置为 `Collections.emptyList()`），权限校验通过数据库实时查询实现。这意味着每次请求都会查询数据库获取最新权限，适合权限需要实时生效的场景。

### 5.4 自定义 401/403 响应

Spring Security 默认返回空 body 或 HTML 页面，对前端不友好。自定义处理器返回统一 JSON 格式：

**401 未认证**:

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            new ObjectMapper().writeValueAsString(
                R.fail(401, "未登录或Token已过期")
            )
        );
    }
}
```

**403 权限不足**:

```java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            new ObjectMapper().writeValueAsString(
                R.fail(403, "权限不足")
            )
        );
    }
}
```

---

## 第六章 接口鉴权完整链路

### 6.1 一次请求的完整鉴权过程

以 `GET /system/user/list` 为例，展示从请求到响应的完整链路：

```
前端发起请求
GET /system/user/list?pageNum=1&pageSize=10
Header: Authorization: Bearer eyJhbGci...
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  RequestLogFilter（第1层）                                    │
│  记录请求开始：POST /system/user/list | IP=127.0.0.1          │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  JwtAuthenticationFilter（第2层）                             │
│  1. 提取 Header → "Bearer eyJhbGci..."                       │
│  2. 去掉前缀 → "eyJhbGci..."                                │
│  3. validateToken → Redis查询 → 有效                         │
│  4. getUserIdFromToken → "1"                                │
│  5. 设置 SecurityContext → authentication.principal = "1"    │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  Spring Security 授权检查                                     │
│  URL: /system/user/list                                      │
│  匹配规则: anyRequest().authenticated()                      │
│  SecurityContext 中有 Authentication → 通过                   │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  @PreAuthorize("hasAuthority('system:user:list')")           │
│  查询当前用户权限 → 包含 system:user:list → 通过               │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  SysUserController.list()                                    │
│  执行业务逻辑 → 查询用户列表 → 返回 R<PageResult<SysUserVO>>  │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│  RequestLogFilter（finally 块）                               │
│  记录请求结束：状态=200 | 耗时=35ms | 用户=admin              │
└─────────────────────────────────────────────────────────────┘
        │
        ▼
    前端收到响应
```

### 6.2 代码分层架构

```
Controller（接收请求、参数校验、返回响应）
    │
    ▼
Service（业务逻辑、事务管理）
    │
    ▼
Mapper（数据访问、SQL 执行）
    │
    ▼
MySQL / Redis
```

**每一层的职责**:

| 层 | 职责 | 示例 |
|----|------|------|
| Controller | 接收请求、参数校验（@Valid）、调用 Service、返回 R<> | SysUserController |
| Service | 业务逻辑、事务管理（@Transactional）、异常抛出 | SysUserServiceImpl |
| Mapper | 数据库操作（CRUD）、自定义 SQL | SysUserMapper |
| Entity | 数据库表映射 | SysUser |
| DTO | 请求参数封装（入参） | SysUserDTO |
| VO | 响应数据封装（出参） | SysUserVO |

### 6.3 前端权限控制策略

**菜单权限** — 控制页面路由：

```javascript
// 登录后获取权限列表
const permissions = response.data.permissions

// 根据权限过滤路由表
const accessibleRoutes = routes.filter(route => {
  return permissions.includes(route.meta.permission)
})

// 动态添加路由
router.addRoutes(accessibleRoutes)
```

**按钮权限** — 控制操作按钮：

```vue
<template>
  <el-button v-if="hasPermission('system:user:add')">新增</el-button>
  <el-button v-if="hasPermission('system:user:edit')">修改</el-button>
  <el-button v-if="hasPermission('system:user:remove')">删除</el-button>
</template>

<script>
function hasPermission(perm) {
  return permissions.includes(perm)
}
</script>
```

---

## 第七章 用户管理模块实战

### 7.1 DTO/VO 设计模式

本项目采用 **DTO/VO 分离** 模式，不直接暴露数据库实体：

```
前端请求 → DTO（入参校验）→ Entity（数据库操作）→ VO（出参过滤）
```

**SysUserDTO** — 新增/修改用户时的请求体：

```java
@Data
public class SysUserDTO {
    @NotNull(message = "用户ID不能为空", groups = UpdateGroup.class)
    private Long userId;

    private Long deptId;

    @NotBlank(message = "用户账号不能为空", groups = CreateGroup.class)
    @Size(min = 2, max = 20, message = "用户账号长度必须在 2 到 20 个字符之间")
    private String userName;

    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickName;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 11, message = "手机号码长度不能超过 11 个字符")
    private String phonenumber;

    private String sex;
    private String avatar;

    @NotBlank(message = "用户密码不能为空", groups = CreateGroup.class)
    @Size(min = 6, max = 20, message = "密码长度必须在 6 到 20 个字符之间")
    private String password;

    private String status;
    private String remark;

    // DTO → Entity 转换
    public SysUser toEntity() {
        SysUser user = new SysUser();
        user.setUserId(this.userId);
        user.setDeptId(this.deptId);
        user.setUserName(this.userName);
        // ... 其他字段
        return user;
    }

    // 校验分组：新增时校验密码，修改时不校验
    public interface CreateGroup {}
    public interface UpdateGroup {}
}
```

**SysUserVO** — 返回给前端的响应体（过滤掉密码等敏感字段）：

```java
@Data
public class SysUserVO {
    private Long userId;
    private Long deptId;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private String sex;
    private String avatar;
    private String status;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private List<SysRoleVO> roles;  // 关联的角色列表

    // Entity → VO 转换
    public static SysUserVO fromEntity(SysUser user) {
        if (user == null) return null;
        SysUserVO vo = new SysUserVO();
        vo.setUserId(user.getUserId());
        vo.setUserName(user.getUserName());
        // ... 其他字段（不包含 password、delFlag、loginIp）
        return vo;
    }
}
```

**使用校验分组**:

```java
// 新增时使用 CreateGroup 校验（校验密码）
@PostMapping
public R<Void> add(@Validated(SysUserDTO.CreateGroup.class) @RequestBody SysUserDTO dto) {
    return userService.createUser(dto.toEntity()) > 0 ? R.ok() : R.fail();
}

// 修改时使用 UpdateGroup 校验（不校验密码）
@PutMapping
public R<Void> edit(@Validated(SysUserDTO.UpdateGroup.class) @RequestBody SysUserDTO dto) {
    return userService.updateUser(dto.toEntity()) > 0 ? R.ok() : R.fail();
}
```

### 7.2 用户 CRUD 接口

**Service 层核心实现**:

```java
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements ISysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 创建用户
    @Override
    @Transactional
    public int createUser(SysUser user) {
        // 检查用户名是否已存在
        if (count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, user.getUserName())) > 0) {
            throw new ServiceException("用户名'" + user.getUserName() + "'已存在");
        }
        // 密码 BCrypt 加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(user) ? 1 : 0;
    }

    // 更新用户
    @Override
    @Transactional
    public int updateUser(SysUser user) {
        SysUser existing = getById(user.getUserId());
        if (existing == null) {
            throw new ServiceException("用户不存在");
        }
        return updateById(user) ? 1 : 0;
    }

    // 批量删除（逻辑删除）
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            if (Constants.SUPER_ADMIN_USER_ID.equals(userId)) {
                throw new ServiceException("不允许删除超级管理员");
            }
        }
        return removeByIds(Arrays.asList(userIds)) ? userIds.length : 0;
    }
}
```

### 7.3 密码加密存储

**BCrypt 加密原理**:

```
用户输入密码: "admin123"
        │
        ▼
BCrypt 加密（每次结果不同，因为包含随机盐值）
        │
        ├→ "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"
        └→ "$2a$10$xxxxx..."（同一个密码，不同的哈希值）
        │
        ▼
存储到数据库的 password 字段
```

**代码实现**:

```java
// 注入密码编码器
@Autowired
private PasswordEncoder passwordEncoder;

// 注册/创建用户时：加密
user.setPassword(passwordEncoder.encode(rawPassword));

// 登录时：验证
boolean matches = passwordEncoder.matches(inputPassword, databaseHashedPassword);
```

### 7.4 逻辑删除

本项目所有核心表都使用**逻辑删除**，不物理删除数据：

```java
@TableLogic(value = "0", delval = "2")
private String delFlag;
// value = "0" → 未删除的记录
// delval = "2" → 删除后的值
```

**效果**:

```java
// 调用 removeById(userId) 时，MyBatis-Plus 自动生成：
// UPDATE sys_user SET del_flag = '2' WHERE user_id = ? AND del_flag = '0'

// 调用 getById(userId) 时，MyBatis-Plus 自动生成：
// SELECT * FROM sys_user WHERE user_id = ? AND del_flag = '0'
```

> **重要**: `del_flag` 列必须存在于数据库表中，否则会报 `Unknown column 'del_flag'` 错误。

---

## 第八章 角色与菜单管理

### 8.1 角色管理

**SysRoleDTO** — 角色请求体（包含菜单关联）：

```java
@Data
public class SysRoleDTO {
    private Long roleId;

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 20)
    private String roleName;

    @NotBlank(message = "权限字符不能为空")
    @Size(min = 2, max = 20)
    private String roleKey;

    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    private String dataScope;
    private String status;
    private Long[] menuIds;   // 关联的菜单ID数组
    private String remark;

    public SysRole toEntity() {
        SysRole role = new SysRole();
        role.setRoleId(this.roleId);
        role.setRoleName(this.roleName);
        role.setRoleKey(this.roleKey);
        role.setRoleSort(this.roleSort);
        role.setDataScope(this.dataScope);
        role.setStatus(this.status);
        role.setRemark(this.remark);
        return role;
    }
}
```

**角色创建（含菜单关联）**:

```java
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements ISysRoleService {

    @Override
    @Transactional
    public int createRole(SysRole role, Long[] menuIds) {
        // 1. 保存角色
        save(role);
        // 2. 保存角色-菜单关联
        if (menuIds != null && menuIds.length > 0) {
            for (Long menuId : menuIds) {
                baseMapper.insertRoleMenu(role.getRoleId(), menuId);
            }
        }
        return 1;
    }
}
```

### 8.2 菜单管理

**菜单类型说明**:

| 类型 | 说明 | 必填字段 | 示例 |
|------|------|----------|------|
| M（目录） | 一级菜单容器 | menuName, path | 系统管理 |
| C（菜单） | 页面路由 | menuName, path, component | 用户管理 |
| F（按钮） | 操作权限 | menuName, perms | 用户新增 |

**菜单树构建**（O(n) 算法）:

```java
@Override
public List<MenuTreeVO> getMenuTreeVO() {
    List<SysMenu> menus = baseMapper.selectMenuList(new SysMenu());
    return buildMenuTreeVO(menus);
}

private List<MenuTreeVO> buildMenuTreeVO(List<SysMenu> menus) {
    // 按 parentId 分组，O(n) 时间复杂度
    Map<Long, List<SysMenu>> parentMap = menus.stream()
            .collect(Collectors.groupingBy(SysMenu::getParentId));
    // 从根节点（parentId=0）递归构建
    return buildChildren(0L, parentMap);
}

private List<MenuTreeVO> buildChildren(Long parentId,
        Map<Long, List<SysMenu>> parentMap) {
    List<SysMenu> children = parentMap.getOrDefault(parentId, Collections.emptyList());
    return children.stream().map(menu -> {
        MenuTreeVO tree = MenuTreeVO.fromMenu(menu);
        tree.setChildren(buildChildren(menu.getMenuId(), parentMap));
        return tree;
    }).collect(Collectors.toList());
}
```

### 8.3 角色-菜单关联

**数据模型**:

```
sys_role_menu 表：
┌─────────┬─────────┐
│ role_id │ menu_id │
├─────────┼─────────┤
│    1    │    1    │  ← 超级管理员 → 系统管理
│    1    │    2    │  ← 超级管理员 → 用户管理
│    1    │    3    │  ← 超级管理员 → 角色管理
│    2    │    1    │  ← 普通角色 → 系统管理
│    2    │    2    │  ← 普通角色 → 用户管理（仅查看）
└─────────┴─────────┘
```

**获取角色已分配的菜单 ID 列表**（用于前端回显）:

```xml
<select id="selectMenuIdsByRoleId" resultType="Long">
    SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}
</select>
```

---

## 第九章 部门管理与数据权限

### 9.1 部门树形结构

部门使用 `parentId` + `ancestors` 实现树形管理：

```
若依科技 (deptId=100, ancestors="0")
├── 研发部门 (deptId=101, ancestors="0,100")
│   ├── 前端组 (deptId=103, ancestors="0,100,101")
│   └── 后端组 (deptId=104, ancestors="0,100,101")
├── 测试部门 (deptId=102, ancestors="0,100")
└── 运维部门 (deptId=105, ancestors="0,100")
```

**ancestors 的作用**: 快速查询所有子部门

```sql
-- 查询部门 100 及其所有子部门
SELECT * FROM sys_dept WHERE dept_id = 100
   OR ancestors LIKE '0,100%'
```

### 9.2 数据权限设计

角色的 `data_scope` 字段控制数据可见范围：

| 值 | 含义 | SQL 效果 |
|----|------|----------|
| 1 | 全部数据 | 无过滤条件 |
| 2 | 自定义数据 | 只看指定部门的数据 |
| 3 | 本部门数据 | 只看自己部门的数据 |
| 4 | 本部门及以下 | 看自己部门 + 子部门的数据 |
| 5 | 仅本人数据 | 只看自己创建的数据 |

---

## 第十章 常见问题与解决方案

### 10.1 环境与依赖问题

#### 问题 1：JDK 版本不匹配

**现象**:
```
Unsupported class file major version 65
```

**原因**: Spring Boot 3.x 要求 JDK 17+，使用了 JDK 8 或 11 编译。

**解决**:
```bash
# 检查 Java 版本
java -version

# 确保使用 JDK 17+
# 在 IDEA 中：File → Project Structure → Project SDK → 选择 JDK 17
```

#### 问题 2：Maven 依赖下载失败

**现象**:
```
Could not resolve dependencies for project
```

**解决**:
```bash
# 1. 使用阿里云镜像（在 settings.xml 中配置）
<mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/central</url>
</mirror>

# 2. 清理本地缓存重新下载
mvn clean install -U
```

#### 问题 3：MyBatis-Plus 版本冲突

**现象**:
```
NoSuchMethodError: com.baomidou.mybatisplus.core.MybatisConfiguration
```

**原因**: `mybatis-plus-spring-boot3-starter` 和 `mybatis-plus-boot-starter` 不能混用。

**解决**: Spring Boot 3.x 必须使用 `mybatis-plus-spring-boot3-starter`：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

### 10.2 Spring Security 问题

#### 问题 4：@PreAuthorize 不生效

**现象**: 不带 Token 或权限不足时，接口不返回 403，而是返回 401 或空 body。

**原因**: 未启用方法级安全配置。

**解决**: 确保 SecurityConfig 上有 `@EnableMethodSecurity`：

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity    // ← 必须加这个注解
public class SecurityConfig { ... }
```

#### 问题 5：401/403 返回空 body

**现象**: APIFox 测试返回 HTTP 401/403，但响应体为空。

**原因**: Spring Security 默认的 `AuthenticationEntryPoint` 和 `AccessDeniedHandler` 不返回 JSON。

**解决**: 配置自定义处理器（已在 SecurityConfig 中注册）：

```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint(authenticationEntryPoint)
    .accessDeniedHandler(accessDeniedHandler)
)
```

#### 问题 6：permitAll() 的接口仍返回 401

**现象**: 登录接口（已配置 permitAll）仍然返回 401。

**原因**: SecurityConfig 中的 `requestMatchers` 路径与实际 Controller 路径不匹配。

**解决**: 检查路径是否完全匹配：

```java
// 正确：匹配 AuthController 的完整路径
.requestMatchers("/auth/login", "/auth/logout", "/auth/register").permitAll()

// 错误：路径拼写错误
.requestMatchers("/api/auth/login").permitAll()  // 如果 Controller 没有 /api 前缀
```

#### 问题 7：静态资源被拦截

**现象**: Swagger/Knife4j 页面无法访问，返回 401。

**解决**: 在 SecurityConfig 中添加静态资源放行：

```java
.requestMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
```

#### 问题 8：CORS 预检请求失败

**现象**: 浏览器控制台报 `CORS preflight channel did not succeed`。

**原因**: OPTIONS 预检请求被 Security 拦截。

**解决**: 确保 CORS 配置在 Security 中正确启用：

```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

### 10.3 JWT 与 Token 问题

#### 问题 9：Token 生成报错 SECRET_KEY_BYTE_ARRAY

**现象**:
```
The signing key's size is not secure enough for HS384
```

**原因**: JWT 密钥长度不足。HS384 至少需要 43 字节（Base64 编码后）。

**解决**: 确保 `application-dev.yml` 中的密钥足够长：

```yaml
jwt:
  secret: DevSecretKeyForJWTTokenGenerationMustBeLongEnough123456  # 至少43字符
```

#### 问题 10：Token 过期后前端未处理

**现象**: Token 过期后，接口返回 401，但前端页面无反应。

**解决**: 前端需要在响应拦截器中处理 401：

```javascript
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')  // 跳转登录页
    }
    return Promise.reject(error)
  }
)
```

#### 问题 11：Redis 连接失败导致 Token 无法验证

**现象**:
```
Unable to connect to Redis
```

**解决**: 检查 Redis 服务是否启动，密码是否正确：

```bash
# 测试 Redis 连接
redis-cli -h localhost -p 6379 -a root ping
# 返回 PONG 表示连接正常
```

### 10.4 MyBatis-Plus 问题

#### 问题 12：Unknown column 'del_flag'

**现象**:
```
### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'del_flag'
```

**原因**: 实体类使用了 `@TableLogic` 注解，但数据库表中没有 `del_flag` 列。

**解决**: 给表添加 `del_flag` 列：

```sql
ALTER TABLE 表名 ADD COLUMN del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志';
```

> **注意**: 所有使用 `@TableLogic` 的表都必须有 `del_flag` 列：sys_user、sys_role、sys_menu、sys_dept。

#### 问题 13：@TableField(exist = false) 忘记标注

**现象**:
```
### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'begin_time'
```

**原因**: 非数据库字段（如查询条件字段）忘记标注 `@TableField(exist = false)`。

**解决**:

```java
@TableField(exist = false)   // ← 必须标注
private String beginTime;

@TableField(exist = false)
private String endTime;
```

#### 问题 14：自动填充不生效

**现象**: `createBy`、`createTime` 等字段插入后为 null。

**原因**: 未配置 `MetaObjectHandler` 或未正确获取当前用户。

**解决**: 检查 `MyMetaObjectHandler` 是否被 Spring 扫描到：

```java
@Component   // 确保是 @Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        // ...
    }
}
```

### 10.5 MySQL 与 Redis 问题

#### 问题 15：MySQL 时区错误

**现象**:
```
The server time zone value '???ú±ê×??±??' is unrecognized
```

**解决**: 在 JDBC URL 中添加时区参数：

```yaml
url: jdbc:mysql://localhost:3306/ry?serverTimezone=GMT%2B8
```

#### 问题 16：Redis 密码认证失败

**现象**:
```
NOAUTH Authentication required
```

**解决**: 在 `application-dev.yml` 中配置 Redis 密码：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: root    # 与 redis.conf 中的 requirepass 一致
```

如果 Redis 无密码，删除 `password` 配置项或留空。

### 10.6 跨域（CORS）问题

#### 问题 17：前端跨域请求被拒绝

**现象**:
```
Access to XMLHttpRequest at 'http://localhost:8080/auth/login'
from origin 'http://localhost:5173' has been blocked by CORS policy
```

**解决**: 在 `application-dev.yml` 中配置允许的前端地址：

```yaml
ruoyi:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:5173
```

同时确保 SecurityConfig 中的 CORS 配置正确：

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(allowedOrigins);
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 10.7 日志与调试

#### 问题 18：如何查看请求日志

本项目内置了 `RequestLogFilter`，每个请求都会在终端输出：

```
[请求] POST /auth/login | 状态=200 | 耗时=35ms | 用户=admin | IP=127.0.0.1
```

#### 问题 19：如何开启 SQL 日志

在 `application-dev.yml` 中添加：

```yaml
logging:
  level:
    com.ruoyi.system.mapper: debug   # 打印 SQL 语句
```

输出示例：

```
==>  Preparing: SELECT * FROM sys_user WHERE user_name = ? AND del_flag = '0'
==> Parameters: admin(String)
<==      Total: 1
```

#### 问题 20：如何查看 Spring Security 详细日志

```yaml
logging:
  level:
    org.springframework.security: debug
```

这会输出详细的认证和授权过程，包括：
- 哪些请求被拦截
- Token 解析过程
- 权限校验结果

---

## 第十一章 APIFox 接口测试指南

> **APIFox** 是国产 API 调试/管理工具（类似 Postman），支持接口调试、文档生成、自动化测试。本章使用 APIFox 完成所有接口的联调测试。

### 11.1 创建项目与环境配置

**第一步：新建项目**

1. 打开 APIFox，点击左侧 `+` 新建项目
2. 项目名称：`RuoYi RBAC 权限系统`
3. 项目类型选择 `普通项目`

**第二步：配置环境变量**

1. 点击项目左上角的环境切换按钮 → `管理环境`
2. 新增环境：`本地开发`
3. 添加环境变量：

| 变量名 | 初始值 | 说明 |
|--------|--------|------|
| `base_url` | `http://localhost:8080` | 后端服务地址 |
| `token` | （留空） | 登录后自动填充 |

4. 保存后选择 `本地开发` 环境

**第三步：创建接口目录**

在项目中按模块创建目录结构：

```
📁 RuoYi RBAC 权限系统
├── 📁 认证模块
│   ├── 用户登录
│   ├── 用户注册
│   ├── 用户登出
│   └── 获取用户信息
├── 📁 用户管理
│   ├── 查询用户列表
│   ├── 查询用户详情
│   ├── 新增用户
│   └── ...
├── 📁 角色管理
├── 📁 部门管理
└── 📁 菜单管理
```

### 11.2 配置全局认证

由于大部分接口需要 JWT Token，建议配置全局请求头，避免每个接口手动添加。

**方式一：全局请求头（推荐）**

1. 点击项目设置 → `全局请求头`
2. 添加：`Authorization` = `Bearer {{token}}`
3. 这样所有请求都会自动携带 Token

**方式二：手动添加**

在每个请求的 `Header` 标签页中添加：

| Key | Value |
|-----|-------|
| Authorization | Bearer {{token}} |

> **注意**: `{{token}}` 是环境变量引用语法，APIFox 会自动替换为实际值。

### 11.3 测试登录接口

**登录是第一个需要测试的接口**，成功后会获取到 Token，后续所有接口都依赖它。

**请求配置**:

| 配置项 | 值 |
|--------|-----|
| 请求方法 | POST |
| URL | `{{base_url}}/auth/login` |
| Content-Type | application/json |

**Body（JSON）**:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "userName": "admin",
    "nickName": "超级管理员",
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "loginTime": 1718000000000,
    "expireTime": 1718086400000,
    "ip": "127.0.0.1",
    "address": "127.0.0.1",
    "roles": ["admin"],
    "permissions": ["system:user:list", "system:user:add", "..."]
  }
}
```

**提取 Token 到环境变量**（自动化关键步骤）:

1. 点击请求的 `后置操作` 标签页
2. 添加 `提取变量` 操作
3. 配置：
   - 变量名：`token`
   - 提取方式：`JSON Body`
   - JSONPath：`$.data.token`
4. 保存后，每次执行登录请求，Token 会自动写入环境变量

**测试异常场景**:

| 场景 | 请求 Body | 预期响应 |
|------|-----------|----------|
| 密码错误 | `{"username":"admin","password":"wrong"}` | `{"code":401,"msg":"用户不存在或密码错误"}` |
| 用户不存在 | `{"username":"nonexist","password":"123"}` | `{"code":401,"msg":"用户不存在或密码错误"}` |
| 用户名为空 | `{"username":"","password":"123"}` | `{"code":400,"msg":"用户名不能为空"}` |
| 密码为空 | `{"username":"admin","password":""}` | `{"code":400,"msg":"密码不能为空"}` |

### 11.4 测试需认证的接口

登录成功后（Token 已存入环境变量），测试其他接口。

**示例：获取当前用户信息**

| 配置项 | 值 |
|--------|-----|
| 请求方法 | GET |
| URL | `{{base_url}}/auth/info` |
| Header | Authorization: Bearer {{token}}（全局已配置则无需手动加） |

**成功响应**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "user": {
      "userId": 1,
      "deptId": 100,
      "userName": "admin",
      "nickName": "超级管理员",
      "email": "admin@ruoyi.com",
      "phonenumber": "15888888888",
      "sex": "0",
      "status": "0",
      "createTime": "2024-01-01 00:00:00",
      "roles": [...]
    },
    "roles": ["admin"],
    "permissions": ["system:user:list", "..."]
  }
}
```

**示例：分页查询用户列表**

| 配置项 | 值 |
|--------|-----|
| 请求方法 | GET |
| URL | `{{base_url}}/system/user/list` |
| Query 参数 | `pageNum=1&pageSize=10&status=0` |

**示例：新增用户**

| 配置项 | 值 |
|--------|-----|
| 请求方法 | POST |
| URL | `{{base_url}}/system/user` |
| Body | 见下方 JSON |

```json
{
  "userName": "testuser",
  "password": "123456",
  "nickName": "测试用户",
  "email": "test@example.com",
  "phonenumber": "13800000001",
  "sex": "0",
  "status": "0"
}
```

**示例：修改用户**

| 配置项 | 值 |
|--------|-----|
| 请求方法 | PUT |
| URL | `{{base_url}}/system/user` |
| Body | 见下方 JSON |

```json
{
  "userId": 3,
  "nickName": "测试用户改名",
  "email": "new@example.com"
}
```

### 11.5 测试权限控制（403 场景）

验证权限控制是否生效，需要使用**普通角色**的账号登录。

**第一步：注册一个普通用户**

```
POST {{base_url}}/auth/register
Body: {"username":"test","password":"123456","nickname":"普通用户"}
```

**第二步：用普通用户登录**

```
POST {{base_url}}/auth/login
Body: {"username":"test","password":"123456"}
```

**第三步：将返回的 Token 替换到环境变量中**（手动或用后置操作提取）

**第四步：访问需要 admin 权限的接口**

```
DELETE {{base_url}}/system/user/3
```

**预期响应**:

```json
{
  "code": 403,
  "msg": "权限不足",
  "data": null
}
```

**第五步：恢复 admin Token**（重新执行 admin 登录请求即可）

### 11.6 批量测试所有接口

APIFox 支持 `自动化测试` 功能，可以批量执行所有接口。

**创建测试套件**:

1. 点击左侧 `自动化测试` → `测试套件`
2. 新建套件：`RBAC 接口全量测试`
3. 按顺序添加测试用例：

| 顺序 | 接口 | 预期状态码 | 说明 |
|------|------|------------|------|
| 1 | POST /auth/login | 200 | 登录获取 Token |
| 2 | GET /auth/info | 200 | 获取用户信息 |
| 3 | GET /system/user/list | 200 | 查询用户列表 |
| 4 | POST /system/user | 200 | 新增用户 |
| 5 | PUT /system/user | 200 | 修改用户 |
| 6 | DELETE /system/user/{新用户ID} | 200 | 删除用户 |
| 7 | GET /system/role/list | 200 | 查询角色列表 |
| 8 | GET /system/dept/list | 200 | 查询部门列表 |
| 9 | GET /system/menu/list | 200 | 查询菜单列表 |
| 10 | GET /system/menu/treeselect | 200 | 获取菜单树 |
| 11 | POST /auth/logout | 200 | 登出 |

**运行测试**: 点击 `运行` 按钮，APIFox 会按顺序执行所有接口并显示结果。

### 11.7 APIFox 常见问题

#### 问题 1：请求返回 401，即使已配置 Token

**排查步骤**:
1. 检查环境变量 `token` 是否有值（环境变量面板查看）
2. 检查 Header 中 `Authorization` 的值是否为 `Bearer xxx`（注意 Bearer 后有空格）
3. 检查 Token 是否过期（默认 24 小时），重新登录获取新 Token

#### 问题 2：请求返回 403 权限不足

**原因**: 当前登录用户的角色没有对应接口的权限。

**解决**: 使用超级管理员（admin）账号登录，或在数据库中给当前用户的角色分配对应菜单权限。

#### 问题 3：请求返回 400 参数错误

**排查步骤**:
1. 检查 `Content-Type` 是否为 `application/json`
2. 检查 JSON 格式是否正确（无多余逗号、引号匹配）
3. 检查必填字段是否遗漏（参考接口文档中的校验规则）

#### 问题 4：请求返回 500 服务器错误

**排查步骤**:
1. 查看后端终端日志，找到具体异常信息
2. 检查数据库是否正常连接
3. 检查 Redis 是否正常运行
4. 检查表结构是否完整（特别是 `del_flag` 列）

#### 问题 5：GET 请求的 Query 参数不生效

**解决**: 确保参数在 APIFox 的 `Params` 标签页中填写，而不是在 Body 中。GET 请求的参数会自动拼接到 URL 后面。

#### 问题 6：PUT/DELETE 请求返回 405 Method Not Allowed

**原因**: 请求方法选择错误，或 URL 路径不正确。

**解决**: 检查请求方法是否与接口文档一致，URL 是否完整（包含 `{{base_url}}`）。

---

## 附录：项目完整接口清单

| 模块 | 方法 | URL | 权限标识 | 说明 |
|------|------|-----|----------|------|
| 认证 | POST | /auth/login | 公开 | 用户登录 |
| 认证 | POST | /auth/register | 公开 | 用户注册 |
| 认证 | POST | /auth/logout | 公开 | 用户登出 |
| 认证 | GET | /auth/info | 需认证 | 获取当前用户信息 |
| 用户 | GET | /system/user/list | system:user:list | 分页查询用户 |
| 用户 | GET | /system/user/{userId} | system:user:query | 查询用户详情 |
| 用户 | POST | /system/user | system:user:add | 新增用户 |
| 用户 | PUT | /system/user | system:user:edit | 修改用户 |
| 用户 | DELETE | /system/user/{userIds} | system:user:remove | 批量删除用户 |
| 用户 | PUT | /system/user/resetPwd | system:user:resetPwd | 重置密码 |
| 用户 | PUT | /system/user/changeStatus | system:user:edit | 切换状态 |
| 用户 | GET | /system/user/roles/{userId} | system:user:query | 获取用户角色 |
| 用户 | PUT | /system/user/roles | system:user:edit | 分配用户角色 |
| 角色 | GET | /system/role/list | system:role:list | 分页查询角色 |
| 角色 | GET | /system/role/{roleId} | system:role:query | 查询角色详情 |
| 角色 | POST | /system/role | system:role:add | 新增角色 |
| 角色 | PUT | /system/role | system:role:edit | 修改角色 |
| 角色 | DELETE | /system/role/{roleIds} | system:role:remove | 批量删除角色 |
| 部门 | GET | /system/dept/list | system:dept:list | 查询部门列表 |
| 部门 | GET | /system/dept/{deptId} | system:dept:query | 查询部门详情 |
| 部门 | POST | /system/dept | system:dept:add | 新增部门 |
| 部门 | PUT | /system/dept | system:dept:edit | 修改部门 |
| 部门 | DELETE | /system/dept/{deptId} | system:dept:remove | 删除部门 |
| 菜单 | GET | /system/menu/list | system:menu:list | 查询菜单列表 |
| 菜单 | GET | /system/menu/{menuId} | system:menu:query | 查询菜单详情 |
| 菜单 | POST | /system/menu | system:menu:add | 新增菜单 |
| 菜单 | PUT | /system/menu | system:menu:edit | 修改菜单 |
| 菜单 | DELETE | /system/menu/{menuId} | system:menu:remove | 删除菜单 |
| 菜单 | GET | /system/menu/treeselect | system:menu:list | 获取菜单树 |
| 菜单 | GET | /system/menu/roleMenuTreeVOselect/{roleId} | system:role:query | 获取角色菜单ID |

---

> **文档版本**: 1.0
> **对应代码**: RuoYi RBAC 权限管理系统 main 分支
> **最后更新**: 2026-06-11
