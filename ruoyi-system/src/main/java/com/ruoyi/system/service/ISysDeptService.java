package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SysDept;

import java.util.List;

/**
 * 部门业务服务接口
 * <p>
 * 定义部门管理的业务方法，包括 CRUD 和树形查询。
 * </p>
 *
 * @author NingTheshy
 */
public interface ISysDeptService {

    /**
     * 查询部门列表（支持数据权限过滤）
     *
     * @param dept 查询条件
     * @return 部门列表
     */
    List<SysDept> getDeptList(SysDept dept);

    /**
     * 根据 ID 查询部门详情
     *
     * @param deptId 部门 ID
     * @return 部门实体
     */
    SysDept getDeptById(Long deptId);

    /**
     * 新增部门
     * <p>自动拼接 ancestors 路径</p>
     *
     * @param dept 部门实体
     * @return 影响行数
     */
    int createDept(SysDept dept);

    /**
     * 修改部门
     *
     * @param dept 部门实体（必须包含 deptId）
     * @return 影响行数
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门
     * <p>删除前校验：不能有子部门，不能有用户属于该部门</p>
     *
     * @param deptId 部门 ID
     * @return 影响行数
     */
    int deleteDeptById(Long deptId);
}
