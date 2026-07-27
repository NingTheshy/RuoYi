package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysDeptConvert;
import com.ruoyi.system.domain.dto.req.SysDeptCreateReq;
import com.ruoyi.system.domain.dto.req.SysDeptQueryReq;
import com.ruoyi.system.domain.dto.req.SysDeptUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDeptResp;
import com.ruoyi.system.domain.entity.SysDept;
import com.ruoyi.system.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.SysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    private final SysUserMapper userMapper;

    private final SysDeptConvert deptConvert;

    public SysDeptServiceImpl(SysUserMapper userMapper, SysDeptConvert deptConvert) {
        this.userMapper = userMapper;
        this.deptConvert = deptConvert;
    }

    /**
     * 查询部门列表（支持数据权限过滤）
     * <p>
     * 标注了 @DataScope 注解，切面会根据当前用户角色自动注入 SQL 条件，
     * 实现行级数据隔离。
     * </p>
     */
    @Override
    @DataScope(alias = "", userIdColumn = "user_id", deptIdColumn = "dept_id", enableUserScope = false)
    public List<SysDeptResp> getDeptList(SysDeptQueryReq queryReq) {
        SysDept dept = deptConvert.toEntity(queryReq);
        return deptConvert.toRespList(baseMapper.selectDeptList(dept));
    }

    @Override
    public SysDeptResp getDeptById(Long deptId) {
        SysDept dept = getById(deptId);
        if (dept == null) {
            throw new ServiceException(404, "部门不存在");
        }
        return deptConvert.toResp(dept);
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
    public int createDept(SysDeptCreateReq req) {
        SysDept dept = deptConvert.toEntity(req);
        Long parentId = normalizeParentId(dept.getParentId());
        dept.setParentId(parentId);
        dept.setAncestors(resolveAncestors(parentId));
        if (!save(dept)) {
            throw new ServiceException(500, "创建部门失败");
        }
        return 1;
    }

    @Override
    @Transactional
    public int updateDept(SysDeptUpdateReq req) {
        SysDept existing = getById(req.getDeptId());
        if (existing == null) {
            throw new ServiceException(404, "部门不存在");
        }

        SysDept dept = deptConvert.toEntity(req);
        Long parentId = normalizeParentId(dept.getParentId());
        validateParent(existing.getDeptId(), parentId);

        String oldAncestors = existing.getAncestors();
        String newAncestors = resolveAncestors(parentId);
        dept.setParentId(parentId);
        dept.setAncestors(newAncestors);

        if (!updateById(dept)) {
            throw new ServiceException(500, "修改部门失败");
        }
        updateChildrenAncestors(existing.getDeptId(), oldAncestors, newAncestors);
        return 1;
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
            throw new ServiceException(404, "部门不存在");
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

    private Long normalizeParentId(Long parentId) {
        return parentId == null ? 0L : parentId;
    }

    private String resolveAncestors(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return "0";
        }
        SysDept parent = getById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父部门不存在");
        }
        return parent.getAncestors() + "," + parent.getDeptId();
    }

    private void validateParent(Long deptId, Long parentId) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        if (Objects.equals(deptId, parentId)) {
            throw new ServiceException(400, "上级部门不能是自己");
        }

        SysDept parent = getById(parentId);
        if (parent == null) {
            throw new ServiceException(404, "父部门不存在");
        }
        String parentAncestors = parent.getAncestors();
        if (String.valueOf(deptId).equals(parentAncestors)
                || (parentAncestors != null && List.of(parentAncestors.split(",")).contains(String.valueOf(deptId)))) {
            throw new ServiceException(400, "上级部门不能是当前部门或其下级部门");
        }
    }

    private void updateChildrenAncestors(Long deptId, String oldAncestors, String newAncestors) {
        String oldPath = buildDeptPath(oldAncestors, deptId);
        String newPath = buildDeptPath(newAncestors, deptId);
        List<SysDept> children = list(new LambdaQueryWrapper<SysDept>()
                .apply("FIND_IN_SET({0}, ancestors)", deptId));
        for (SysDept child : children) {
            String childAncestors = child.getAncestors();
            if (childAncestors != null && childAncestors.contains(oldPath)) {
                child.setAncestors(childAncestors.replace(oldPath, newPath));
            }
        }
        if (!children.isEmpty()) {
            updateBatchById(children);
        }
    }

    private String buildDeptPath(String ancestors, Long deptId) {
        return ancestors + "," + deptId;
    }
}
