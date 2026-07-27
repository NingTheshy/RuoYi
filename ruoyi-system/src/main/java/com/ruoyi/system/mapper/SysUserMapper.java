package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层
 * <p>
 * 继承 MyBatis-Plus 的 {@link BaseMapper}，提供基础 CRUD 方法。
 * 自定义查询方法对应 SysUserMapper.xml 中的 SQL 语句。
 * </p>
 *
 * @author NingTheshy
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     * <p>用于登录认证和注册时检查用户名唯一性</p>
     *
     * @param userName 用户名
     * @return 用户实体，不存在时返回 null
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 查询用户列表（支持条件筛选）
     *
     * @param user 查询条件（可选：userName、status、deptId）
     * @return 用户列表，按 createTime 排序
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 更新用户登录信息
     * <p>登录成功后记录登录 IP 和登录时间</p>
     *
     * @param userId  用户 ID
     * @param loginIp 登录 IP 地址
     * @param loginDate 登录时间
     * @return 更新的记录数
     */
    int updateUserLoginInfo(@Param("userId") Long userId,
                            @Param("loginIp") String loginIp,
                            @Param("loginDate") LocalDateTime loginDate);

    /**
     * 插入用户-角色关联
     * <p>使用 INSERT IGNORE 避免重复关联时报错</p>
     *
     * @param userId 用户 ID
     * @param roleId 角色 ID
     * @return 插入的记录数
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色关联
     * <p>用于重新分配角色时先清除旧关联</p>
     *
     * @param userId 用户 ID
     * @return 删除的记录数
     */
    int deleteUserRoles(Long userId);

    /**
     * 查询用户的角色 ID 列表
     * <p>用于用户编辑时，前端回显已勾选的角色</p>
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    List<Long> selectUserRoleIds(Long userId);

    /**
     * 查询用户所属部门 ID
     *
     * @param userId 用户 ID
     * @return 部门 ID
     */
    Long selectDeptIdByUserId(Long userId);
}
