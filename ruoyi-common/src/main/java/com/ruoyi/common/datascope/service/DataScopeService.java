package com.ruoyi.common.datascope.service;

import com.ruoyi.common.datascope.context.DataScopeContext;

/**
 * 数据权限服务
 */
public interface DataScopeService {

    DataScopeContext buildDataScopeCondition(String alias,
                                             String userIdColumn,
                                             String deptIdColumn,
                                             boolean enableUserScope);
}
