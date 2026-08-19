# RuoYi v2

## 项目简介

v2 在 v1 核心 RBAC 能力上补充岗位管理与字典管理，完善系统基础数据维护能力。

## 本版本新增

- 岗位（Post）管理及用户岗位关联
- 字典类型、字典数据维护
- 对应的 REST API、权限菜单和 MySQL 初始化数据

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot 3 | 3.2.5 |
| Java | JDK 17+ | 17 |
| 持久层 | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL 8.0+ | 8.0+ |
| 缓存 | Redis 5.0+ | 5.0+ |
| 认证 | Spring Security + JWT (jjwt) | 0.12.5 |
| 接口文档 | Knife4j (OpenAPI 3 Jakarta) | 4.5.0 |
| 对象映射 | MapStruct | 1.5.5.Final |
| 工具 | Lombok、Jackson、Hibernate Validator | - |

## 项目结构

```
RuoYi
├── ruoyi-admin                  # 启动模块（API 入口、配置）
│   ├── config
│   │   ├── MybatisPlusConfig    # MyBatis-Plus 拦截器链（数据权限 → 分页）
│   │   ├── OpenApiConfig        # Knife4j/Swagger 文档配置
│   │   └── OpenApiProperties    # 文档自定义属性
│   └── web
│       ├── auth
│       │   └── AuthController   # 认证接口（登录/注册/登出/获取当前用户）
│       └── system
│           ├── SysUserController    # 用户管理
│           ├── SysRoleController    # 角色管理
│           ├── SysMenuController    # 菜单管理
│           ├── SysDeptController    # 部门管理
│           ├── SysDictTypeController    # 字典类型管理
│           ├── SysDictDataController    # 字典数据管理
│           └── SysPostController    # 岗位管理
├── ruoyi-common                 # 公共模块（工具类、配置、安全等）
│   ├── core                     # 核心常量、异常、统一响应、枚举
│   ├── datascope                # 数据权限注解/切面/上下文
│   ├── mybatis                  # MyBatis-Plus 自动填充、数据权限拦截器
│   ├── redis                    # Redis 配置
│   └── security                 # 安全认证（JWT 过滤器、Token 服务、登录服务）
├── ruoyi-system                 # 系统模块（业务逻辑）
│   ├── config                   # 系统 MyBatis 配置
│   ├── convert                  # MapStruct 对象转换器
│   ├── domain/entity            # 实体类
│   ├── mapper                   # 数据访问层（MyBatis-Plus Mapper）
│   └── service                  # 业务服务层
└── sql                          # 数据库脚本
    ├── schema.sql               # 建表脚本
    └── data.sql                 # 初始化数据（含管理员账号、菜单、角色等）
```

## 主要功能

### 系统管理
- **用户管理** — 用户 CRUD、分页查询、重置密码、状态切换、角色分配
- **角色管理** — 角色 CRUD、分页查询
- **菜单管理** — 菜单 CRUD、菜单树、角色菜单分配
- **部门管理** — 部门 CRUD、树形结构展示
- **字典管理** — 字典类型与字典数据 CRUD、字典数据按类型查询
- **岗位管理** — 岗位 CRUD、分页查询

### 认证与安全
- **JWT 认证** — 基于 Bearer Token 的无状态认证
- **注册/登录/登出** — 用户注册自动分配默认角色，登录获取 Token
- **权限控制** — 方法级 `@PreAuthorize` 注解，细粒度按钮级权限
- **数据权限** — 支持全部/自定义/本部门/本部门及以下/仅本人五种数据范围
- **密码加密** — BCrypt 强哈希算法
- **CORS 跨域** — 可配置允许的跨域来源
- **统一异常处理** — 全局异常拦截，统一 JSON 响应格式

### 接口规范
- **统一响应** — `R<T>` 包装所有响应，前端一致解析
- **分页结果** — `PageResult<T>` 统一分页格式
- **API 文档** — Knife4j 自动生成 OpenAPI 3 文档

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户信息表 |
| `sys_role` | 角色信息表 |
| `sys_menu` | 菜单权限表 |
| `sys_dept` | 部门表 |
| `sys_user_role` | 用户-角色关联表 |
| `sys_role_menu` | 角色-菜单关联表 |
| `sys_role_dept` | 角色-部门关联表（数据权限） |
| `sys_post` | 岗位信息表 |
| `sys_user_post` | 用户-岗位关联表 |
| `sys_dict_type` | 字典类型表 |
| `sys_dict_data` | 字典数据表 |

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 2. 初始化数据库

```bash
# 创建数据库并执行建表脚本
mysql -u root -p < sql/schema.sql
# 导入初始化数据
mysql --default-character-set=utf8mb4 -u root -p < sql/data.sql
```

### 3. 修改配置

编辑 `ruoyi-admin/src/main/resources/application-dev.yml`，配置数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ry?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      password: root

jwt:
  secret: 你的JWT签名密钥（至少256位长度）
```

### 4. 启动项目

```bash
mvn clean install
cd ruoyi-admin
mvn spring-boot:run
```

### 5. 访问

- 项目地址: http://localhost:8080
- API 文档: http://localhost:8080/doc.html

### 6. 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| ryou | admin123 | 普通角色 |

## 许可证

MIT License
