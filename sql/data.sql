-- data.sql
-- 导入建议：
-- 1. 含中文初始化数据，命令行导入时请使用 utf8mb4 字符集，例如：
--    mysql --default-character-set=utf8mb4 -u root -p < data.sql
USE `ry`;

-- ----------------------------
-- 初始数据 - 部门
-- ----------------------------
INSERT INTO sys_dept VALUES (100, 0, '0', '若依科技', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO sys_dept VALUES (101, 100, '0,100', '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO sys_dept VALUES (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO sys_dept VALUES (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO sys_dept VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', NOW(), '', NULL, NULL);

-- ----------------------------
-- 初始数据 - 用户 (密码: admin123)
-- ----------------------------
INSERT INTO sys_user VALUES (1, 103, 'admin', '若依管理员', 'ry@163.com', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '管理员');
INSERT INTO sys_user VALUES (2, 103, 'ryou', '若依', 'ry@qq.com', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '测试员');

-- ----------------------------
-- 初始数据 - 角色
-- ----------------------------
INSERT INTO sys_role VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '超级管理员');
INSERT INTO sys_role VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '普通角色（默认查看研发部门数据）');

-- ----------------------------
-- 初始数据 - 用户角色关联
-- ----------------------------
INSERT INTO sys_user_role VALUES (1, 1);
INSERT INTO sys_user_role VALUES (2, 2);

-- ----------------------------
-- 初始数据 - 菜单
-- ----------------------------
-- 一级菜单 - 系统管理
INSERT INTO sys_menu VALUES (1, '系统管理', 0, 1, 'system', NULL, NULL, 1, 0, 'M', '0', '0', '', 'system', 'admin', NOW(), '', NULL, '系统管理目录', '0');
-- 二级菜单
INSERT INTO sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW(), '', NULL, '用户管理菜单', '0');
INSERT INTO sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', NOW(), '', NULL, '角色管理菜单', '0');
INSERT INTO sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', NOW(), '', NULL, '菜单管理菜单', '0');
INSERT INTO sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', NOW(), '', NULL, '部门管理菜单', '0');

