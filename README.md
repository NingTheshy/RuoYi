# RuoYi 权限管理系统

## 项目简介

RuoYi 是一个基于 Spring Boot 3 的权限管理系统，采用 RBAC（基于角色的访问控制）模型。本项目经过重构，使用现代化的技术栈和最佳实践。

## 技术栈

- **框架**: Spring Boot 3.2.5
- **Java 版本**: 17+
- **持久层**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0+
- **缓存**: Redis 5.0+
- **认证**: JWT
- **文档**: Knife4j 4.5.0 (OpenAPI 3)
- **对象映射**: MapStruct 1.5.5

## 项目结构

```
RuoYi
├── ruoyi-admin          # 启动模块（API 入口）
├── ruoyi-common         # 公共模块（工具类、配置、安全等）
│   ├── core             # 核心常量、异常、统一响应
│   ├── datascope        # 数据权限
│   ├── mybatis          # MyBatis-Plus 配置
│   ├── redis            # Redis 配置
│   └── security         # 安全认证
├── ruoyi-system         # 系统模块（业务逻辑）
│   ├── domain           # 实体、DTO、VO
│   ├── mapper           # 数据访问层
│   └── service          # 业务服务层
└── sql                  # 数据库脚本
```

## 主要功能

- 用户管理
- 角色管理
- 菜单管理
- 部门管理
- JWT 认证授权
- 数据权限控制
- API 文档（Knife4j）

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 2. 初始化数据库

```bash
# 执行 sql 目录下的脚本
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/data.sql
```

### 3. 修改配置

编辑 `ruoyi-admin/src/main/resources/application-dev.yml`，配置数据库和 Redis 连接信息。

### 4. 启动项目

```bash
mvn clean install
cd ruoyi-admin
mvn spring-boot:run
```

### 5. 访问

- 项目地址: http://localhost:8080
- API 文档: http://localhost:8080/doc.html

## 版本管理

本项目支持通过 Git 命令进行版本切换：

```bash
# 查看所有版本
git log --oneline

# 切换到指定版本
git checkout <commit-hash>

# 查看分支
git branch -a

# 切换分支
git checkout <branch-name>
```

## 许可证

MIT License
