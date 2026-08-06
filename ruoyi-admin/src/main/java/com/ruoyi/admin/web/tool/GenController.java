package com.ruoyi.admin.web.tool;

import com.ruoyi.common.core.annotation.OperLog;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.GenTableCreateReq;
import com.ruoyi.system.domain.dto.req.GenTableQueryReq;
import com.ruoyi.system.domain.dto.req.GenTableUpdateReq;
import com.ruoyi.system.domain.dto.req.GenSyncReq;
import com.ruoyi.system.domain.dto.resp.GenPreviewResp;
import com.ruoyi.system.domain.dto.resp.GenTableResp;
import com.ruoyi.system.service.GenTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "代码生成")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/tool/gen")
public class GenController {

    private final GenTableService genTableService;

    public GenController(GenTableService genTableService) {
        this.genTableService = genTableService;
    }

    @Operation(summary = "分页查询生成表列表")
    @PreAuthorize("hasAuthority('tool:gen:list')")
    @GetMapping("/list")
    public R<PageResult<GenTableResp>> list(@Valid @ParameterObject GenTableQueryReq queryReq,
                                             @Parameter(description = "页码", example = "1")
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @Parameter(description = "每页条数", example = "10")
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(genTableService.getTablePage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "查询生成表详情")
    @PreAuthorize("hasAuthority('tool:gen:query')")
    @GetMapping("/{tableId}")
    public R<GenTableResp> getInfo(@PathVariable Long tableId) {
        return R.ok(genTableService.getTableById(tableId));
    }

    @OperLog(title = "代码生成", businessType = "1")
    @Operation(summary = "新增生成表配置")
    @PreAuthorize("hasAuthority('tool:gen:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody GenTableCreateReq req) {
        genTableService.createTable(req);
        return R.ok();
    }

    @OperLog(title = "代码生成", businessType = "2")
    @Operation(summary = "修改生成表配置")
    @PreAuthorize("hasAuthority('tool:gen:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody GenTableUpdateReq req) {
        genTableService.updateTable(req);
        return R.ok();
    }

    @OperLog(title = "代码生成", businessType = "3")
    @Operation(summary = "删除生成表配置")
    @PreAuthorize("hasAuthority('tool:gen:remove')")
    @DeleteMapping("/{tableIds}")
    public R<Void> remove(@PathVariable Long[] tableIds) {
        genTableService.deleteTableByIds(tableIds);
        return R.ok();
    }

    @Operation(summary = "预览生成代码")
    @PreAuthorize("hasAuthority('tool:gen:preview')")
    @GetMapping("/preview/{tableId}")
    public R<GenPreviewResp> preview(@PathVariable Long tableId) {
        return R.ok(genTableService.previewCode(tableId));
    }

    @OperLog(title = "代码生成", businessType = "1")
    @Operation(summary = "生成代码（下载ZIP）")
    @PreAuthorize("hasAuthority('tool:gen:code')")
    @PostMapping("/download/{tableId}")
    public void download(@PathVariable Long tableId, HttpServletResponse response) throws IOException {
        byte[] data = genTableService.generateCode(tableId);
        response.reset();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + URLEncoder.encode("ruoyi.zip", StandardCharsets.UTF_8));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setContentLength(data.length);
        try (OutputStream os = response.getOutputStream()) {
            os.write(data);
            os.flush();
        }
    }

    @OperLog(title = "代码生成", businessType = "1")
    @Operation(summary = "同步数据库表")
    @PreAuthorize("hasAuthority('tool:gen:sync')")
    @PostMapping("/sync")
    public R<Void> sync(@Valid @RequestBody GenSyncReq req) {
        genTableService.syncTables(req);
        return R.ok();
    }
}
