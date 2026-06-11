package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysDept;

import java.util.List;

/**
 * 部门数据访问层
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，提供基础 CRUD 方法。
 * 自定义查询方法对应 SysDeptMapper.xml 中的 SQL 语句。
 * </p>
 *
 * @author NingTheshy
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门列表（支持条件筛选）
     * <p>
     * 对应 XML 中的 selectDeptList，支持按 deptName 和 status 模糊/精确查询。
     * </p>
     *
     * @param dept 查询条件（可选：deptName、status）
     * @return 部门列表，按 parentId 和 orderNum 排序
     */
    List<SysDept> selectDeptList(SysDept dept);
}
