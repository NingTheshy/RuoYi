package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysDept;

import java.util.List;

public interface SysDeptMapper extends BaseMapper<SysDept> {

    List<SysDept> selectDeptList(SysDept dept);
}
