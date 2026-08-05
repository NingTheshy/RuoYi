# RuoYi API 接口文档

> Base URL: `http://localhost:8080`
> Content-Type: `application/json`
> 认证方式: `Authorization: Bearer {token}`（除登录/注册外所有接口）
> 时间约定: 数据库字段使用 `DATETIME`，Java 使用 `LocalDateTime`，接口收发格式统一为 `yyyy-MM-dd HH:mm:ss`

---

## 统一响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

失败时：

```json
{
  "code": 401,
  "msg": "未登录或Token已过期",
  "data": null
}
```

失败响应现在会同时返回真实 HTTP 状态码，不再是“HTTP 200 + body.code 表示失败”的旧口径。

| 场景 | HTTP 状态码 | body.code | body.msg 示例 |
|------|-------------|-----------|----------------|
| 参数校验失败 | 400 | 400 | `userName: 用户名不能为空` |
| 业务规则拦截 | 400 | 400 | `不允许删除超级管理员` |
| 未登录 / Token 无效或过期 | 401 | 401 | `未登录或Token已过期` |
| 登录认证失败 | 401 | 401 | `密码错误` / `用户已被停用` |
| 权限不足 | 403 | 403 | `权限不足，无法访问` |
| 资源不存在 | 404 | 404 | `请求的资源不存在` |
| 请求方法不支持 | 405 | 405 | `不支持的请求方法: GET` |
| 系统内部错误 | 500 | 500 | `系统内部错误` |

---

## 联调验收记录

以下结果基于当前代码实际启动后端并发起真实 HTTP 请求得到，可作为前后端联调时的验收基线：

| 场景 | 请求示例 | 预期 HTTP 状态码 | 预期响应体关键信息 |
|------|----------|------------------|--------------------|
| 未登录访问受保护接口 | `GET /auth/info` | 401 | `code=401`, `msg=未登录或Token已过期` |
| 登录参数校验失败 | `POST /auth/login`，`username=""` | 400 | `code=400`，消息包含字段校验失败原因 |
| 普通用户越权删除用户 | 普通角色账号调用 `DELETE /system/user/2` | 403 | `code=403`, `msg=权限不足，无法访问` |
| 已登录访问不存在用户 | 管理员调用 `GET /system/user/999999` | 404 | `code=404`, `msg=用户不存在` |
| 已登录访问不存在路由 | 管理员调用 `GET /system/not-exists` | 404 | `code=404`, `msg=请求的资源不存在` |
| 业务规则拦截 | 管理员调用 `DELETE /system/user/1` | 400 | `code=400`, `msg=不允许删除超级管理员` |
| 请求方法不支持 | 管理员调用 `GET /auth/login` | 405 | `code=405`, `msg=不支持的请求方法: GET` |

### 401 响应体说明

- 后端在 `401` 场景下会返回 JSON 响应体，不是只返回状态码。
- 不同调试工具对 `401` body 的展示不完全一致：
- `curl -i` 通常能直接看到响应头和响应体。
- 某些 `Invoke-WebRequest` 场景下可能只看到状态码，看不到 body。
- 联调时应优先以 HTTP 状态码为准，工具能读取 body 时再展示 `msg`。

---

## 一、认证模块 `/auth`

### 1.1 登录

```
POST /auth/login
```

**请求体 LoginReq：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| username | String | ✅ | `@NotBlank` |
| password | String | ✅ | `@NotBlank` |

**成功响应：** `R<LoginResp>`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "userName": "admin",
    "nickName": "若依管理员",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "loginTime": "2026-06-15 16:30:00",
    "expireTime": "2026-06-16 16:30:00",
    "ip": "127.0.0.1",
    "address": "127.0.0.1",
    "permissions": ["system:user:list", "system:user:add", "system:user:edit", "system:user:remove", "..."],
    "roles": ["admin"]
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |
| userName | String | 用户名 |
| nickName | String | 昵称 |
| token | String | JWT Token（前端存 localStorage/sessionStorage） |
| loginTime | String | 登录时间 `yyyy-MM-dd HH:mm:ss` |
| expireTime | String | 过期时间 `yyyy-MM-dd HH:mm:ss` |
| ip | String | IP 地址 |
| address | String | 地址（当前存储为 IP） |
| permissions | String[] | 权限标识列表，admin 拥有全部权限 |
| roles | String[] | 角色标识列表 |

---

### 1.2 注册

