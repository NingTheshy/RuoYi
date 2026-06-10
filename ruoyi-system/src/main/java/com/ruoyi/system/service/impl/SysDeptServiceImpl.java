package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysDept;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    @DataScope(alias = "", userIdColumn = "dept_id", deptIdColumn = "dept_id")
    public List<SysDept> selectDeptList(SysDept dept) {
        return baseMapper.selectDeptList(dept);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return getById(deptId);
    }

    @Override
    @Transactional
    public int insertDept(SysDept dept) {
        SysDept parent = getById(dept.getParentId());
        if (parent != null) {
            dept.setAncestors(parent.getAncestors() + "," + parent.getDeptId());
        }
        return save(dept) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateDept(SysDept dept) {
        return updateById(dept) ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteDeptById(Long deptId) {
        SysDept dept = getById(deptId);
        if (dept == null) {
            throw new ServiceException("部门不存在");
        }
        return removeById(deptId) ? 1 : 0;
    }
}
