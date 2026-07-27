package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.enums.StatusFlag;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.SysPostConvert;
import com.ruoyi.system.domain.dto.req.SysPostCreateReq;
import com.ruoyi.system.domain.dto.req.SysPostQueryReq;
import com.ruoyi.system.domain.dto.req.SysPostUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysPostResp;
import com.ruoyi.system.domain.entity.SysPost;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.service.SysPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {

    private final SysPostConvert postConvert;

    public SysPostServiceImpl(SysPostConvert postConvert) {
        this.postConvert = postConvert;
    }

    @Override
    public PageResult<SysPostResp> getPostPage(SysPostQueryReq queryReq, Integer pageNum, Integer pageSize) {
        SysPost query = postConvert.toEntity(queryReq);
        Page<SysPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPostName()), SysPost::getPostName, query.getPostName())
                .like(StringUtils.hasText(query.getPostCode()), SysPost::getPostCode, query.getPostCode())
                .eq(StringUtils.hasText(query.getStatus()), SysPost::getStatus, query.getStatus())
                .orderByAsc(SysPost::getPostSort);
        Page<SysPost> result = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(postConvert.toRespList(result.getRecords()), result.getTotal());
    }

    @Override
    public List<SysPostResp> getPostList(SysPostQueryReq queryReq) {
        SysPost query = postConvert.toEntity(queryReq);
        return postConvert.toRespList(baseMapper.selectPostList(query));
    }

    @Override
    public SysPostResp getPostById(Long postId) {
        SysPost post = getById(postId);
        if (post == null) {
            throw new ServiceException(404, "岗位不存在");
        }
        return postConvert.toResp(post);
    }

    @Override
    @Transactional
    public int createPost(SysPostCreateReq req) {
        SysPost post = postConvert.toEntity(req);
        assertPostCodeUnique(post.getPostCode(), null);
        if (post.getStatus() == null) {
            post.setStatus(StatusFlag.NORMAL.getCode());
        }
        if (post.getPostSort() == null) {
            post.setPostSort(0);
        }
        return save(post) ? 1 : 0;
    }

    @Override
    @Transactional
    public int updatePost(SysPostUpdateReq req) {
        SysPost existing = getById(req.getPostId());
        if (existing == null) {
            throw new ServiceException(404, "岗位不存在");
        }
        SysPost post = postConvert.toEntity(req);
        assertPostCodeUnique(post.getPostCode(), post.getPostId());
        return updateById(post) ? 1 : 0;
    }

    @Override
    @Transactional
    public int deletePostByIds(Long[] postIds) {
        for (Long postId : postIds) {
            Long count = baseMapper.countUserPost(postId);
            if (count != null && count > 0) {
                throw new ServiceException("存在关联用户，不允许删除");
            }
        }
        return removeByIds(Arrays.asList(postIds)) ? postIds.length : 0;
    }

    private void assertPostCodeUnique(String postCode, Long excludePostId) {
        SysPost existing = baseMapper.selectPostByCode(postCode);
        if (existing != null && !existing.getPostId().equals(excludePostId)) {
            throw new ServiceException(400, "岗位编码'" + postCode + "'已存在");
        }
    }
}