# RuoYi API 接口文档

> Base URL: `http://localhost:8080`
> Content-Type: `application/json`
> 认证方式: `Authorization: Bearer {token}`（除登录/注册外所有接口）

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
  "msg": "未登录或 Token 已过期",
  "data": null
}
```

---

## 一、认证模块 `/auth`

### 1.1 登录

```
POST /auth/login
```

**请求体 LoginDTO：**

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

**成功响应：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "userName": "admin",
    "nickName": "超级管理员",
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "loginTime": 1749628800000,
    "expireTime": 1749715200000,
    "ip": "127.0.0.1",
    "address": "127.0.0.1",
    "permissions": ["*:*:*"],
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
| loginTime | Long | 登录时间（毫秒时间戳） |
| expireTime | Long | 过期时间（毫秒时间戳） |
| ip | String | IP 地址 |
| address | String | 地址（当前存储为 IP） |
| permissions | String[] | 权限列表，`*:*:*` 表示超级管理员 |
| roles | String[] | 角色列表 |

---

### 1.2 注册

```
POST /auth/register
```

**请求体 RegisterDTO：**

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
| username | String | ✅ | 2-20 字符 |
| password | String | ✅ | 6-20 字符 |
| nickname | String | ✅ | 最大 20 字符 |
| email | String | ❌ | `@Email` 格式 |
| phonenumber | String | ❌ | -- |

**成功响应：** `R<Void>` — 空 data

---

### 1.3 登出

```
POST /auth/logout
```

**请求头：** `Authorization: Bearer {token}`

**请求体：** 无

**成功响应：** `R<Void>`

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
      "userName": "admin",
      "nickName": "超级管理员",
      "email": "admin@ruoyi.com",
      "phonenumber": "15888888888",
      "sex": "0",
      "avatar": "",
      "status": "0",
      "createTime": "2024-01-01 00:00:00",
      "roles": [{ "roleId": 1, "roleName": "超级管理员", "roleKey": "admin", "roleSort": 1, "status": "0", "createTime": "2024-01-01 00:00:00" }]
    },
    "roles": ["admin"],
    "permissions": ["*:*:*"]
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
| deptId | Long | ❌ | 部门ID（含下级部门） |
| beginTime | String | ❌ | 创建开始时间 `yyyy-MM-dd` |
| endTime | String | ❌ | 创建结束时间 `yyyy-MM-dd` |

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
        "nickName": "超级管理员",
        "email": "admin@ruoyi.com",
        "phonenumber": "15888888888",
        "sex": "0",
        "avatar": "",
        "status": "0",
        "remark": "管理员",
        "createTime": "2024-01-01 00:00:00",
        "roles": [
          { "roleId": 1, "roleName": "超级管理员", "roleKey": "admin", "roleSort": 1, "status": "0", "createTime": "2024-01-01 00:00:00" }
        ]
      }
    ]
  }
}
```

**SysUserVO 字段：**

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
| roles | SysRoleVO[] | 角色列表 |

**SysRoleVO 字段：**

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

**响应：** `R<SysUserVO>` 同上

---

### 2.3 新增用户

```
POST /system/user
```

**权限：** `system:user:add`

**请求体 SysUserDTO（CreateGroup 校验）：**

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
| userName | String | ✅ | 2-20 字符 |
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

**请求体 SysUserDTO（UpdateGroup 校验）：**

```json
{
  "userId": 2,
  "deptId": 103,
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
PUT /system/user/resetPwd?userId=2&password=newPass123
```

**权限：** `system:user:resetPwd`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| password | String | ✅ | 新密码 |

**响应：** `R<Void>`

---

### 2.7 修改用户状态

```
PUT /system/user/changeStatus?userId=2&status=1
```

**权限：** `system:user:edit`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | ✅ | 用户ID |
| status | String | ✅ | `0`=正常, `1`=停用 |

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
PUT /system/user/roles?userId=2
```

**权限：** `system:user:edit`

**请求体：** `Long[]` — 角色ID数组

```json
[1, 2, 5]
```

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
      "remark": "",
      "children": [
        {
          "deptId": 103,
          "parentId": 100,
          "ancestors": "0,100",
          "deptName": "研发部门",
          "orderNum": 1,
          "leader": "张三",
          "phone": "",
          "email": "",
          "status": "0",
          "remark": "",
          "children": []
        }
      ]
    }
  ]
}
```

**SysDeptVO 字段：**

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
| children | SysDeptVO[] | 子部门（递归树） |

> 前端拿到平铺数据后自行构建树，或直接使用嵌套 children 渲染

---

### 3.2 获取部门详情

```
GET /system/dept/{deptId}
```

**权限：** `system:dept:query`

**响应：** `R<SysDeptVO>`（单个，无 children）

---

### 3.3 新增部门

```
POST /system/dept
```

**权限：** `system:dept:add`

**请求体 SysDeptDTO：**

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
| deptName | String | ✅ | 最大 50 字符 |
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

**请求体：** SysDeptDTO，必须包含 `deptId`

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

**响应：** `R<SysRoleVO>`

---

### 4.3 新增角色

```
POST /system/role
```

**权限：** `system:role:add`

**请求体 SysRoleDTO：**

