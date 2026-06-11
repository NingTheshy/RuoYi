# RuoYi 后端 API 接口文档

> **Base URL**: `http://localhost:8080`
> **认证方式**: JWT Token（Header: `Authorization: Bearer <token>`）
> **统一响应格式**: `{ "code": 200, "msg": "操作成功", "data": ... }`

---

## 目录

1. [统一响应结构](#1-统一响应结构)
2. [认证模块 /auth](#2-认证模块-auth)
3. [用户管理 /system/user](#3-用户管理-systemuser)
4. [角色管理 /system/role](#4-角色管理-systemrole)
5. [部门管理 /system/dept](#5-部门管理-systemdept)
6. [菜单管理 /system/menu](#6-菜单管理-systemmenu)

---

## 1. 统一响应结构

### R\<T\> 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 状态码：200=成功，401=未认证，403=权限不足，400=参数错误，500=失败 |
| msg | String | 提示消息 |
| data | T | 响应数据（失败时为 null） |

### PageResult\<T\> 分页响应体（data 字段的结构）

| 字段 | 类型 | 说明 |
|------|------|------|
| rows | Array | 当前页数据列表 |
| total | long | 总记录数 |

### 错误响应示例

```json
// 401 未认证（Token 缺失/过期）
{ "code": 401, "msg": "未登录或Token已过期", "data": null }

// 403 权限不足
{ "code": 403, "msg": "权限不足", "data": null }

// 400 参数校验失败
{ "code": 400, "msg": "用户名不能为空", "data": null }

// 500 业务失败
{ "code": 500, "msg": "操作失败", "data": null }
```

---

## 2. 认证模块 /auth

### 2.1 用户登录

- **URL**: `POST /auth/login`
- **认证**: 无需
- **Content-Type**: `application/json`

**请求体** (LoginDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| username | String | 是 | @NotBlank | 用户名 |
| password | String | 是 | @NotBlank | 密码 |

**请求示例**:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**成功响应** `R<LoginVO>`:

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

**LoginVO 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户 ID |
| userName | String | 用户账号 |
| nickName | String | 用户昵称 |
| token | String | JWT Token，后续请求放入 Header |
| loginTime | Long | 登录时间戳（毫秒） |
| expireTime | Long | Token 过期时间戳（毫秒，默认 24 小时后） |
| ip | String | 登录 IP |
| address | String | 登录地址 |
| roles | String[] | 角色标识列表，如 `["admin", "common"]` |
| permissions | String[] | 权限标识列表，如 `["system:user:list", ...]` |

**失败响应**:

```json
{ "code": 401, "msg": "用户不存在或密码错误", "data": null }
```

---

### 2.2 用户注册

- **URL**: `POST /auth/register`
- **认证**: 无需
- **Content-Type**: `application/json`

**请求体** (RegisterDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| username | String | 是 | @NotBlank, @Size(2-20) | 用户名 |
| password | String | 是 | @NotBlank, @Size(6-20) | 密码 |
| nickname | String | 是 | @NotBlank, @Size(max=20) | 昵称 |
| email | String | 否 | @Email | 邮箱 |
| phonenumber | String | 否 | - | 手机号 |

**请求示例**:

```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "email": "zhangsan@example.com",
  "phonenumber": "13800138000"
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

**说明**: 注册成功后自动分配默认角色（roleId=2），密码经 BCrypt 加密存储。

---

### 2.3 用户登出

- **URL**: `POST /auth/logout`
- **认证**: 需要（Header 携带 Token）

**请求示例**:

```
POST /auth/logout
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 2.4 获取当前用户信息

- **URL**: `GET /auth/info`
- **认证**: 需要

**请求示例**:

```
GET /auth/info
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

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
      "avatar": null,
      "status": "0",
      "remark": "管理员",
      "createTime": "2024-01-01 00:00:00",
      "roles": [
        {
          "roleId": 1,
          "roleName": "超级管理员",
          "roleKey": "admin",
          "roleSort": 1,
          "dataScope": "1",
          "menuCheckStrictly": 1,
          "deptCheckStrictly": 1,
          "status": "0",
          "remark": null,
          "createTime": "2024-01-01 00:00:00"
        }
      ]
    },
    "roles": ["admin"],
    "permissions": ["system:user:list", "system:user:add", "..."]
  }
}
```

**data 字段结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| user | SysUserVO | 用户详细信息（不含密码） |
| roles | String[] | 角色标识列表 |
| permissions | String[] | 权限标识列表 |

**SysUserVO 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户 ID |
| deptId | Long | 部门 ID |
| userName | String | 用户账号 |
| nickName | String | 用户昵称 |
| email | String | 邮箱 |
| phonenumber | String | 手机号 |
| sex | String | 性别（0=男 1=女 2=未知） |
| avatar | String | 头像地址 |
| status | String | 状态（0=正常 1=停用） |
| remark | String | 备注 |
| createTime | String | 创建时间（格式：yyyy-MM-dd HH:mm:ss） |
| roles | SysRoleVO[] | 关联的角色列表（简要信息） |

---

## 3. 用户管理 /system/user

> 所有接口需要认证 + 对应权限标识

### 3.1 分页查询用户列表

- **URL**: `GET /system/user/list`
- **权限**: `system:user:list`

**Query 参数** (SysUserQueryDTO):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userName | String | 否 | 用户账号（模糊查询） |
| nickName | String | 否 | 用户昵称（模糊查询） |
| phonenumber | String | 否 | 手机号（模糊查询） |
| status | String | 否 | 状态（0=正常 1=停用） |
| deptId | Long | 否 | 部门 ID |
| beginTime | String | 否 | 创建时间-开始（yyyy-MM-dd） |
| endTime | String | 否 | 创建时间-结束（yyyy-MM-dd） |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |

**请求示例**:

```
GET /system/user/list?userName=admin&status=0&pageNum=1&pageSize=10
Authorization: Bearer <token>
```

**成功响应** `R<PageResult<SysUserVO>>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "rows": [
      {
        "userId": 1,
        "deptId": 100,
        "userName": "admin",
        "nickName": "超级管理员",
        "email": "admin@ruoyi.com",
        "phonenumber": "15888888888",
        "sex": "0",
        "avatar": null,
        "status": "0",
        "remark": "管理员",
        "createTime": "2024-01-01 00:00:00",
        "roles": []
      }
    ],
    "total": 1
  }
}
```

---

### 3.2 查询用户详情

- **URL**: `GET /system/user/{userId}`
- **权限**: `system:user:query`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户 ID |

**请求示例**:

```
GET /system/user/1
Authorization: Bearer <token>
```

**成功响应** `R<SysUserVO>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "deptId": 100,
    "userName": "admin",
    "nickName": "超级管理员",
    "email": "admin@ruoyi.com",
    "phonenumber": "15888888888",
    "sex": "0",
    "avatar": null,
    "status": "0",
    "remark": "管理员",
    "createTime": "2024-01-01 00:00:00",
    "roles": []
  }
}
```

---

### 3.3 新增用户

- **URL**: `POST /system/user`
- **权限**: `system:user:add`
- **Content-Type**: `application/json`

**请求体** (SysUserDTO, CreateGroup 校验):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| userName | String | 是 | @NotBlank, @Size(2-20) | 用户账号 |
| password | String | 是 | @NotBlank, @Size(6-20) | 密码（BCrypt 加密存储） |
| nickName | String | 是 | @NotBlank, @Size(max=30) | 用户昵称 |
| deptId | Long | 否 | - | 部门 ID |
| email | String | 否 | @Email | 邮箱 |
| phonenumber | String | 否 | @Size(max=11) | 手机号 |
| sex | String | 否 | - | 性别（0=男 1=女 2=未知） |
| avatar | String | 否 | - | 头像地址 |
| status | String | 否 | - | 状态（0=正常 1=停用），默认 0 |
| remark | String | 否 | - | 备注 |

**请求示例**:

```json
{
  "userName": "zhangsan",
  "password": "123456",
  "nickName": "张三",
  "deptId": 100,
  "email": "zhangsan@example.com",
  "phonenumber": "13800138000",
  "sex": "0"
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 3.4 修改用户

- **URL**: `PUT /system/user`
- **权限**: `system:user:edit`
- **Content-Type**: `application/json`

**请求体** (SysUserDTO, UpdateGroup 校验):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| userId | Long | 是 | @NotNull | 用户 ID |
| userName | String | 否 | @Size(2-20) | 用户账号 |
| nickName | String | 否 | @Size(max=30) | 用户昵称 |
| deptId | Long | 否 | - | 部门 ID |
| email | String | 否 | @Email | 邮箱 |
| phonenumber | String | 否 | @Size(max=11) | 手机号 |
| sex | String | 否 | - | 性别 |
| avatar | String | 否 | - | 头像地址 |
| status | String | 否 | - | 状态 |
| remark | String | 否 | - | 备注 |

> **注意**: 修改用户不支持修改密码，密码重置请使用 `PUT /system/user/resetPwd`

**请求示例**:

```json
{
  "userId": 2,
  "nickName": "张三改名",
  "email": "new@example.com"
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 3.5 批量删除用户

- **URL**: `DELETE /system/user/{userIds}`
- **权限**: `system:user:remove`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userIds | Long[] | 是 | 用户 ID 数组，多个用逗号分隔 |

> **注意**: 超级管理员（userId=1）不可删除。采用逻辑删除（del_flag 设为 2）。

**请求示例**:

```
DELETE /system/user/2,3
Authorization: Bearer <token>
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 3.6 重置用户密码

- **URL**: `PUT /system/user/resetPwd`
- **权限**: `system:user:resetPwd`
- **Content-Type**: `application/x-www-form-urlencoded`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户 ID |
| password | String | 是 | 新密码 |

**请求示例**:

```
PUT /system/user/resetPwd?userId=2&password=newPassword123
Authorization: Bearer <token>
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 3.7 切换用户状态

- **URL**: `PUT /system/user/changeStatus`
- **权限**: `system:user:edit`
- **Content-Type**: `application/x-www-form-urlencoded`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户 ID |
| status | String | 是 | 目标状态（"0"=正常, "1"=停用） |

> **注意**: 超级管理员（userId=1）不可停用。

**请求示例**:

```
PUT /system/user/changeStatus?userId=2&status=1
Authorization: Bearer <token>
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 3.8 获取用户的角色 ID 列表

- **URL**: `GET /system/user/roles/{userId}`
- **权限**: `system:user:query`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户 ID |

> **用途**: 用户编辑时，前端回显已勾选的角色。

**请求示例**:

```
GET /system/user/roles/1
Authorization: Bearer <token>
```

**成功响应** `R<Long[]>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [1]
}
```

---

### 3.9 分配用户角色

- **URL**: `PUT /system/user/roles`
- **权限**: `system:user:edit`
- **Content-Type**: `application/json`

**请求参数**:

| 参数 | 类型 | 位置 | 必填 | 说明 |
|------|------|------|------|------|
| userId | Long | Query | 是 | 用户 ID |
| roleIds | Long[] | Body | 是 | 角色 ID 数组 |

> **注意**: 超级管理员（userId=1）的角色不可修改。会先删除旧角色关联，再插入新角色关联。

**请求示例**:

```
PUT /system/user/roles?userId=2
Authorization: Bearer <token>
Content-Type: application/json

[1, 2]
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

## 4. 角色管理 /system/role

### 4.1 分页查询角色列表

- **URL**: `GET /system/role/list`
- **权限**: `system:role:list`

**Query 参数** (SysRoleQueryDTO):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleName | String | 否 | 角色名称（模糊查询） |
| roleKey | String | 否 | 权限标识（模糊查询） |
| status | String | 否 | 状态（0=正常 1=停用） |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |

**请求示例**:

```
GET /system/role/list?roleName=admin&pageNum=1&pageSize=10
Authorization: Bearer <token>
```

**成功响应** `R<PageResult<SysRoleVO>>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
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
        "remark": null,
        "createTime": "2024-01-01 00:00:00"
      }
    ],
    "total": 1
  }
}
```

**SysRoleVO 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| roleId | Long | 角色 ID |
| roleName | String | 角色名称 |
| roleKey | String | 权限标识（如 admin、common） |
| roleSort | Integer | 显示顺序 |
| dataScope | String | 数据范围（1=全部 2=自定义 3=本部门 4=本部门及以下 5=仅本人） |
| menuCheckStrictly | Integer | 菜单树是否关联（0=不关联 1=关联） |
| deptCheckStrictly | Integer | 部门树是否关联（0=不关联 1=关联） |
| status | String | 状态（0=正常 1=停用） |
| remark | String | 备注 |
| createTime | String | 创建时间（yyyy-MM-dd HH:mm:ss） |

---

### 4.2 查询角色详情

- **URL**: `GET /system/role/{roleId}`
- **权限**: `system:role:query`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | Long | 是 | 角色 ID |

**成功响应** `R<SysRoleVO>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "roleId": 1,
    "roleName": "超级管理员",
    "roleKey": "admin",
    "roleSort": 1,
    "dataScope": "1",
    "menuCheckStrictly": 1,
    "deptCheckStrictly": 1,
    "status": "0",
    "remark": null,
    "createTime": "2024-01-01 00:00:00"
  }
}
```

---

### 4.3 新增角色

- **URL**: `POST /system/role`
- **权限**: `system:role:add`
- **Content-Type**: `application/json`

**请求体** (SysRoleDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| roleName | String | 是 | @NotBlank, @Size(2-20) | 角色名称 |
| roleKey | String | 是 | @NotBlank, @Size(2-20) | 权限标识（如 admin、common） |
| roleSort | Integer | 是 | @NotNull | 显示顺序 |
| dataScope | String | 否 | - | 数据范围（1-5） |
| menuCheckStrictly | Integer | 否 | - | 菜单树关联（0/1） |
| deptCheckStrictly | Integer | 否 | - | 部门树关联（0/1） |
| status | String | 否 | - | 状态（0=正常 1=停用） |
| menuIds | Long[] | 否 | - | 关联的菜单 ID 数组 |
| remark | String | 否 | - | 备注 |

**请求示例**:

```json
{
  "roleName": "普通用户",
  "roleKey": "common",
  "roleSort": 2,
  "status": "0",
  "menuIds": [1, 2, 3, 100, 101],
  "remark": "普通用户角色"
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 4.4 修改角色

- **URL**: `PUT /system/role`
- **权限**: `system:role:edit`
- **Content-Type**: `application/json`

**请求体** (SysRoleDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| roleId | Long | 是 | - | 角色 ID |
| roleName | String | 是 | @NotBlank, @Size(2-20) | 角色名称 |
| roleKey | String | 是 | @NotBlank, @Size(2-20) | 权限标识 |
| roleSort | Integer | 是 | @NotNull | 显示顺序 |
| dataScope | String | 否 | - | 数据范围 |
| menuCheckStrictly | Integer | 否 | - | 菜单树关联 |
| deptCheckStrictly | Integer | 否 | - | 部门树关联 |
| status | String | 否 | - | 状态 |
| menuIds | Long[] | 否 | - | 关联的菜单 ID 数组 |
| remark | String | 否 | - | 备注 |

> **说明**: 会先删除角色的旧菜单关联，再根据 menuIds 重新插入。

**请求示例**:

```json
{
  "roleId": 2,
  "roleName": "普通用户",
  "roleKey": "common",
  "roleSort": 2,
  "status": "0",
  "menuIds": [1, 2, 3, 100, 101, 102]
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 4.5 批量删除角色

- **URL**: `DELETE /system/role/{roleIds}`
- **权限**: `system:role:remove`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleIds | Long[] | 是 | 角色 ID 数组，多个用逗号分隔 |

> **注意**: 超级管理员角色（roleId=1）不可删除。删除时同时清除角色-菜单关联。

**请求示例**:

```
DELETE /system/role/3,4
Authorization: Bearer <token>
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

## 5. 部门管理 /system/dept

### 5.1 查询部门列表

- **URL**: `GET /system/dept/list`
- **权限**: `system:dept:list`

**Query 参数** (SysDeptQueryDTO):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deptName | String | 否 | 部门名称（模糊查询） |
| status | String | 否 | 状态（0=正常 1=停用） |

> **说明**: 返回平铺列表，前端自行组装树形结构（通过 parentId 和 children 字段）。

**请求示例**:

```
GET /system/dept/list?status=0
Authorization: Bearer <token>
```

**成功响应** `R<List<SysDeptVO>>`:

```json
{
  "code": 200,
  "msg": "操作成功",
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
      "remark": null,
      "children": [
        {
          "deptId": 101,
          "parentId": 100,
          "ancestors": "0,100",
          "deptName": "研发部门",
          "orderNum": 1,
          "leader": null,
          "phone": null,
          "email": null,
          "status": "0",
          "remark": null,
          "children": []
        }
      ]
    }
  ]
}
```

**SysDeptVO 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| deptId | Long | 部门 ID |
| parentId | Long | 父部门 ID（0 表示顶级） |
| ancestors | String | 祖级列表（如 "0,100,101"） |
| deptName | String | 部门名称 |
| orderNum | Integer | 显示顺序 |
| leader | String | 负责人 |
| phone | String | 联系电话 |
| email | String | 邮箱 |
| status | String | 状态（0=正常 1=停用） |
| remark | String | 备注 |
| children | SysDeptVO[] | 子部门列表（递归结构） |

---

### 5.2 查询部门详情

- **URL**: `GET /system/dept/{deptId}`
- **权限**: `system:dept:query`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deptId | Long | 是 | 部门 ID |

**成功响应** `R<SysDeptVO>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "deptId": 100,
    "parentId": 0,
    "ancestors": "0",
    "deptName": "若依科技",
    "orderNum": 0,
    "leader": "若依",
    "phone": "15888888888",
    "email": "ry@qq.com",
    "status": "0",
    "remark": null,
    "children": []
  }
}
```

---

### 5.3 新增部门

- **URL**: `POST /system/dept`
- **权限**: `system:dept:add`
- **Content-Type**: `application/json`

**请求体** (SysDeptDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| parentId | Long | 否 | - | 父部门 ID（0 或不传表示顶级） |
| deptName | String | 是 | @NotBlank, @Size(max=50) | 部门名称 |
| orderNum | Integer | 是 | @NotNull | 显示顺序 |
| leader | String | 否 | @Size(max=20) | 负责人 |
| phone | String | 否 | @Size(max=11) | 联系电话 |
| email | String | 否 | @Email, @Size(max=50) | 邮箱 |
| status | String | 否 | - | 状态（0=正常 1=停用） |
| remark | String | 否 | - | 备注 |

> **注意**: ancestors（祖级列表）由服务端自动计算，前端无需传递。

**请求示例**:

```json
{
  "parentId": 100,
  "deptName": "测试部门",
  "orderNum": 3,
  "leader": "李四",
  "phone": "13800138000",
  "email": "test@ruoyi.com",
  "status": "0"
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 5.4 修改部门

- **URL**: `PUT /system/dept`
- **权限**: `system:dept:edit`
- **Content-Type**: `application/json`

**请求体** (SysDeptDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| deptId | Long | 是 | - | 部门 ID |
| parentId | Long | 否 | - | 父部门 ID |
| deptName | String | 否 | @Size(max=50) | 部门名称 |
| orderNum | Integer | 否 | - | 显示顺序 |
| leader | String | 否 | @Size(max=20) | 负责人 |
| phone | String | 否 | @Size(max=11) | 联系电话 |
| email | String | 否 | @Email | 邮箱 |
| status | String | 否 | - | 状态 |
| remark | String | 否 | - | 备注 |

**请求示例**:

```json
{
  "deptId": 101,
  "deptName": "研发部门（改名）",
  "orderNum": 1
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 5.5 删除部门

- **URL**: `DELETE /system/dept/{deptId}`
- **权限**: `system:dept:remove`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deptId | Long | 是 | 部门 ID |

> **注意**: 删除前校验 —— 不能有子部门，不能有用户属于该部门。

**请求示例**:

```
DELETE /system/dept/103
Authorization: Bearer <token>
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

## 6. 菜单管理 /system/menu

### 6.1 查询菜单列表

- **URL**: `GET /system/menu/list`
- **权限**: `system:menu:list`

**Query 参数** (SysMenuQueryDTO):

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuName | String | 否 | 菜单名称（模糊查询） |
| status | String | 否 | 状态（0=正常 1=停用） |
| visible | String | 否 | 显示状态（0=显示 1=隐藏） |

> **说明**: 返回平铺列表，前端可通过 parentId 和 children 自行组装树形结构。

**请求示例**:

```
GET /system/menu/list?status=0
Authorization: Bearer <token>
```

**成功响应** `R<List<SysMenuVO>>`:

```json
{
  "code": 200,
  "msg": "操作成功",
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
      "perms": null,
      "icon": "system",
      "remark": null,
      "createTime": "2024-01-01 00:00:00",
      "children": []
    }
  ]
}
```

**SysMenuVO 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| menuId | Long | 菜单 ID |
| menuName | String | 菜单名称 |
| parentId | Long | 父菜单 ID（0 表示顶级） |
| orderNum | Integer | 显示顺序 |
| path | String | 路由地址 |
| component | String | 组件路径 |
| query | String | 路由参数 |
| isFrame | Integer | 是否外链（0=否 1=是） |
| isCache | Integer | 是否缓存（0=缓存 1=不缓存） |
| menuType | String | 菜单类型（M=目录 C=菜单 F=按钮） |
| visible | String | 显示状态（0=显示 1=隐藏） |
| status | String | 状态（0=正常 1=停用） |
| perms | String | 权限标识（如 system:user:list） |
| icon | String | 菜单图标 |
| remark | String | 备注 |
| createTime | String | 创建时间（yyyy-MM-dd HH:mm:ss） |
| children | SysMenuVO[] | 子菜单列表（递归结构） |

---

### 6.2 查询菜单详情

- **URL**: `GET /system/menu/{menuId}`
- **权限**: `system:menu:query`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuId | Long | 是 | 菜单 ID |

**成功响应** `R<SysMenuVO>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
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
    "perms": null,
    "icon": "system",
    "remark": null,
    "createTime": "2024-01-01 00:00:00",
    "children": []
  }
}
```

---

### 6.3 新增菜单

- **URL**: `POST /system/menu`
- **权限**: `system:menu:add`
- **Content-Type**: `application/json`

**请求体** (SysMenuDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| menuName | String | 是 | @NotBlank, @Size(max=50) | 菜单名称 |
| parentId | Long | 否 | - | 父菜单 ID（0 或不传表示顶级） |
| orderNum | Integer | 是 | @NotNull | 显示顺序 |
| path | String | 否 | @Size(max=200) | 路由地址 |
| component | String | 否 | @Size(max=200) | 组件路径 |
| query | String | 否 | @Size(max=255) | 路由参数 |
| isFrame | Integer | 否 | - | 是否外链（0=否 1=是），默认 1 |
| isCache | Integer | 否 | - | 是否缓存（0=缓存 1=不缓存），默认 0 |
| menuType | String | 是 | @NotBlank | 菜单类型（M=目录 C=菜单 F=按钮） |
| visible | String | 否 | - | 显示状态（0=显示 1=隐藏） |
| status | String | 否 | - | 状态（0=正常 1=停用） |
| perms | String | 否 | @Size(max=100) | 权限标识（如 system:user:list） |
| icon | String | 否 | @Size(max=100) | 菜单图标 |
| remark | String | 否 | - | 备注 |

**请求示例**:

```json
{
  "menuName": "用户管理",
  "parentId": 1,
  "orderNum": 1,
  "path": "user",
  "component": "system/user/index",
  "menuType": "C",
  "visible": "0",
  "status": "0",
  "perms": "system:user:list",
  "icon": "user"
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 6.4 修改菜单

- **URL**: `PUT /system/menu`
- **权限**: `system:menu:edit`
- **Content-Type**: `application/json`

**请求体** (SysMenuDTO):

| 字段 | 类型 | 必填 | 校验规则 | 说明 |
|------|------|------|----------|------|
| menuId | Long | 是 | - | 菜单 ID |
| menuName | String | 否 | @Size(max=50) | 菜单名称 |
| parentId | Long | 否 | - | 父菜单 ID |
| orderNum | Integer | 否 | - | 显示顺序 |
| path | String | 否 | @Size(max=200) | 路由地址 |
| component | String | 否 | @Size(max=200) | 组件路径 |
| query | String | 否 | @Size(max=255) | 路由参数 |
| isFrame | Integer | 否 | - | 是否外链 |
| isCache | Integer | 否 | - | 是否缓存 |
| menuType | String | 否 | - | 菜单类型 |
| visible | String | 否 | - | 显示状态 |
| status | String | 否 | - | 状态 |
| perms | String | 否 | @Size(max=100) | 权限标识 |
| icon | String | 否 | @Size(max=100) | 菜单图标 |
| remark | String | 否 | - | 备注 |

**请求示例**:

```json
{
  "menuId": 100,
  "menuName": "用户管理（改名）",
  "orderNum": 1
}
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 6.5 删除菜单

- **URL**: `DELETE /system/menu/{menuId}`
- **权限**: `system:menu:remove`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| menuId | Long | 是 | 菜单 ID |

> **注意**: 删除前校验 —— 不能有子菜单。

**请求示例**:

```
DELETE /system/menu/100
Authorization: Bearer <token>
```

**成功响应**:

```json
{ "code": 200, "msg": "操作成功", "data": null }
```

---

### 6.6 获取菜单树

- **URL**: `GET /system/menu/treeselect`
- **权限**: `system:menu:list`

> **用途**: 角色分配菜单时，前端用此接口渲染树形选择器。

**请求示例**:

```
GET /system/menu/treeselect
Authorization: Bearer <token>
```

**成功响应** `R<List<MenuTreeVO>>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "label": "系统管理",
      "children": [
        {
          "id": 100,
          "label": "用户管理",
          "children": []
        },
        {
          "id": 101,
          "label": "角色管理",
          "children": []
        }
      ]
    }
  ]
}
```

**MenuTreeVO 字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 菜单 ID（对应 menuId） |
| label | String | 菜单名称（对应 menuName） |
| children | MenuTreeVO[] | 子节点列表（递归结构） |

---

### 6.7 获取角色已分配的菜单 ID 列表

- **URL**: `GET /system/menu/roleMenuTreeVOselect/{roleId}`
- **权限**: `system:role:query`

**路径参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleId | Long | 是 | 角色 ID |

> **用途**: 角色编辑时，前端回显已勾选的菜单节点（配合 treeselect 使用）。

**请求示例**:

```
GET /system/menu/roleMenuTreeVOselect/1
Authorization: Bearer <token>
```

**成功响应** `R<List<Long>>`:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [1, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 500]
}
```

---

## 附录：前端联调指南

### 认证流程

```
1. POST /auth/login          → 获取 token
2. 请求头添加 Authorization: Bearer <token>
3. GET /auth/info             → 获取用户信息、角色、权限
4. 根据 permissions 动态生成菜单和按钮
```

### 前端存储建议

登录成功后，前端需要存储以下信息：

```js
localStorage.setItem('token', data.token)
localStorage.setItem('userId', data.userId)
localStorage.setItem('userName', data.userName)
localStorage.setItem('nickName', data.nickName)
localStorage.setItem('roles', JSON.stringify(data.roles))
localStorage.setItem('permissions', JSON.stringify(data.permissions))
```

### 权限控制示例

```vue
<!-- 按钮权限控制 -->
<el-button v-if="permissions.includes('system:user:add')">新增</el-button>
<el-button v-if="permissions.includes('system:user:edit')">修改</el-button>
<el-button v-if="permissions.includes('system:user:remove')">删除</el-button>

<!-- 路由权限控制 -->
// 根据 permissions 过滤路由表，只保留有权限的路由
```

### 树形数据处理

部门列表（`/system/dept/list`）和菜单列表（`/system/menu/list`）返回的是平铺列表，前端需要自行组装树形结构：

```js
function buildTree(list, parentId = 0) {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(list, item.deptId || item.menuId)
    }))
}
```

### 常用状态码

| 状态码 | 含义 | 处理方式 |
|--------|------|----------|
| 200 | 成功 | 正常处理 data |
| 401 | 未认证/Token过期 | 跳转登录页 |
| 403 | 权限不足 | 提示用户 |
| 400 | 参数错误 | 提示用户检查输入 |
| 500 | 服务端错误 | 提示用户稍后重试 |
