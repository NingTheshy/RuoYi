package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.SysDept;

import java.util.List;

public interface ISysDeptService {

    List<SysDept> selectDeptList(SysDept dept);

    SysDept selectDeptById(Long deptId);

    int insertDept(SysDept dept);

    int updateDept(SysDept dept);

    int deleteDeptById(Long deptId);
}
