package com.ruoyi.admin.web.system;

import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.req.SysDictDataCreateReq;
import com.ruoyi.system.domain.dto.req.SysDictDataQueryReq;
import com.ruoyi.system.domain.dto.req.SysDictDataUpdateReq;
import com.ruoyi.system.domain.dto.resp.DictDataOptionResp;
import com.ruoyi.system.domain.dto.resp.SysDictDataResp;
import com.ruoyi.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典数据管理")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    public SysDictDataController(SysDictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    @Operation(summary = "分页查询字典数据列表")
    @PreAuthorize("hasAuthority('system:dict:data:list')")
    @GetMapping("/list")
    public R<PageResult<SysDictDataResp>> list(@Valid @ParameterObject SysDictDataQueryReq queryReq,
                                               @Parameter(description = "页码", example = "1")
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @Parameter(description = "每页条数", example = "10")
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(dictDataService.getDictDataPage(queryReq, pageNum, pageSize));
    }

    @Operation(summary = "按类型获取字典数据（下拉选项）")
    @GetMapping("/type/{dictType}")
    public R<List<DictDataOptionResp>> getDictDataByType(@PathVariable String dictType) {
        return R.ok(dictDataService.getDictDataByType(dictType));
    }

    @Operation(summary = "新增字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:add')")
    @PostMapping
    public R<Void> add(@Valid @RequestBody SysDictDataCreateReq req) {
        dictDataService.createDictData(req);
        return R.ok();
    }

    @Operation(summary = "修改字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:edit')")
    @PutMapping
    public R<Void> edit(@Valid @RequestBody SysDictDataUpdateReq req) {
        dictDataService.updateDictData(req);
        return R.ok();
    }

    @Operation(summary = "批量删除字典数据")
    @PreAuthorize("hasAuthority('system:dict:data:remove')")
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return R.ok();
    }
}