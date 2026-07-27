package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.SysPost;

import java.util.List;

public interface SysPostMapper extends BaseMapper<SysPost> {

    List<SysPost> selectPostList(SysPost post);

    SysPost selectPostByCode(String postCode);

    Long countUserPost(Long postId);
}