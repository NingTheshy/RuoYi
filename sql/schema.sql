-- schema.sql
-- 时间字段策略：
-- 1. 数据库统一使用 DATETIME，无需因 Java 侧切换到 LocalDateTime 而改列类型。
-- 2. 接口层按 yyyy-MM-dd HH:mm:ss 进行序列化与反序列化。
drop database if exists ry;
CREATE DATABASE IF NOT EXISTS `ry` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ry`;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id       BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id       BIGINT DEFAULT NULL COMMENT '部门ID',
    user_name     VARCHAR(30) NOT NULL UNIQUE COMMENT '用户账号',
    nick_name     VARCHAR(30) NOT NULL COMMENT '用户昵称',
    email         VARCHAR(50) DEFAULT '' COMMENT '邮箱',
    phonenumber   VARCHAR(11) DEFAULT '' COMMENT '手机号',
    sex           CHAR(1) DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
    avatar        VARCHAR(100) DEFAULT '' COMMENT '头像地址',
    password      VARCHAR(100) NOT NULL COMMENT '密码',
    status        CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    login_ip      VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
    login_date    DATETIME DEFAULT NULL COMMENT '最后登录时间',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time   DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户信息表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id             BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name           VARCHAR(30) NOT NULL COMMENT '角色名称',
    role_key            VARCHAR(100) NOT NULL UNIQUE COMMENT '角色权限字符串',
    role_sort           INT NOT NULL COMMENT '显示顺序',
    data_scope          CHAR(1) DEFAULT '1' COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    menu_check_strictly TINYINT(1) DEFAULT 1 COMMENT '菜单树选择项是否关联',
    dept_check_strictly TINYINT(1) DEFAULT 1 COMMENT '部门树选择项是否关联',
    status              CHAR(1) NOT NULL COMMENT '状态（0正常 1停用）',
    del_flag            CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by           VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time         DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark              VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (role_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色信息表';

-- ----------------------------
-- 菜单表
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    menu_id     BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    menu_name   VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id   BIGINT DEFAULT 0 COMMENT '父菜单ID',
    order_num   INT DEFAULT 0 COMMENT '显示顺序',
    path        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    component   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    query       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    is_frame    INT DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    is_cache    INT DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
    menu_type   CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    visible     CHAR(1) DEFAULT '0' COMMENT '是否显示（0显示 1隐藏）',
    status      CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    perms       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    icon        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT '' COMMENT '备注',
    del_flag    CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (menu_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='菜单权限表';

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    dept_id     BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id   BIGINT DEFAULT 0 COMMENT '父部门ID',
    ancestors   VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    dept_name   VARCHAR(30) NOT NULL COMMENT '部门名称',
    order_num   INT DEFAULT 0 COMMENT '显示顺序',
    leader      VARCHAR(20) DEFAULT NULL COMMENT '负责人',
    phone       VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
    email       VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    status      CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag    CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='部门表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- ----------------------------
-- 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- ----------------------------
-- 角色部门关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_dept;
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB COMMENT='角色和部门关联表';

-- ----------------------------
-- 索引优化
-- ----------------------------
-- sys_user 表索引
CREATE INDEX idx_user_name ON sys_user(user_name);
CREATE INDEX idx_dept_id ON sys_user(dept_id);
CREATE INDEX idx_status ON sys_user(status);
CREATE INDEX idx_del_flag ON sys_user(del_flag);

-- sys_menu 表索引
CREATE INDEX idx_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_menu_type ON sys_menu(menu_type);
CREATE INDEX idx_visible ON sys_menu(visible);
CREATE INDEX idx_perms ON sys_menu(perms);

-- sys_dept 表索引
CREATE INDEX idx_parent_id_dept ON sys_dept(parent_id);
CREATE INDEX idx_dept_name ON sys_dept(dept_name);

-- sys_role 表索引
CREATE INDEX idx_role_key ON sys_role(role_key);
CREATE INDEX idx_data_scope ON sys_role(data_scope);

-- sys_user_role 表索引
CREATE INDEX idx_user_role_role_id ON sys_user_role(role_id);

-- sys_role_menu 表索引
CREATE INDEX idx_role_menu_menu_id ON sys_role_menu(menu_id);

-- ----------------------------
-- 岗位表
-- ----------------------------
DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
    post_id     BIGINT NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    post_code   VARCHAR(64) NOT NULL UNIQUE COMMENT '岗位编码',
    post_name   VARCHAR(50) NOT NULL COMMENT '岗位名称',
    post_sort   INT NOT NULL COMMENT '显示顺序',
    status      CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    del_flag    CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (post_id),
    INDEX idx_post_code (post_code),
    INDEX idx_post_name (post_name),
    INDEX idx_post_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='岗位信息表';

-- ----------------------------
-- 用户岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_post;
CREATE TABLE sys_user_post (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (user_id, post_id),
    INDEX idx_user_post_post_id (post_id)
) ENGINE=InnoDB COMMENT='用户和岗位关联表';

-- ----------------------------
-- 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    dict_id     BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
    dict_name   VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_type   VARCHAR(100) NOT NULL UNIQUE COMMENT '字典类型',
    status      CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    del_flag    CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (dict_id),
    INDEX idx_dict_type (dict_type),
    INDEX idx_dict_status (status),
    INDEX idx_dict_del_flag (del_flag)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典类型表';

-- ----------------------------
-- 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    dict_code   BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    dict_sort   INT DEFAULT 0 COMMENT '排序号',
    dict_label  VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value  VARCHAR(100) NOT NULL COMMENT '字典值',
    dict_type   VARCHAR(100) NOT NULL COMMENT '字典类型',
    css_class   VARCHAR(100) DEFAULT NULL COMMENT 'CSS样式类',
    list_class  VARCHAR(100) DEFAULT NULL COMMENT '表格样式类',
    is_default  CHAR(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    status      CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    del_flag    CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (dict_code),
    INDEX idx_dict_data_type (dict_type),
    INDEX idx_dict_data_value (dict_value),
    INDEX idx_dict_data_status (status),
    INDEX idx_dict_data_del_flag (del_flag)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='字典数据表';

-- ----------------------------
-- 参数配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    config_id    BIGINT NOT NULL AUTO_INCREMENT COMMENT '参数配置ID',
    config_name  VARCHAR(100) NOT NULL COMMENT '参数名称',
    config_key   VARCHAR(100) NOT NULL UNIQUE COMMENT '参数键名',
    config_value VARCHAR(500) NOT NULL COMMENT '参数键值',
    config_type  CHAR(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    status       CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by    VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by    VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time  DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',
    del_flag     CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (config_id),
    INDEX idx_config_key (config_key),
    INDEX idx_config_name (config_name),
    INDEX idx_config_status (status),
    INDEX idx_config_del_flag (del_flag)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='参数配置表';

-- ----------------------------
-- 通知公告表
-- ----------------------------
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    notice_id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    notice_title   VARCHAR(50) NOT NULL COMMENT '公告标题',
    notice_type    CHAR(1) DEFAULT '1' COMMENT '公告类型（1通知 2公告）',
    notice_content TEXT DEFAULT NULL COMMENT '公告内容',
    status         CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by      VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time    DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    del_flag       CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (notice_id),
    INDEX idx_notice_type (notice_type),
    INDEX idx_notice_status (status),
    INDEX idx_notice_del_flag (del_flag)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='通知公告表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    oper_id         BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    title           VARCHAR(50) DEFAULT '' COMMENT '操作模块',
    business_type   CHAR(1) DEFAULT '0' COMMENT '业务类型（0其他 1新增 2修改 3删除）',
    method          VARCHAR(100) DEFAULT '' COMMENT '方法名称',
    request_method  VARCHAR(10) DEFAULT '' COMMENT '请求方式',
    operator_type   CHAR(1) DEFAULT '0' COMMENT '操作类别（0其他 1后台用户 2手机端用户）',
    oper_name       VARCHAR(50) DEFAULT '' COMMENT '操作人员',
    dept_name       VARCHAR(50) DEFAULT '' COMMENT '部门名称',
    oper_url        VARCHAR(255) DEFAULT '' COMMENT '请求URL',
    oper_ip         VARCHAR(128) DEFAULT '' COMMENT '主机地址',
    oper_location   VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    oper_param      VARCHAR(2000) DEFAULT '' COMMENT '请求参数',
    json_result     VARCHAR(2000) DEFAULT '' COMMENT '返回参数',
    status          INT DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    error_msg       VARCHAR(2000) DEFAULT '' COMMENT '错误信息',
    oper_time       DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (oper_id),
    INDEX idx_oper_log_title (title),
    INDEX idx_oper_log_oper_name (oper_name),
    INDEX idx_oper_log_status (status),
    INDEX idx_oper_log_oper_time (oper_time)
) ENGINE=InnoDB AUTO_INCREMENT=100000 COMMENT='操作日志表';

-- ----------------------------
-- 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    info_id         BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_name       VARCHAR(50) DEFAULT '' COMMENT '用户名称',
    ip_addr         VARCHAR(128) DEFAULT '' COMMENT 'IP地址',
    login_location  VARCHAR(255) DEFAULT '' COMMENT '登录地点',
    browser         VARCHAR(50) DEFAULT '' COMMENT '浏览器类型',
    os              VARCHAR(50) DEFAULT '' COMMENT '操作系统',
    status          CHAR(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    msg             VARCHAR(255) DEFAULT '' COMMENT '提示信息',
    login_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (info_id),
    INDEX idx_login_log_user_name (user_name),
    INDEX idx_login_log_ip_addr (ip_addr),
    INDEX idx_login_log_status (status),
    INDEX idx_login_log_login_time (login_time)
) ENGINE=InnoDB AUTO_INCREMENT=100000 COMMENT='登录日志表';

-- ----------------------------
-- 定时任务表
-- ----------------------------
DROP TABLE IF EXISTS sys_job;
CREATE TABLE sys_job (
    job_id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    job_name        VARCHAR(64) NOT NULL COMMENT '任务名称',
    job_group       VARCHAR(64) DEFAULT 'DEFAULT' COMMENT '任务分组',
    job_class_name  VARCHAR(255) NOT NULL COMMENT '任务执行类全限定名',
    cron_expression VARCHAR(128) NOT NULL COMMENT 'Cron表达式',
    misfire_policy  VARCHAR(20) DEFAULT '3' COMMENT '错过策略（1立即执行 2执行一次 3放弃执行）',
    concurrent      CHAR(1) DEFAULT '1' COMMENT '是否并发（0禁止 1允许）',
    status          CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    del_flag        CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    PRIMARY KEY (job_id),
    INDEX idx_job_name (job_name),
    INDEX idx_job_group (job_group),
    INDEX idx_job_status (status),
    INDEX idx_job_del_flag (del_flag)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='定时任务表';

-- ----------------------------
-- 任务日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_job_log;
CREATE TABLE sys_job_log (
    job_log_id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    job_name        VARCHAR(64) NOT NULL COMMENT '任务名称',
    job_group       VARCHAR(64) DEFAULT 'DEFAULT' COMMENT '任务分组',
    job_class_name  VARCHAR(255) NOT NULL COMMENT '任务执行类全限定名',
    cron_expression VARCHAR(128) NOT NULL COMMENT 'Cron表达式',
    status          CHAR(1) DEFAULT '0' COMMENT '状态（0成功 1失败）',
    error_msg       VARCHAR(2000) DEFAULT '' COMMENT '错误信息',
    job_time        DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (job_log_id),
    INDEX idx_job_log_job_name (job_name),
    INDEX idx_job_log_status (status),
    INDEX idx_job_log_job_time (job_time)
) ENGINE=InnoDB AUTO_INCREMENT=100000 COMMENT='任务日志表';
