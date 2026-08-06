package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysNoticeCreateReq;
import com.ruoyi.system.domain.dto.req.SysNoticeQueryReq;
import com.ruoyi.system.domain.dto.req.SysNoticeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysNoticeResp;
import com.ruoyi.system.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知公告管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    private final SysNoticeService noticeService;

    public SysNoticeController(SysNoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Operation(summary = "分页查询通知公告列表")
    @PreAuthorize("hasAuthority('system:notice:list')")
    @GetMapping("/list")
    public R<PageResult<SysNoticeResp>> list(@Valid @ParameterObject SysNoticeQueryReq queryReq,
                                             @Parameter(description = "页码", example = "1")
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @Parameter(description = "每页条数", example = "10")
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(noticeService.getNoticePage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询通知公告详情")
    @PreAuthorize("hasAuthority('system:notice:query')")
    @GetMapping("/{noticeId}")
    public R<SysNoticeResp> getInfo(@PathVariable Long noticeId) {
        return R.ok(noticeService.getNoticeById(noticeId));
    }

    @Operation(summary = "新增通知公告")
    @PreAuthorize("hasAuthority('system:notice:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysNoticeCreateReq req) {
        noticeService.createNotice(req);
        return R.ok();
    }

    @Operation(summary = "修改通知公告")
    @PreAuthorize("hasAuthority('system:notice:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysNoticeUpdateReq req) {
        noticeService.updateNotice(req);
        return R.ok();
    }

    @Operation(summary = "批量删除通知公告")
    @PreAuthorize("hasAuthority('system:notice:remove')")
    @DeleteMapping("/{noticeIds}")
    public R<Void> remove(@PathVariable Long[] noticeIds) {
        noticeService.deleteNoticeByIds(noticeIds);
        return R.ok();
    }
}