```
POST /auth/register
```

**请求体 RegisterReq：**

```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phonenumber": "13800138000"
}
```

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| username | String | ✅ | 2-30 字符 |
| password | String | ✅ | 6-20 字符 |
| nickname | String | ✅ | 最大 30 字符 |
| email | String | ❌ | `@Email` + 最大 50 字符 |
| phonenumber | String | ❌ | 最大 11 字符 |

**成功响应：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

> 注册成功后 `data` 为 `null`，前端收到 code=200 即表示注册完成，可引导用户跳转登录页。
> 注册用户自动获得「普通角色」（`common`），拥有基础查看权限。

**失败响应示例：**

```json
// 用户名已存在
{"code": 400, "msg": "用户名'zhangsan'已存在", "data": null}

// 参数校验失败
{"code": 400, "msg": "username: 用户名不能为空; password: 密码长度必须在 6 到 20 个字符之间", "data": null}
```

| 场景 | HTTP 状态码 | body.msg |
|------|-------------|----------|
| 注册成功 | 200 | `操作成功` |
| 用户名已存在 | 400 | `用户名'xxx'已存在` |
| 必填字段缺失 | 400 | 字段级校验错误（如 `username: 用户名不能为空`） |
| 密码太短 | 400 | `password: 密码长度必须在 6-20 个字符之间` |

---

### 1.3 登出

```
POST /auth/logout
```

**请求头：** 可选 `Authorization: Bearer {token}`

**请求体：** 无

**成功响应：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

> Token 从 Redis 中删除后立即失效，无需等待过期时间。

---

### 1.4 获取当前用户信息

```
GET /auth/info
```

**请求头：** `Authorization: Bearer {token}`

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "user": {
      "userId": 1,
      "deptId": 103,
      "userName": "admin",
      "nickName": "若依管理员",
      "email": "ry@163.com",
      "phonenumber": "15888888888",
      "sex": "0",
      "avatar": "",
      "status": "0",
      "remark": "管理员",
      "createTime": "2026-06-15 19:00:00"
    },
    "roles": ["admin"],
    "permissions": ["system:user:list", "system:user:add", "system:user:edit", "..."]
  }
}
```

---

## 二、用户管理 `/system/user`

> 需要 `Authorization` 头 + 对应权限标识

### 2.1 用户列表（分页）

```
GET /system/user/list?pageNum=1&pageSize=10&userName=&status=0
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | ❌ | 页码，默认 1 |
| pageSize | Integer | ❌ | 每页条数，默认 10 |
| userName | String | ❌ | 用户名，模糊匹配 |
| nickName | String | ❌ | 昵称，模糊匹配 |
| phonenumber | String | ❌ | 手机号，模糊匹配 |
| status | String | ❌ | `0`=正常, `1`=停用 |
| deptId | Long | ❌ | 部门ID（精确匹配） |
| beginTime | String | ❌ | 创建开始时间 `yyyy-MM-dd HH:mm:ss` |
| endTime | String | ❌ | 创建结束时间 `yyyy-MM-dd HH:mm:ss` |

