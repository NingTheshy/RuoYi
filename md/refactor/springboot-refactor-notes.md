# Spring Boot 重构笔记

这份文档是给刚学 Spring Boot 的人看的，专门解释这个项目最近两轮重构到底改了什么、为什么这么改、改完以后代码是怎么跑起来的。

## 1. 这两轮主要改了什么

最近两轮主要做了 5 件事：

1. 把字段注入 `@Autowired` 统一成了构造器注入。
2. 把零散的 `@Value` 配置，逐步收成了 `@ConfigurationProperties` 配置类。
3. 把认证接口里的业务编排，从 `AuthController` 下沉到了 `AuthService`。
4. 把 Service 接口名从 `ISysUserService` 这类老风格，统一成了 `SysUserService`。
5. 把容易误导的命名收了一遍，比如 `MapStructConfig` 改成了 `MapStructMapperConfig`。

可以先记一句总原则：

- `Controller` 负责接 HTTP 请求和返回响应
- `Service` 负责业务流程
- `Entity` 负责数据库模型
- `Req/Resp` 负责接口输入输出
- `Properties` 负责承接配置文件

## 2. 为什么不再推荐字段注入

以前很多示例都这么写：

```java
@Autowired
private TokenService tokenService;
```

这叫字段注入。

Spring 在创建对象之后，再通过反射把 `tokenService` 塞进去，所以看代码时会有两个问题：

1. 这个类依赖了谁，不够显眼。
2. 这个字段不能舒服地写成 `final`。

现在统一成了构造器注入：

```java
private final TokenService tokenService;

public LoginService(TokenService tokenService) {
    this.tokenService = tokenService;
}
```

这种写法的好处是：

1. 一眼就能看出这个类需要哪些依赖。
2. 字段可以是 `final`，对象创建出来就是完整状态。
3. 单元测试更方便，因为可以手动 new。

### `@RequiredArgsConstructor` 是什么

`@RequiredArgsConstructor` 是 Lombok 的注解，它会自动帮你生成“所有 `final` 字段”的构造器。

例如：

```java
@RequiredArgsConstructor
public class LoginService {
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
}
```

编译后，Lombok 会自动生成和手写构造器等价的代码。

这次我没有直接全部改成 `@RequiredArgsConstructor`，不是因为它不好，而是因为你现在还在学概念。  
显式构造器更适合教学，你能明确看到“依赖是怎么进来的”。  
等你把注入方式、配置绑定、分层理解透了，再统一换成 Lombok 简写会更顺。

## 3. `@Value` 和 `@ConfigurationProperties` 的区别

### 3.1 `@Value`

`@Value` 适合读取零散的单个配置。

例如：

```java
@Value("${jwt.expiration:86400000}")
private long tokenExpiration;
```

意思是：

- 去配置文件里找 `jwt.expiration`
- 找到了就用它
- 找不到就用默认值 `86400000`

### 3.2 `@ConfigurationProperties`

当一组配置是相关的，就更适合收成一个配置类。

例如现在项目里的：

- [JwtProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/JwtProperties.java)
- [RuoyiProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/RuoyiProperties.java)

`JwtProperties` 负责承接：

- `jwt.secret`
- `jwt.expiration`

`RuoyiProperties` 负责承接：

- `ruoyi.security.token-header`
- `ruoyi.security.token-prefix`
- `ruoyi.cors.allowed-origins`

这样做的好处是：

1. 配置更集中，不会散落在很多类里的 `@Value` 上。
2. 一组配置的语义更清楚。
3. 后面配置项变多时，更容易维护。

### 3.2.1 为什么这次把 `AutoConfiguration.imports` 删掉了

这个项目当前的模块接入方式是：

