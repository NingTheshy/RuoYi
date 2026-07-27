package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysDictTypeCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictTypeUpdateReq;
import com.ruoyi.system.domain.dto.resp.SysDictTypeResp;
import com.ruoyi.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "字典类型管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    public SysDictTypeController(SysDictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    @Operation(summary = "分页查询字典类型列表")
    @PreAuthorize("hasAuthority('system:dict:type:list')")
    @GetMapping("/list")
    public R<PageResult<SysDictTypeResp>> list(@Valid @ParameterObject SysDictTypeQueryReq queryReq,
                                               @Parameter(description = "页码", example = "1")
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @Parameter(description = "每页条数", example = "10")
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dictTypeService.getDictTypePage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "新增字典类型")
    @PreAuthorize("hasAuthority('system:dict:type:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDictTypeCreateReq req) {
        dictTypeService.createDictType(req);
        return R.ok();
    }

    @Operation(summary = "修改字典类型")
    @PreAuthorize("hasAuthority('system:dict:type:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDictTypeUpdateReq req) {
        dictTypeService.updateDictType(req);
        return R.ok();
    }

    @Operation(summary = "批量删除字典类型")
    @PreAuthorize("hasAuthority('system:dict:type:remove')")
    @DeleteMapping("/{dictIds}")
    public R<Void> remove(@PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return R.ok();
    }
}