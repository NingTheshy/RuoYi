package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysDept;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysDeptServiceImpl implements ISysDeptService {

    @Autowired
    private SysDeptMapper deptMapper;

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        return deptMapper.selectDeptList(dept);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return deptMapper.selectDeptById(deptId);
    }

    @Override
    @Transactional
    public int insertDept(SysDept dept) {
        SysDept parent = deptMapper.selectDeptById(dept.getParentId());
        if (parent != null) {
            dept.setAncestors(parent.getAncestors() + "," + parent.getDeptId());
        }
        return deptMapper.insertDept(dept);
    }

    @Override
    @Transactional
    public int updateDept(SysDept dept) {
        return deptMapper.updateDept(dept);
    }

    @Override
    @Transactional
    public int deleteDeptById(Long deptId) {
        SysDept dept = deptMapper.selectDeptById(deptId);
        if (dept == null) {
            throw new ServiceException("部门不存在");
        }
        return deptMapper.deleteDeptById(deptId);
    }
}
