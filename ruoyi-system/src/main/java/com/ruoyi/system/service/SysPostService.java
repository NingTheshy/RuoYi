package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.system.domain.dto.req.SysPostCreateReq;
import com.ruoyi.system.domain.dto.req.SysPostQueryReq;
import com.ruoyi.system.domain.dto.req.SysPostUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysPostResp;

import java.util.List;

public interface SysPostService {
    PageResult<SysPostResp> getPostPage(SysPostQueryReq queryReq, Integer pageNum, Integer pageSize);
    List<SysPostResp> getPostList(SysPostQueryReq queryReq);
    SysPostResp getPostById(Long postId);
    int createPost(SysPostCreateReq req);
    int updatePost(SysPostUpdateReq req);
    int deletePostByIds(Long[] postIds);
}