```json
{
  "roleName": "普通角色",
  "roleKey": "common",
  "roleSort": 2,
  "dataScope": "1",
  "menuCheckStrictly": 1,
  "deptCheckStrictly": 1,
  "status": "0",
  "menuIds": [1, 100, 1000, 1001, 1002],
  "remark": "普通角色"
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| roleId | Long | ❌ | | 新增不传 |
| roleName | String | ✅ | 2-20 字符 | 角色名称 |
| roleKey | String | ✅ | 2-20 字符 | 角色标识（唯一） |
| roleSort | Integer | ✅ | | 排序号 |
| dataScope | String | ❌ | | 数据范围：1=全部, 2=自定义, 3=本部门, 4=本部门及以下, 5=仅本人 |
| menuCheckStrictly | Integer | ❌ | | 菜单树父子关联：0=不关联, 1=关联 |
| deptCheckStrictly | Integer | ❌ | | 部门树父子关联：0=不关联, 1=关联 |
| status | String | ❌ | | `0`/`1` |
| menuIds | Long[] | ❌ | | 授权菜单ID数组 |
| remark | String | ❌ | | 备注 |

**响应：** `R<Void>`

---

### 4.4 修改角色

```
PUT /system/role
```

**权限：** `system:role:edit`

**请求体：** SysRoleDTO，必须包含 `roleId`，`menuIds` 会重建关联

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
      "isFrame": 0,
      "isCache": 0,
      "menuType": "M",
      "visible": "0",
      "status": "0",
      "perms": "",
      "icon": "system",
      "remark": "系统管理目录",
      "createTime": "2024-01-01 00:00:00",
      "children": [
        {
          "menuId": 100,
          "menuName": "用户管理",
          "parentId": 1,
          "orderNum": 1,
          "path": "user",
          "component": "system/user/index",
          "query": null,
          "isFrame": 0,
          "isCache": 0,
          "menuType": "C",
          "visible": "0",
          "status": "0",
          "perms": "system:user:list",
          "icon": "user",
          "remark": "用户管理菜单",
          "createTime": "2024-01-01 00:00:00",
          "children": [
            { "menuId": 1000, "menuName": "用户查询", "parentId": 100, "menuType": "F", "perms": "system:user:query", "children": [] },
            { "menuId": 1001, "menuName": "用户新增", "parentId": 100, "menuType": "F", "perms": "system:user:add", "children": [] }
          ]
        }
      ]
    }
  ]
}
```

**SysMenuVO 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| menuId | Long | 菜单ID |
| menuName | String | 菜单名称 |
| parentId | Long | 父菜单ID |
| orderNum | Integer | 排序号 |
| path | String | 路由地址 |
| component | String | 组件路径 |
| query | String | 路由参数 |
| isFrame | Integer | `0`=内部, `1`=外链 |
| isCache | Integer | `0`=缓存, `1`=不缓存 |
| menuType | String | `M`=目录, `C`=菜单, `F`=按钮 |
| visible | String | `0`=显示, `1`=隐藏 |
| status | String | `0`=正常, `1`=停用 |
| perms | String | 权限标识，如 `system:user:add` |
| icon | String | 图标 |
| remark | String | 备注 |
| createTime | String | 创建时间 |
| children | SysMenuVO[] | 子菜单（递归树） |

---

### 5.2 获取菜单详情

```
GET /system/menu/{menuId}
```

**权限：** `system:menu:query`

**响应：** `R<SysMenuVO>`（单个）

---

### 5.3 新增菜单

```
POST /system/menu
```

**权限：** `system:menu:add`

**请求体 SysMenuDTO：**

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
| component | String | ❌ | 组件路径，最大 200 字符 |
| query | String | ❌ | 路由参数 |
| isFrame | Integer | ❌ | `0` 内部 / `1` 外链 |
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

**请求体：** SysMenuDTO，必须包含 `menuId`

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
            { "id": 1000, "label": "用户查询", "children": [] },
            { "id": 1001, "label": "用户新增", "children": [] }
          ]
        }
      ]
    }
  ]
}
```

**MenuTreeVO 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 菜单ID（对应 SysMenu.menuId） |
| label | String | 菜单名称（对应 SysMenu.menuName） |
| children | MenuTreeVO[] | 子菜单 |

---

### 5.7 获取角色已授权菜单ID

```
GET /system/menu/roleMenuTreeVOselect/{roleId}
```

**权限：** `system:role:query`

**成功响应：**

```json
{
  "code": 200,
  "data": [1, 100, 1000, 1001, 1002]
}
```

> 用于角色编辑弹窗中菜单树的回显勾选

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
{ "code": 401, "msg": "未登录或 Token 已过期", "data": null }
```

前端应在响应拦截器中捕获 401 并跳转登录页。

### 权限控制

前端根据 `/auth/info` 返回的 `permissions` 数组控制按钮/菜单显示：

- `["*:*:*"]` 表示超级管理员，拥有全部权限
- 普通用户返回具体权限标识，如 `["system:user:list", "system:user:query"]`

### 数据范围

角色的 `dataScope` 字段控制行级数据权限：

- `1` — 全部数据
- `2` — 自定义（需关联 sys_role_dept）
- `3` — 本部门数据
- `4` — 本部门及以下数据
- `5` — 仅本人数据

### 前端部门/菜单树构建

- `/system/dept/list` 返回含 `children` 的嵌套结构，前端可直接渲染树
- `/system/menu/list` 同理，含 `children` 递归
- 如需平铺数据，前端可自行展平

### 分页参数

支持分页的接口（用户列表、角色列表）：

- `pageNum` — 页码（默认 1）
- `pageSize` — 每页条数（默认 10）
- 响应中 `data.total` 为总记录数，`data.rows` 为当前页数据
