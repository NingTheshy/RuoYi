# RuoYi v1

## 版本定位

v1 是项目的基础 RBAC 权限版本，完成用户注册、登录认证、角色与权限控制，以及基础组织数据管理。

## 已实现功能

- Spring Boot 3 + JDK 17 基础工程
- JWT 登录认证与 Spring Security 权限校验
- 用户、角色、菜单/权限、部门基础管理
- Redis 缓存、统一响应、异常处理和数据权限
- API 文档与配套数据库脚本

## 启动要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 5+

先执行 `sql/schema.sql` 和 `sql/data.sql`，再启动 `ruoyi-admin` 模块。v1 只包含核心权限能力，后续版本的岗位、字典、日志和运维功能尚未加入。