**权限：** `system:user:list`

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "total": 50,
    "rows": [
      {
        "userId": 1,
        "deptId": 103,
        "userName": "admin",
        "nickName": "若依管理员",
        "email": "ry@163.com",
        "phonenumber": "15888888888",
        "sex": "0",
        "avatar": "",
        "status": "0",
        "remark": "管理员",
        "createTime": "2024-01-01 00:00:00"
      }
    ]
  }
}
```

**SysUserResp 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |
| deptId | Long | 部门ID |
| userName | String | 用户名 |
| nickName | String | 昵称 |
| email | String | 邮箱 |
| phonenumber | String | 手机号 |
| sex | String | `0`=男, `1`=女, `2`=未知 |
| avatar | String | 头像路径 |
| status | String | `0`=正常, `1`=停用 |
| remark | String | 备注 |
| createTime | String | 创建时间 `yyyy-MM-dd HH:mm:ss` |
| roles | SysRoleResp[] | 角色列表字段；当前列表/详情接口默认不展开，通常为 `null` 或空 |

**SysRoleResp 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| roleId | Long | 角色ID |
| roleName | String | 角色名 |
| roleKey | String | 角色标识 |
| roleSort | Integer | 排序 |
| dataScope | String | 数据范围 1-5 |
| menuCheckStrictly | Integer | 菜单树是否关联 0/1 |
| deptCheckStrictly | Integer | 部门树是否关联 0/1 |
| status | String | 状态 |
| remark | String | 备注 |
| createTime | String | 创建时间 |

---

### 2.2 获取用户详情

```
GET /system/user/{userId}
```

**权限：** `system:user:query`

**响应：** `R<SysUserResp>` 同上

---

### 2.3 新增用户

```
POST /system/user
```

**权限：** `system:user:add`

**请求体 SysUserCreateReq：**

```json
{
  "deptId": 103,
  "userName": "zhangsan",
  "nickName": "张三",
  "email": "zhangsan@example.com",
  "phonenumber": "13800138000",
  "sex": "0",
  "password": "123456",
  "status": "0",
  "remark": "普通用户"
}
```

| 字段 | 类型 | 必填(Create) | 校验 |
|------|------|-------------|------|
| userId | Long | ❌ | 新增不传 |
| deptId | Long | ❌ | 部门ID |
| userName | String | ✅ | 2-30 字符 |
| nickName | String | ✅ | 最大 30 字符 |
| email | String | ❌ | `@Email` 格式 |
| phonenumber | String | ❌ | 最大 11 字符 |
| sex | String | ❌ | `0`/`1`/`2` |
| avatar | String | ❌ | 头像路径 |
| password | String | ✅ | 6-20 字符，BCrypt 加密存储 |
| status | String | ❌ | `0`/`1` |
| remark | String | ❌ | 备注 |

**响应：** `R<Void>`

---

### 2.4 修改用户

```
PUT /system/user
```

**权限：** `system:user:edit`

**请求体 SysUserUpdateReq：**

```json
{
  "userId": 2,
  "deptId": 103,
  "userName": "zhangsan",
  "nickName": "张三改名",
  "email": "zhangsan@example.com",
  "phonenumber": "13800138000",
  "sex": "0",
  "status": "0",
  "remark": "修改备注"
}
```

| 字段 | 类型 | 必填(Update) | 说明 |
|------|------|-------------|------|
| userId | Long | ✅ | 必传，标识要修改的用户 |
| 其余字段 | | ❌ | 同新增，password 字段会被忽略 |

> ⚠️ 修改接口不处理密码，如需重置密码请用 `resetPwd`

**响应：** `R<Void>`

---

### 2.5 删除用户

```
DELETE /system/user/{userIds}
```

**权限：** `system:user:remove`

| 参数 | 类型 | 说明 |
|------|------|------|
| userIds | Long[] | 路径参数，多选用逗号分隔：`/system/user/2,3,4` |

> ⚠️ admin 用户（ID=1）不可删除

**响应：** `R<Void>`

---

### 2.6 重置密码

```
PUT /system/user/resetPwd
```

**权限：** `system:user:resetPwd`

**请求体：** `SysUserResetPasswordReq`

```json
{
  "userId": 2,
  "password": "newPass123"
}
```

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| userId | Long | ✅ | `@NotNull` |
| password | String | ✅ | `@NotBlank` + 6-20 字符 |

**响应：** `R<Void>`

---

### 2.7 修改用户状态

```
PUT /system/user/changeStatus
```

**权限：** `system:user:edit`

**请求体：** `SysUserChangeStatusReq`

```json
{
  "userId": 2,
  "status": "1"
}
```

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| userId | Long | ✅ | `@NotNull` |
| status | String | ✅ | `@Pattern(0|1)` |

> ⚠️ admin 用户（ID=1）状态不可修改

**响应：** `R<Void>`

---

### 2.8 获取用户角色ID列表

```
GET /system/user/roles/{userId}
```

**权限：** `system:user:query`

**响应：**

```json
{
  "code": 200,
  "data": [1, 2, 5]
}
```

> 用于角色分配弹窗的回显

---

### 2.9 分配用户角色

```
PUT /system/user/roles
```

**权限：** `system:user:edit`

**请求体：** `SysUserAssignRolesReq`

```json
{
  "userId": 2,
  "roleIds": [1, 2, 5]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| roleIds | Long[] | ❌ | 角色ID数组；传空数组可清空角色 |

**响应：** `R<Void>`

---

## 三、部门管理 `/system/dept`

### 3.1 部门列表

```
GET /system/dept/list?deptName=研发&status=0
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deptName | String | ❌ | 部门名称，模糊匹配 |
| status | String | ❌ | `0`=正常, `1`=停用 |

**权限：** `system:dept:list`

**成功响应：**

```json
{
  "code": 200,
  "data": [
    {
      "deptId": 100,
      "parentId": 0,
      "ancestors": "0",
      "deptName": "若依科技",
      "orderNum": 0,
      "leader": "若依",
      "phone": "15888888888",
      "email": "ry@qq.com",
      "status": "0",
      "remark": null
    },
    {
      "deptId": 103,
      "parentId": 101,
      "ancestors": "0,100,101",
      "deptName": "研发部门",
      "orderNum": 1,
      "leader": "若依",
      "phone": "15888888888",
      "email": "ry@qq.com",
      "status": "0",
      "remark": null
    }
  ]
}
```

**SysDeptResp 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| deptId | Long | 部门ID |
| parentId | Long | 父部门ID，顶级为 0 |
| ancestors | String | 祖级列表，如 `"0,100,103"` |
| deptName | String | 部门名称 |
| orderNum | Integer | 排序号 |
| leader | String | 负责人 |
| phone | String | 电话 |
| email | String | 邮箱 |
| status | String | `0`=正常, `1`=停用 |
| remark | String | 备注 |
| children | SysDeptResp[] | 子部门列表字段；当前 `/system/dept/list` 默认返回平铺结果，不保证已填充 |

> `/system/dept/list` 当前返回按 `parentId + orderNum` 排序的平铺列表，前端如需树形请自行组装。

---

### 3.2 获取部门详情

```
GET /system/dept/{deptId}
```

**权限：** `system:dept:query`

**响应：** `R<SysDeptResp>`（单个，无 children）

---

### 3.3 新增部门

```
POST /system/dept
```

**权限：** `system:dept:add`

**请求体 SysDeptCreateReq：**

```json
{
  "parentId": 100,
  "deptName": "测试部门",
  "orderNum": 3,
  "leader": "李四",
  "phone": "13800138000",
  "email": "lisi@example.com",
  "status": "0"
}
```

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| deptId | Long | ❌ | 新增不传 |
| parentId | Long | ❌ | 父部门ID，顶级传 0 或不传 |
| deptName | String | ✅ | 最大 30 字符 |
| orderNum | Integer | ✅ | 排序号 |
| leader | String | ❌ | 最大 20 字符 |
| phone | String | ❌ | 最大 11 字符 |
| email | String | ❌ | `@Email` 格式 |
| status | String | ❌ | `0`/`1` |
| remark | String | ❌ | 备注 |

**响应：** `R<Void>`

---

### 3.4 修改部门

```
PUT /system/dept
```

**权限：** `system:dept:edit`

**请求体：** SysDeptUpdateReq，必须包含 `deptId`

**响应：** `R<Void>`

---

### 3.5 删除部门

```
DELETE /system/dept/{deptId}
```

**权限：** `system:dept:remove`

> ⚠️ 有子部门或有用户的部门不可删除

**响应：** `R<Void>`

---

## 四、角色管理 `/system/role`

### 4.1 角色列表（分页）

```
GET /system/role/list?pageNum=1&pageSize=10&roleName=&status=0
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | ❌ | 页码，默认 1 |
| pageSize | Integer | ❌ | 每页条数，默认 10 |
| roleName | String | ❌ | 角色名称，模糊匹配 |
| roleKey | String | ❌ | 角色标识，模糊匹配 |
| status | String | ❌ | `0`=正常, `1`=停用 |

**权限：** `system:role:list`

**成功响应：**

```json
{
  "code": 200,
  "data": {
    "total": 3,
    "rows": [
      {
        "roleId": 1,
        "roleName": "超级管理员",
        "roleKey": "admin",
        "roleSort": 1,
        "dataScope": "1",
        "menuCheckStrictly": 1,
        "deptCheckStrictly": 1,
        "status": "0",
        "remark": "超级管理员",
        "createTime": "2024-01-01 00:00:00"
      }
    ]
  }
}
```

---

### 4.2 获取角色详情

```
GET /system/role/{roleId}
```

**权限：** `system:role:query`

**响应：** `R<SysRoleResp>`

---

### 4.3 新增角色

```
POST /system/role
```

**权限：** `system:role:add`

**请求体 SysRoleCreateReq：**

```json
{
  "roleName": "普通角色",
  "roleKey": "common",
  "roleSort": 2,
  "dataScope": "1",
  "menuCheckStrictly": 1,
  "deptCheckStrictly": 1,
  "status": "0",
  "menuIds": [1, 100, 1001, 1002],
  "remark": "普通角色"
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| roleId | Long | ❌ | | 新增不传 |
| roleName | String | ✅ | 2-30 字符 | 角色名称 |
| roleKey | String | ✅ | 2-100 字符 | 角色标识（唯一） |
| roleSort | Integer | ✅ | | 排序号 |
| dataScope | String | ❌ | | 数据范围：1=全部, 2=自定义, 3=本部门, 4=本部门及以下, 5=仅本人 |
| menuCheckStrictly | Integer | ❌ | | 菜单树父子关联：0=不关联, 1=关联 |
| deptCheckStrictly | Integer | ❌ | | 部门树父子关联：0=不关联, 1=关联 |
| status | String | ✅ | | `0`/`1` |
| menuIds | Long[] | ❌ | | 授权菜单ID数组 |
| remark | String | ❌ | | 备注 |

**响应：** `R<Void>`

---

### 4.4 修改角色

```
PUT /system/role
```

**权限：** `system:role:edit`

**请求体：** SysRoleUpdateReq，必须包含 `roleId`，`menuIds` 会重建关联

**响应：** `R<Void>`

---

### 4.5 删除角色

```
DELETE /system/role/{roleIds}
```

**权限：** `system:role:remove`

| 参数 | 类型 | 说明 |
|------|------|------|
| roleIds | Long[] | 路径参数，多选用逗号分隔 |

> ⚠️ admin 角色（ID=1）不可删除。删除角色同时清除用户-角色关联

**响应：** `R<Void>`

---

## 五、菜单管理 `/system/menu`

### 5.1 菜单列表

```
GET /system/menu/list?menuName=用户&status=0&visible=0
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuName | String | ❌ | 菜单名称，模糊匹配 |
| status | String | ❌ | `0`=正常, `1`=停用 |
| visible | String | ❌ | `0`=显示, `1`=隐藏 |

**权限：** `system:menu:list`

**成功响应：**

```json
{
  "code": 200,
  "data": [
    {
      "menuId": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "orderNum": 1,
      "path": "system",
      "component": null,
      "query": null,
      "isFrame": 1,
      "isCache": 0,
      "menuType": "M",
      "visible": "0",
      "status": "0",
      "perms": "",
      "icon": "system",
      "remark": "系统管理目录",
      "createTime": "2024-01-01 00:00:00"
    },
    {
      "menuId": 100,
      "menuName": "用户管理",
      "parentId": 1,
      "orderNum": 1,
      "path": "user",
      "component": "system/user/index",
      "query": "",
      "isFrame": 1,
      "isCache": 0,
      "menuType": "C",
      "visible": "0",
      "status": "0",
      "perms": "system:user:list",
      "icon": "user",
      "remark": "用户管理菜单",
      "createTime": "2024-01-01 00:00:00"
    }
  ]
}
```

**SysMenuResp 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| menuId | Long | 菜单ID |
| menuName | String | 菜单名称 |
| parentId | Long | 父菜单ID |
| orderNum | Integer | 排序号 |
| path | String | 路由地址 |
| component | String | 组件路径 |
| query | String | 路由参数 |
| isFrame | Integer | `0`=外链, `1`=内部 |
| isCache | Integer | `0`=缓存, `1`=不缓存 |
| menuType | String | `M`=目录, `C`=菜单, `F`=按钮 |
| visible | String | `0`=显示, `1`=隐藏 |
| status | String | `0`=正常, `1`=停用 |
| perms | String | 权限标识，如 `system:user:add` |
| icon | String | 图标 |
| remark | String | 备注 |
| createTime | String | 创建时间 |
| children | SysMenuResp[] | 子菜单列表字段；当前 `/system/menu/list` 默认返回平铺结果，不保证已填充 |

---

### 5.2 获取菜单详情

```
GET /system/menu/{menuId}
```

**权限：** `system:menu:query`

**响应：** `R<SysMenuResp>`（单个）

---

### 5.3 新增菜单

```
POST /system/menu
```

**权限：** `system:menu:add`

**请求体 SysMenuCreateReq：**

```json
{
  "parentId": 1,
  "menuName": "用户管理",
  "orderNum": 1,
  "path": "user",
  "component": "system/user/index",
  "isFrame": 0,
  "isCache": 0,
  "menuType": "C",
  "visible": "0",
  "status": "0",
  "perms": "system:user:list",
  "icon": "user",
  "remark": ""
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuId | Long | ❌ | 新增不传 |
| menuName | String | ✅ | 最大 50 字符 |
| parentId | Long | ❌ | 父菜单ID |
| orderNum | Integer | ✅ | 排序号 |
| path | String | ❌ | 路由地址，最大 200 字符 |
| component | String | ❌ | 组件路径，最大 255 字符 |
| query | String | ❌ | 路由参数 |
| isFrame | Integer | ❌ | `0` 外链 / `1` 内部 |
| isCache | Integer | ❌ | `0` 缓存 / `1` 不缓存 |
| menuType | String | ✅ | `M` 目录 / `C` 菜单 / `F` 按钮 |
| visible | String | ❌ | `0` 显示 / `1` 隐藏 |
| status | String | ❌ | `0` 正常 / `1` 停用 |
| perms | String | ❌ | 权限标识，如 `system:user:add` |
| icon | String | ❌ | 图标名称 |
| remark | String | ❌ | 备注 |

**响应：** `R<Void>`

---

### 5.4 修改菜单

```
PUT /system/menu
```

**权限：** `system:menu:edit`

**请求体：** SysMenuUpdateReq，必须包含 `menuId`

**响应：** `R<Void>`

---

### 5.5 删除菜单

```
DELETE /system/menu/{menuId}
```

**权限：** `system:menu:remove`

> ⚠️ 有子菜单的不可删除

**响应：** `R<Void>`

---

### 5.6 获取菜单树（角色授权用）

```
GET /system/menu/treeselect
```

**权限：** `system:menu:list`

**成功响应：**

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "label": "系统管理",
      "children": [
        {
          "id": 100,
          "label": "用户管理",
          "children": [
            { "id": 1001, "label": "用户查询", "children": [] },
            { "id": 1002, "label": "用户新增", "children": [] }
          ]
        }
      ]
    }
  ]
}
```

**MenuTreeResp 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 菜单ID（对应 SysMenu.menuId） |
| label | String | 菜单名称（对应 SysMenu.menuName） |
| children | MenuTreeResp[] | 子菜单 |

---

### 5.7 获取角色已授权菜单ID

```
GET /system/menu/role-menu-tree-select/{roleId}
```

**权限：** `system:role:query`

**成功响应：**

```json
{
  "code": 200,
  "data": [1, 100, 1001, 1002]
}
```

> 用于角色编辑弹窗中菜单树的回显勾选
> 兼容旧路径：`GET /system/menu/roleMenuTreeVOselect/{roleId}`，建议前端逐步切到新路径。

---

## 六、前端联调要点

### 请求头

所有需认证的接口必须在请求头中携带：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

前端可在 axios 拦截器中统一添加：

```js
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})
```

### 401 处理

当 Token 过期或无效时，后端返回：

```json
{ "code": 401, "msg": "未登录或Token已过期", "data": null }
```

同时 HTTP 状态码也会是 `401`，前端应优先按 HTTP 状态码处理，再读取响应体消息。

部分调试工具在 401 场景下可能默认不展示响应体，但并不代表后端没有返回 JSON；联调时至少要以 `status=401` 为准，能读取 body 时再展示 `msg`。

### 权限控制

前端根据 `/auth/info` 返回的 `permissions` 数组控制按钮/菜单显示：

- `["*:*:*"]` 表示超级管理员，拥有全部权限
- 当前接口直接返回数据库中的权限标识集合，如 `["system:user:list", "system:user:query"]`

### 数据范围

角色的 `dataScope` 字段控制行级数据权限：

- `1` — 全部数据
- `2` — 自定义（需关联 sys_role_dept）
- `3` — 本部门数据
- `4` — 本部门及以下数据
- `5` — 仅本人数据

### 前端部门/菜单树构建

- `/system/dept/list` 当前返回平铺列表，前端按 `parentId` 自行构建树
- `/system/menu/list` 当前返回平铺列表，前端按 `parentId` 自行构建树
- `/system/menu/treeselect` 返回树形结构，可直接用于树组件

### 分页参数

支持分页的接口（用户列表、角色列表）：

- `pageNum` — 页码（默认 1）
- `pageSize` — 每页条数（默认 10）
- 响应中 `data.total` 为总记录数，`data.rows` 为当前页数据
