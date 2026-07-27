package com.ruoyi.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类
 * <p>
 * 所有数据库实体的公共父类，包含审计字段和通用属性。
 * 使用 MyBatis-Plus 的自动填充功能（{@link com.ruoyi.common.mybatis.handler.MyMetaObjectHandler}），
 * 在 insert/update 时自动设置 createBy、createTime、updateBy、updateTime。
 * </p>
 *
 * <p>子类通过 {@code @EqualsAndHashCode(callSuper = true)} 继承这些字段。</p>
 *
 * @author NingTheshy
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建者（插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 更新者（插入和更新时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 创建时间（插入时自动填充） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（插入和更新时自动填充） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;
}