-- 用户管理按钮
INSERT INTO sys_menu VALUES (1001, '用户查询', 100, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1002, '用户新增', 100, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1003, '用户修改', 100, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1004, '用户删除', 100, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1005, '重置密码', 100, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', NOW(), '', NULL, '', '0');

-- 角色管理按钮
INSERT INTO sys_menu VALUES (1011, '角色查询', 101, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1012, '角色新增', 101, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1013, '角色修改', 101, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1014, '角色删除', 101, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- 菜单管理按钮
INSERT INTO sys_menu VALUES (1021, '菜单查询', 102, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1022, '菜单新增', 102, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1023, '菜单修改', 102, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1024, '菜单删除', 102, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- 部门管理按钮
INSERT INTO sys_menu VALUES (1031, '部门查询', 103, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1032, '部门新增', 103, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1033, '部门修改', 103, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1034, '部门删除', 103, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 角色菜单关联 (管理员拥有所有菜单)
-- ----------------------------
INSERT INTO sys_role_menu SELECT 1, menu_id FROM sys_menu;

-- 普通角色只拥有查看权限
INSERT INTO sys_role_menu VALUES (2, 1);
INSERT INTO sys_role_menu VALUES (2, 100);
INSERT INTO sys_role_menu VALUES (2, 1001);
INSERT INTO sys_role_menu VALUES (2, 101);
INSERT INTO sys_role_menu VALUES (2, 1011);
INSERT INTO sys_role_menu VALUES (2, 102);
INSERT INTO sys_role_menu VALUES (2, 1021);
INSERT INTO sys_role_menu VALUES (2, 103);
INSERT INTO sys_role_menu VALUES (2, 1031);

-- ----------------------------
-- 初始数据 - 角色部门关联
-- ----------------------------
-- 普通角色使用“自定义数据权限”，默认可查看研发部门（dept_id=103）数据
INSERT INTO sys_role_dept VALUES (2, 103);

-- ----------------------------
-- 初始数据 - 岗位
-- ----------------------------
INSERT INTO sys_post VALUES (1, 'admin', '管理员', 1, '0', 'admin', NOW(), '', NULL, '管理员岗位', '0');
INSERT INTO sys_post VALUES (2, 'developer', '开发工程师', 2, '0', 'admin', NOW(), '', NULL, '开发岗位', '0');
INSERT INTO sys_post VALUES (3, 'tester', '测试工程师', 3, '0', 'admin', NOW(), '', NULL, '测试岗位', '0');

-- ----------------------------
-- 初始数据 - 用户岗位关联
-- ----------------------------
INSERT INTO sys_user_post VALUES (1, 1);
INSERT INTO sys_user_post VALUES (2, 2);

-- ----------------------------
-- 初始数据 - 字典类型
-- ----------------------------
INSERT INTO sys_dict_type VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', NOW(), '', NULL, '用户性别字典');
INSERT INTO sys_dict_type VALUES (2, '用户状态', 'sys_user_status', '0', 'admin', NOW(), '', NULL, '用户状态字典');
INSERT INTO sys_dict_type VALUES (3, '菜单状态', 'sys_show_hide', '0', 'admin', NOW(), '', NULL, '菜单状态字典');

-- ----------------------------
-- 初始数据 - 字典数据
-- ----------------------------
INSERT INTO sys_dict_data VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_dict_data VALUES (4, 1, '正常', '0', 'sys_user_status', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES (5, 2, '停用', '1', 'sys_user_status', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_dict_data VALUES (6, 1, '显示', '0', 'sys_show_hide', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_dict_data VALUES (7, 2, '隐藏', '1', 'sys_show_hide', '', '', 'N', '0', 'admin', NOW(), '', NULL, '');

-- ----------------------------
-- 初始数据 - 岗位管理菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', NOW(), '', NULL, '岗位管理菜单', '0');

INSERT INTO sys_menu VALUES (1041, '岗位查询', 104, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1042, '岗位新增', 104, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1043, '岗位修改', 104, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1044, '岗位删除', 104, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 字典管理菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:type:list', 'dict', 'admin', NOW(), '', NULL, '字典管理菜单', '0');

INSERT INTO sys_menu VALUES (1051, '字典类型查询', 105, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:type:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1052, '字典类型新增', 105, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:type:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1053, '字典类型修改', 105, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:type:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1054, '字典类型删除', 105, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:type:remove', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1055, '字典数据查询', 105, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:data:list', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1056, '字典数据新增', 105, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:data:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1057, '字典数据修改', 105, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:data:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1058, '字典数据删除', 105, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:dict:data:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 角色菜单关联 (管理员新增岗位和字典菜单)
-- ----------------------------
INSERT INTO sys_role_menu VALUES (1, 104);
INSERT INTO sys_role_menu VALUES (1, 1041);
INSERT INTO sys_role_menu VALUES (1, 1042);
INSERT INTO sys_role_menu VALUES (1, 1043);
INSERT INTO sys_role_menu VALUES (1, 1044);
INSERT INTO sys_role_menu VALUES (1, 105);
INSERT INTO sys_role_menu VALUES (1, 1051);
INSERT INTO sys_role_menu VALUES (1, 1052);
INSERT INTO sys_role_menu VALUES (1, 1053);
INSERT INTO sys_role_menu VALUES (1, 1054);
INSERT INTO sys_role_menu VALUES (1, 1055);
INSERT INTO sys_role_menu VALUES (1, 1056);
INSERT INTO sys_role_menu VALUES (1, 1057);
INSERT INTO sys_role_menu VALUES (1, 1058);

-- 普通角色只拥有查看权限
INSERT INTO sys_role_menu VALUES (2, 104);
INSERT INTO sys_role_menu VALUES (2, 1041);
INSERT INTO sys_role_menu VALUES (2, 105);
INSERT INTO sys_role_menu VALUES (2, 1051);
INSERT INTO sys_role_menu VALUES (2, 1055);

-- ----------------------------
-- 初始数据 - 参数配置
-- ----------------------------
INSERT INTO sys_config VALUES (1, '系统名称', 'sysName', 'RuoYi', 'Y', '0', 'admin', NOW(), '', NULL, '系统名称配置', '0');
INSERT INTO sys_config VALUES (2, '系统版权', 'sysCopyright', 'Copyright 2026 RuoYi', 'Y', '0', 'admin', NOW(), '', NULL, '系统版权信息', '0');
INSERT INTO sys_config VALUES (3, '系统Logo', 'sysLogo', '', 'Y', '0', 'admin', NOW(), '', NULL, '系统Logo路径', '0');
INSERT INTO sys_config VALUES (4, '是否开启验证码', 'sysCaptchaEnabled', 'false', 'Y', '0', 'admin', NOW(), '', NULL, '登录验证码开关', '0');
INSERT INTO sys_config VALUES (5, '密码策略', 'sysPasswordPolicy', 'medium', 'Y', '0', 'admin', NOW(), '', NULL, '密码复杂度策略', '0');

-- ----------------------------
-- 初始数据 - 通知公告
-- ----------------------------
INSERT INTO sys_notice VALUES (1, '欢迎使用RuoYi', '2', '<p>欢迎使用RuoYi权限管理系统！</p><p>系统提供用户管理、角色管理、菜单管理、部门管理等功能。</p>', '0', 'admin', NOW(), '', NULL, '系统欢迎公告', '0');
INSERT INTO sys_notice VALUES (2, '系统升级通知', '1', '<p>系统将于今晚22:00进行升级维护，请提前保存数据。</p>', '0', 'admin', NOW(), '', NULL, '系统维护通知', '0');

-- ----------------------------
-- 初始数据 - 参数配置菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (106, '参数配置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', NOW(), '', NULL, '参数配置菜单', '0');

INSERT INTO sys_menu VALUES (1061, '参数查询', 106, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1062, '参数新增', 106, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1063, '参数修改', 106, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1064, '参数删除', 106, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 通知公告菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', NOW(), '', NULL, '通知公告菜单', '0');

INSERT INTO sys_menu VALUES (1071, '公告查询', 107, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1072, '公告新增', 107, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1073, '公告修改', 107, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1074, '公告删除', 107, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 角色菜单关联 (管理员新增参数和公告菜单)
-- ----------------------------
INSERT INTO sys_role_menu VALUES (1, 106);
INSERT INTO sys_role_menu VALUES (1, 1061);
INSERT INTO sys_role_menu VALUES (1, 1062);
INSERT INTO sys_role_menu VALUES (1, 1063);
INSERT INTO sys_role_menu VALUES (1, 1064);
INSERT INTO sys_role_menu VALUES (1, 107);
INSERT INTO sys_role_menu VALUES (1, 1071);
INSERT INTO sys_role_menu VALUES (1, 1072);
INSERT INTO sys_role_menu VALUES (1, 1073);
INSERT INTO sys_role_menu VALUES (1, 1074);

-- 普通角色只拥有查看权限
INSERT INTO sys_role_menu VALUES (2, 106);
INSERT INTO sys_role_menu VALUES (2, 1061);
INSERT INTO sys_role_menu VALUES (2, 107);
INSERT INTO sys_role_menu VALUES (2, 1071);

-- ----------------------------
-- 初始数据 - 监控目录菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (2, '监控管理', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, '监控管理目录', '0');

-- ----------------------------
-- 初始数据 - 操作日志菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (108, '操作日志', 2, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', NOW(), '', NULL, '操作日志菜单', '0');

INSERT INTO sys_menu VALUES (1081, '日志查询', 108, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1082, '日志删除', 108, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1083, '清空日志', 108, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'monitor:operlog:clean', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 登录日志菜单
-- ----------------------------
INSERT INTO sys_menu VALUES (109, '登录日志', 2, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', NOW(), '', NULL, '登录日志菜单', '0');

INSERT INTO sys_menu VALUES (1091, '日志删除', 109, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', NOW(), '', NULL, '', '0');
INSERT INTO sys_menu VALUES (1092, '清空日志', 109, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'monitor:logininfor:clean', '#', 'admin', NOW(), '', NULL, '', '0');

-- ----------------------------
-- 初始数据 - 角色菜单关联 (管理员新增监控日志菜单)
-- ----------------------------
INSERT INTO sys_role_menu VALUES (1, 2);
INSERT INTO sys_role_menu VALUES (1, 108);
INSERT INTO sys_role_menu VALUES (1, 1081);
INSERT INTO sys_role_menu VALUES (1, 1082);
INSERT INTO sys_role_menu VALUES (1, 1083);
INSERT INTO sys_role_menu VALUES (1, 109);
INSERT INTO sys_role_menu VALUES (1, 1091);
INSERT INTO sys_role_menu VALUES (1, 1092);

-- 普通角色只拥有查看权限
INSERT INTO sys_role_menu VALUES (2, 2);
INSERT INTO sys_role_menu VALUES (2, 108);
INSERT INTO sys_role_menu VALUES (2, 1081);
INSERT INTO sys_role_menu VALUES (2, 109);
