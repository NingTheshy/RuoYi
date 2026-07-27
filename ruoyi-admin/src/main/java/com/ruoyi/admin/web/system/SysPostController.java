package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysPostCreateReq;
import com.ruoyi.system.domain.dto.req.SysPostQueryReq;
import com.ruoyi.system.domain.dto.req.SysPostUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysPostResp;
import com.ruoyi.system.service.SysPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "岗位管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/post")
public class SysPostController {

    private final SysPostService postService;

    public SysPostController(SysPostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "分页查询岗位列表")
    @PreAuthorize("hasAuthority('system:post:list')")
    @GetMapping("/list")
    public R<PageResult<SysPostResp>> list(@Valid @ParameterObject SysPostQueryReq queryReq,
                                           @Parameter(description = "页码", example = "1")
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @Parameter(description = "每页条数", example = "10")
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(postService.getPostPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询岗位详情")
    @PreAuthorize("hasAuthority('system:post:query')")
    @GetMapping("/{postId}")
    public R<SysPostResp> getInfo(@PathVariable Long postId) {
        return R.ok(postService.getPostById(postId));
    }

    @Operation(summary = "新增岗位")
    @PreAuthorize("hasAuthority('system:post:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysPostCreateReq req) {
        postService.createPost(req);
        return R.ok();
    }

    @Operation(summary = "修改岗位")
    @PreAuthorize("hasAuthority('system:post:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysPostUpdateReq req) {
        postService.updatePost(req);
        return R.ok();
    }

    @Operation(summary = "批量删除岗位")
    @PreAuthorize("hasAuthority('system:post:remove')")
    @DeleteMapping("/{postIds}")
    public R<Void> remove(@PathVariable Long[] postIds) {
        postService.deletePostByIds(postIds);
        return R.ok();
    }
}