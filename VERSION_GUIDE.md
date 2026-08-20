# RuoYi 版本切换指南

本文面向开发者，说明各版本包含的功能以及如何使用 Git 切换版本。

## 版本总览

| 版本 | 基准内容 | 主要新增 |
| --- | --- | --- |
| `v1` | 基础 RBAC 权限系统 | 用户注册登录、JWT、角色权限、部门、Redis、数据权限、API 文档 |
| `v2` | v1 + 基础系统数据 | 岗位管理、字典类型与字典数据管理 |
| `v3` | v2 + 系统运营能力 | 系统参数配置、通知公告 |
| `v4` | v3 + 审计能力 | 登录日志、操作日志、操作日志注解与切面 |
| `v5` | v4 + 运维能力 | Quartz 定时任务、任务执行日志、在线用户和强制下线 |
| `v6` | v5 + 开发运维工具（当前最新） | 代码生成器、数据库表结构同步、代码预览、CPU/内存/磁盘/JVM 服务器监控 |

每个版本的 Tag 都固定在对应版本的代码快照上。`main` 分支是当前开发主线，当前指向 v6 的代码并额外移除了仓库级编辑器/助手配置目录；历史 Tag 中的版本文件保持原样。

## 查看远程版本

首次使用或需要刷新版本列表时执行：

```bash
git fetch origin --tags
git tag --list --sort=version:refname
```

## 切换到指定版本

直接切换到某个 Tag（适合查看、运行或定位历史代码）：

```bash
git switch --detach v3
```

也可以使用旧版 Git 命令：

```bash
git checkout v3
```

把 `v3` 替换为 `v1`、`v2`、`v4`、`v5` 或 `v6` 即可切换到其他版本。切换 Tag 后会处于 detached HEAD 状态，此时不要直接在该状态下提交长期开发代码。

确认当前版本：

```bash
git describe --tags --always
git status
```

## 基于版本开始开发

如果需要在某个版本上继续开发，请从 Tag 创建开发分支：

```bash
git switch -c feature/from-v3 v3
```

开发完成后正常提交即可：

```bash
git add .
git commit -m "feat: describe your change"
git push -u origin feature/from-v3
```

## 返回主分支

```bash
git switch main
git pull origin main
```

## 版本选择建议

- 需要最小可用的权限系统：选择 `v1`。
- 需要岗位和字典基础数据：选择 `v2`。
- 需要参数配置和公告：选择 `v3`。
- 需要审计登录、操作行为：选择 `v4`。
- 需要定时任务和在线用户管理：选择 `v5`。
- 需要完整的当前功能、代码生成和服务器监控：选择 `v6` 或 `main`。

切换版本后，请使用该版本对应的 `sql/schema.sql` 和 `sql/data.sql` 初始化数据库，不要混用不同版本的数据库脚本。