- 启动类放在根包 [RuoYiAdminApplication](file:///d:/dev/code/RuoYi/ruoyi-admin/src/main/java/com/ruoyi/RuoYiAdminApplication.java)
- `@SpringBootApplication` 会默认扫描 `com.ruoyi` 及其子包
- 所以 `ruoyi-common`、`ruoyi-system` 里的 `@Configuration`、`@Service`、`@Component` 本来就会被注册

在这种前提下，再额外保留 `AutoConfiguration.imports`，就会带来两个问题：

1. Bean 注册来源变成两套，不容易判断“这个类到底是扫描进来的，还是自动配置导入进来的”
2. 文件里如果混入 `LoginService`、`TokenService`、`JwtAuthenticationFilter` 这类普通组件，就不符合自动配置入口应有的语义

所以这次的处理是：

- 把启动类上移到根包，回到 Spring Boot 默认扫描方式
- 删除多余的 `AutoConfiguration.imports`

这样更符合“单一装配入口”的思路，也更适合你现在读代码。

### 3.3 现在这个项目是怎么启用配置类扫描的

在启动类 [RuoYiAdminApplication](file:///d:/dev/code/RuoYi/ruoyi-admin/src/main/java/com/ruoyi/RuoYiAdminApplication.java) 上加了：

```java
@ConfigurationPropertiesScan(basePackages = "com.ruoyi")
```

它的作用是：

- 扫描项目中的 `@ConfigurationProperties`
- 自动把这些配置类注册成 Spring Bean

所以现在 `JwtProperties` 和 `RuoyiProperties` 都可以直接被注入到别的类里。

### 3.3.1 为什么又把一部分服务接口留在 `common`，实现放到 `system`

这次又收了一类以前很容易写乱的地方：

- [PermissionService](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/service/PermissionService.java)
- [DataScopeService](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/datascope/service/DataScopeService.java)

现在它们在 `common` 里只保留接口，真正实现放到：

- [PermissionServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/PermissionServiceImpl.java)
- [DataScopeServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/DataScopeServiceImpl.java)

这样做的原因是：

1. `JwtAuthenticationFilter`、`DataScopeAspect` 这些横切能力属于公共基础设施，所以它们依赖的类型放在 `common` 是合理的
2. 但“查 `sys_user`、`sys_role`、`sys_menu`、`sys_role_dept`”这种事，本质上是系统域实现细节，不应该继续写在 `common`
3. 所以最合适的形态就是：

- `common` 定义“需要什么能力”
- `system` 提供“这个能力怎么实现”

你以后看到这种结构，可以把它理解成一句话：

- 上层公共模块只依赖抽象，不直接依赖业务表实现

## 3.4 看配置时，到底该怎么读

很多初学者一看到这些就会懵：

- 配置类里有默认值
- `application.yml` 里又写了一遍
- 有的地方直接 `@Value`
- 有的地方又是 `@ConfigurationProperties`

其实你不要一上来就问“为什么配这么多”，先按下面 5 步看。

### 第一步：先看是谁在用这个配置

比如：

- `jwt.expiration` 被 [AuthServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/AuthServiceImpl.java) 和 [TokenService](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/service/TokenService.java) 用来决定 token 什么时候过期
- `ruoyi.security.token-header` 被 [JwtAuthenticationFilter](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/filter/JwtAuthenticationFilter.java) 和 [AuthController](file:///d:/dev/code/RuoYi/ruoyi-admin/src/main/java/com/ruoyi/admin/web/auth/AuthController.java) 用来决定从哪个请求头取 token
- `ruoyi.cors.allowed-origins` 被 [SecurityConfig](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/SecurityConfig.java) 用来决定谁能跨域访问后端

先找到“谁在读它”，你才知道这个配置是干什么的。

### 第二步：看这个配置是不是有代码默认值

现在这个项目已经收敛成“配置文件是单一真相来源”的写法：

- [JwtProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/JwtProperties.java) 不再在代码里写默认值
- [RuoyiProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/RuoyiProperties.java) 里的 token 配置也不再在代码里写默认值

这就说明：

- 如果配置文件没写，启动时会直接因为校验失败而暴露问题
- 不会再出现“代码里还有一份默认值，结果把错误配置悄悄兜住了”

### 第三步：再看 yml 里有没有覆盖它

现在这套配置大多数都没有代码默认值，所以顺序通常是：

1. 先看 `application.yml`
2. 再看 `application-dev.yml`
3. 最后再看启动参数或环境变量有没有覆盖

如果以后某个配置类重新加了默认值，再把“代码默认值”也纳入排查顺序。

### 第四步：判断这是“必须配”还是“可选配”

不是所有配置都一个等级。

#### 典型必须配

- 数据库账号密码
- Redis 账号密码
- 生产环境 JWT secret

这类配置本质上不应该靠代码默认值长期兜底。

#### 典型可选配

- `server.port`
- 本地开发的 CORS 地址
- 日志级别

这类配置通常可以有合理默认值。

### 第五步：问一句“这个默认值是为了方便，还是会掩盖问题”

这是最关键的一步。

例如：

- `jwt.expiration` 放在 `application.yml`
  - 这是公共配置，所有环境都要用到
- `jwt.secret` 放在 `application-dev.yml`
  - 这是环境配置，开发和生产应当分开管理

所以以后你看配置，不要只问“为什么有默认值”，还要问：

- 这个值是不是应该放在公共配置里
- 它是不是应该只在开发环境存在
- 它会不会让错误配置被悄悄吞掉

## 3.5 你以后自己写配置时，可以套这个模板

以后你自己写一个配置，不需要一上来就写得很高级，先按这个思路：

### 情况 1：只有 1 个零散值

例如：

- 上传文件最大大小
- 某个开关

可以先用 `@Value`。

### 情况 2：一组明显相关的值

例如：

- `jwt.secret`
- `jwt.expiration`

或者：

- `ruoyi.security.token-header`
- `ruoyi.security.token-prefix`

这时就该用 `@ConfigurationProperties`。

### 情况 3：这个值配错会出大问题

例如：

- secret 为空
- 过期时间 <= 0

这时就要加配置校验，比如：

```java
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @Positive
    private long expiration;
}
```

这次我已经在项目里把这个思路补进去了：

- [JwtProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/JwtProperties.java)
- [RuoyiProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/RuoyiProperties.java)

这样如果有人把关键配置写错，项目会更早暴露问题，而不是跑起来后再慢慢炸。

## 4. 为什么要把认证逻辑从 Controller 挪到 Service

以前 `AuthController` 里做了太多事：

1. 查用户
2. 校验登录
3. 生成 token
4. 查角色和权限
5. 拼 `LoginResp`
6. 更新登录时间和 IP

这会导致 `Controller` 太胖，而且直接知道 `SysUser` 这种持久化实体。

现在把这些流程下沉到了：

- [AuthService](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/AuthService.java)
- [AuthServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/AuthServiceImpl.java)

现在分工变成：

- [AuthController](file:///d:/dev/code/RuoYi/ruoyi-admin/src/main/java/com/ruoyi/admin/web/auth/AuthController.java)：接请求、调用服务、返回结果
- [AuthServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/AuthServiceImpl.java)：编排认证业务流程
- [LoginService](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/service/LoginService.java)：负责登录校验和 token 发放

这就是常见的分层思路：

- Controller 不负责拼复杂业务
- Service 负责业务编排

## 5. `Entity`、`Req`、`Resp` 到底分别是什么

### 5.1 Entity

`Entity` 是数据库模型，比如：

- `SysUser`
- `SysRole`
- `SysMenu`

它们通常和表结构强相关，所以不适合直接暴露给前端。

### 5.2 Req

`Req` 是接口请求体，比如：

- `LoginReq`
- `RegisterReq`
- `SysUserCreateReq`

它代表“前端传进来什么”。

### 5.3 Resp

`Resp` 是接口响应体，比如：

- `LoginResp`
- `AuthInfoResp`
- `SysUserResp`

它代表“后端返回给前端什么”。

### 5.4 为什么要分开

如果不分开，常见问题有：

1. 数据库字段会暴露给前端。
2. 某些内部字段可能不小心返回出去。
3. 一改表结构，接口层也会一起跟着乱。

所以现在项目在朝这个方向走：

- Controller 尽量只碰 `Req/Resp`
- Service 内部才去碰 `Entity`

## 6. 为什么 `AuthController` 现在更干净了

现在的 [AuthController](file:///d:/dev/code/RuoYi/ruoyi-admin/src/main/java/com/ruoyi/admin/web/auth/AuthController.java) 主要只做这些事：

1. `register()` 接收注册请求，然后调用 `authService.register()`
2. `login()` 接收登录请求，然后调用 `authService.login()`
3. `logout()` 从请求头里拿 token，再交给 `authService.logout()`
4. `getInfo()` 直接调用 `authService.getAuthInfo()`

也就是说，它现在更像“接口入口”，而不再像“业务大杂烩”。

## 7. 为什么要去掉 `I` 前缀接口名

以前常见写法是：

- `ISysUserService`
- `ISysRoleService`

这是一种比较老的命名习惯，很多老项目里都有。

现在更常见的写法是：

- `SysUserService`
- `SysRoleService`

原因很简单：

1. Java 里接口和实现本来就能靠 `interface`/`implements` 分出来。
2. `I` 前缀信息量不大，反而让名字更别扭。
3. 现在大多数 Spring 项目更喜欢不带 `I` 的接口名。

所以这次统一成不带 `I`，更接近行业常见风格。

## 8. 为什么 `MapStructConfig` 要改名

原来有个名字叫 `MapStructConfig`，但它并不是 Spring 的 `@Configuration`。

它实际上是 MapStruct 的元配置：

```java
@MapperConfig(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapStructMapperConfig {
}
```

如果还叫 `MapStructConfig`，很容易让人误会它是 Spring 配置类。

所以改名成 [MapStructMapperConfig](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/convert/MapStructMapperConfig.java) 更清楚。

## 9. 现在请求进来之后，大概怎么走

以登录接口为例：

1. 前端发 `POST /auth/login`
2. Spring MVC 把 JSON 反序列化成 `LoginReq`
3. `@Valid` 触发参数校验
4. `AuthController.login()` 收到 `LoginReq`
5. `AuthController` 调 `authService.login(loginReq, ip)`
6. `AuthServiceImpl` 调 `userService` 查用户
7. `AuthServiceImpl` 调 `loginService.login()` 校验密码并生成 token
8. `AuthServiceImpl` 再查角色和权限，组装成 `LoginResp`
9. `AuthController` 返回 `R<LoginResp>`
10. Spring MVC 把它序列化成 JSON 返回给前端

## 10. 你现在应该怎么理解这些改动

你不用一开始就把所有注解全背下来，先抓 4 个核心点：

1. 依赖怎么进类里：构造器注入
2. 配置怎么进类里：`@Value` 或 `@ConfigurationProperties`
3. HTTP 接口放哪：`Controller`
4. 业务流程放哪：`Service`

如果你把这 4 个点吃透，后面再看 Lombok、AOP、Security、MapStruct，就不会乱。

## 11. 这两轮继续优化后，又补了什么

### 11.1 认证异常不再靠 `null` 往上猜

现在 [AuthServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/AuthServiceImpl.java) 在登录失败、当前用户不存在时，会直接抛业务异常：

- 登录用户名不存在：直接抛 401 业务异常
- `/auth/info` 当前用户查不到：直接抛 401 业务异常

这样做的好处是：

- Controller 不用自己写一堆判空分支
- 错误语义更明确
- 统一交给 [GlobalExceptionHandler](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/core/exception/GlobalExceptionHandler.java) 返回

### 11.2 查询参数校验，也接进了统一异常处理

之前很多人只知道 `@RequestBody + @Valid` 会进统一异常处理，
但其实 `GET` 请求里的查询参数、表单参数，也可能触发校验异常。

这次补进去的是：

- `BindException`
- `HandlerMethodValidationException`
- `ConstraintViolationException`

也就是说，现在不只是 JSON 请求体，连查询参数校验失败，也会尽量走统一的 400 响应。

### 11.3 不是所有异常都应该一把抓成 `catch (Exception)`

这是你以后看代码时一个很重要的判断点。

比如 [JwtAuthenticationFilter](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/filter/JwtAuthenticationFilter.java)：

- 如果只是 token 本身坏了、过期了、内容非法了
  - 这是“坏请求/坏凭证”
  - 可以记日志，然后让请求以未认证身份继续往后走
- 如果是 Redis、数据库、权限查询这类基础设施真的炸了
  - 这是“系统故障”
  - 就不应该假装没事继续吞掉

所以现在的思路是：

- 只吞“可以预期的坏 token 异常”
- 不中断地放行给后续认证/鉴权流程处理
- 不吞真正的系统异常

### 11.4 为什么 401/403 处理器也不要自己 `new ObjectMapper()`

这也是初学者很容易忽略的一点。

如果你在 [CustomAuthenticationEntryPoint](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/handler/CustomAuthenticationEntryPoint.java)
和 [CustomAccessDeniedHandler](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/handler/CustomAccessDeniedHandler.java)
里自己 new 一个 `ObjectMapper`，问题是：

- 它不一定带上 Spring Boot 全局 Jackson 配置
- 以后你改时间格式、序列化规则、模块注册时，这两个地方可能单独跑偏

所以现在改成：

- 直接注入 Spring 管理的 `ObjectMapper`
- 保证 401/403 响应和普通接口 JSON 风格一致

同样的思路，这次也顺手用了在 [RedisConfig](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/redis/config/RedisConfig.java) 上：

- 不是完全 `new ObjectMapper()` 从零来一份
- 而是基于 Spring 全局 `ObjectMapper` 复制一份再加 Redis 需要的多态配置

这样做的意义是：

- Web 接口 JSON 风格和 Redis 序列化风格不会完全脱节
- 该独立的能力仍然独立，比如 Redis 需要的默认类型信息
- 以后你调 Jackson 全局行为时，Redis 这边也更容易保持一致

## 12. 现在还可以继续优化的地方

虽然已经比前面又干净了一些，但还有空间：

1. 接口路径整体上还是传统后台风格，不是完全 REST 化
2. 数据权限这块目前还是字符串拼 SQL 条件，维护成本偏高
3. Service 层还没有继续细拆成 command/query 或 application/domain 风格

这些不是 bug，而是“后续架构演进空间”。

## 12. 你下一步最适合怎么学

建议你按这个顺序看代码：

1. [RuoyiProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/RuoyiProperties.java)
2. [JwtProperties](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/config/JwtProperties.java)
3. [AuthController](file:///d:/dev/code/RuoYi/ruoyi-admin/src/main/java/com/ruoyi/admin/web/auth/AuthController.java)
4. [AuthServiceImpl](file:///d:/dev/code/RuoYi/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/AuthServiceImpl.java)
5. [LoginService](file:///d:/dev/code/RuoYi/ruoyi-common/src/main/java/com/ruoyi/common/security/service/LoginService.java)

看这 5 个文件时，分别问自己：

1. 这个类是干什么的
2. 它依赖谁
3. 它是接配置、接请求，还是写业务
4. 它有没有碰数据库实体

这样看几遍，你对这个项目的理解会快很多。
