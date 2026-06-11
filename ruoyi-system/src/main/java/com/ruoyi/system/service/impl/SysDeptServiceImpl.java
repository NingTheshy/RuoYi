package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.entity.SysDept;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门业务服务实现类
 * <p>
 * 实现部门的 CRUD 操作，包含以下业务规则：
 * </p>
 * <ul>
 *   <li>新增部门时自动拼接 ancestors 路径</li>
 *   <li>删除部门前校验：不能有子部门、不能有用户属于该部门</li>
 *   <li>查询部门列表支持数据权限过滤（@DataScope）</li>
 * </ul>
 *
 * @author NingTheshy
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询部门列表（支持数据权限过滤）
     * <p>
     * 标注了 @DataScope 注解，切面会根据当前用户角色自动注入 SQL 条件，
     * 实现行级数据隔离。
     * </p>
     */
    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id")
    public List<SysDept> getDeptList(SysDept dept) {
        return baseMapper.selectDeptList(dept);
    }

    @Override
    public SysDept getDeptById(Long deptId) {
        return getById(deptId);
    }

    /**
     * 新增部门
     * <p>
     * 如果有父部门（parentId != 0），自动拼接 ancestors 路径：
     * 父部门的 ancestors + "," + 父部门 ID
     * </p>
     */
    @Override
    @Transactional
    public int createDept(SysDept dept) {
        SysDept parent = getById(dept.getParentId());
        if (parent != null) {
            // 有父部门：ancestors = 父部门的ancestors + "," + 父部门ID
            dept.setAncestors(parent.getAncestors() + "," + parent.getDeptId());
        } else {
            // 顶级部门（parentId=0）：ancestors 设为 "0"
            dept.setAncestors("0");
        }
        return save(dept) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateDept(SysDept dept) {
        return updateById(dept) ? 1 : 0;
    }

    /**
     * 删除部门
     * <p>
     * 删除前校验：
     * 1. 部门是否存在
     * 2. 是否有子部门（parent_id = deptId 的记录数 > 0）
     * 3. 是否有用户属于该部门（dept_id = deptId 的用户数 > 0）
     * </p>
     */
    @Override
    @Transactional
    public int deleteDeptById(Long deptId) {
        SysDept dept = getById(deptId);
        if (dept == null) {
            throw new ServiceException("部门不存在");
        }
        // 检查是否存在子部门
        Long childCount = count(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, deptId));
        if (childCount > 0) {
            throw new ServiceException("存在下级部门，不允许删除");
        }
        // 检查是否有用户属于该部门
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, deptId));
        if (userCount > 0) {
            throw new ServiceException("部门下存在用户，不允许删除");
        }
        return removeById(deptId) ? 1 : 0;
    }
}
