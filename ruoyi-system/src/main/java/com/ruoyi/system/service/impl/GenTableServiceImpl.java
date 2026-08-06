package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.PageResult;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.convert.GenTableConvert;
import com.ruoyi.system.domain.dto.req.GenTableCreateReq;
import com.ruoyi.system.domain.dto.req.GenTableQueryReq;
import com.ruoyi.system.domain.dto.req.GenTableUpdateReq;
import com.ruoyi.system.domain.dto.req.GenSyncReq;
import com.ruoyi.system.domain.dto.resp.GenPreviewResp;
import com.ruoyi.system.domain.dto.resp.GenTableResp;
import com.ruoyi.system.domain.entity.GenTable;
import com.ruoyi.system.domain.entity.GenTableColumn;
import com.ruoyi.system.mapper.GenTableColumnMapper;
import com.ruoyi.system.mapper.GenTableMapper;
import com.ruoyi.system.service.GenTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements GenTableService {

    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    private final GenTableConvert genTableConvert;
    private final GenTableColumnMapper columnMapper;
    private final GenUtils genUtils;

    public GenTableServiceImpl(GenTableConvert genTableConvert, GenTableColumnMapper columnMapper, GenUtils genUtils) {
        this.genTableConvert = genTableConvert;
        this.columnMapper = columnMapper;
        this.genUtils = genUtils;
    }

    @Override
    public PageResult<GenTableResp> getTablePage(GenTableQueryReq queryReq, Integer pageNum, Integer pageSize) {
        GenTable query = genTableConvert.toEntity(queryReq);
        Page<GenTable> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GenTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTableName()), GenTable::getTableName, query.getTableName())
                .like(StringUtils.hasText(query.getTableComment()), GenTable::getTableComment, query.getTableComment())
                .orderByAsc(GenTable::getTableId);
        Page<GenTable> result = baseMapper.selectPage(page, wrapper);
        List<GenTableResp> respList = genTableConvert.toRespList(result.getRecords());
        for (GenTableResp resp : respList) {
            List<GenTableColumn> columns = columnMapper.selectList(
                    new LambdaQueryWrapper<GenTableColumn>()
                            .eq(GenTableColumn::getTableId, resp.getTableId())
                            .orderByAsc(GenTableColumn::getSort)
            );
            resp.setColumns(genTableConvert.toColumnRespList(columns));
        }
        return new PageResult<>(respList, result.getTotal());
    }

    @Override
    public GenTableResp getTableById(Long tableId) {
        GenTable table = getById(tableId);
        if (table == null) {
            throw new ServiceException(404, "生成表配置不存在");
        }
        GenTableResp resp = genTableConvert.toResp(table);
        List<GenTableColumn> columns = columnMapper.selectList(
                new LambdaQueryWrapper<GenTableColumn>()
                        .eq(GenTableColumn::getTableId, tableId)
                        .orderByAsc(GenTableColumn::getSort)
        );
        resp.setColumns(genTableConvert.toColumnRespList(columns));
        return resp;
    }

    @Override
    @Transactional
    public int createTable(GenTableCreateReq req) {
        GenTable table = genTableConvert.toEntity(req);
        if (!StringUtils.hasText(table.getClassName())) {
            table.setClassName(genUtils.generateClassName(table.getTableName()));
        }
        if (!StringUtils.hasText(table.getPackageName())) {
            table.setPackageName("com.ruoyi.system");
        }
        if (!StringUtils.hasText(table.getModuleName())) {
            table.setModuleName("system");
        }
        if (!StringUtils.hasText(table.getBusinessName())) {
            table.setBusinessName(table.getTableName().replaceFirst("sys_", ""));
        }
        boolean result = save(table);
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int updateTable(GenTableUpdateReq req) {
        GenTable existing = getById(req.getTableId());
        if (existing == null) {
            throw new ServiceException(404, "生成表配置不存在");
        }
        GenTable table = genTableConvert.toEntity(req);
        boolean result = updateById(table);
        return result ? 1 : 0;
    }

    @Override
    @Transactional
    public int deleteTableByIds(Long[] tableIds) {
        for (Long tableId : tableIds) {
            columnMapper.delete(
                    new LambdaQueryWrapper<GenTableColumn>()
                            .eq(GenTableColumn::getTableId, tableId)
            );
        }
        return removeByIds(Arrays.asList(tableIds)) ? tableIds.length : 0;
    }

    @Override
    @Transactional
    public void syncTables(GenSyncReq req) {
        for (String tableName : req.getTableNames()) {
            syncOneTable(tableName);
        }
    }

    private void syncOneTable(String tableName) {
        GenTable existing = getOne(
                new LambdaQueryWrapper<GenTable>()
                        .eq(GenTable::getTableName, tableName)
        );
        if (existing != null) {
            return;
        }
        GenTable table = new GenTable();
        table.setTableName(tableName);
        table.setTableComment(tableName);
        table.setClassName(genUtils.generateClassName(tableName));
        table.setPackageName("com.ruoyi.system");
        table.setModuleName("system");
        table.setBusinessName(tableName.replaceFirst("sys_", ""));
        table.setFunctionName(tableName);
        save(table);
        List<GenTableColumn> columns = genUtils.getTableColumns(tableName);
        for (GenTableColumn column : columns) {
            column.setTableId(table.getTableId());
            columnMapper.insert(column);
        }
        log.info("[代码生成] 同步表结构: tableName={}, columnCount={}", tableName, columns.size());
    }

    @Override
    public GenPreviewResp previewCode(Long tableId) {
        GenTable table = getById(tableId);
        if (table == null) {
            throw new ServiceException(404, "生成表配置不存在");
        }
        List<GenTableColumn> columns = columnMapper.selectList(
                new LambdaQueryWrapper<GenTableColumn>()
                        .eq(GenTableColumn::getTableId, tableId)
                        .orderByAsc(GenTableColumn::getSort)
        );
        return genUtils.previewCode(table, columns);
    }

    @Override
    public byte[] generateCode(Long tableId) {
        GenTable table = getById(tableId);
        if (table == null) {
            throw new ServiceException(404, "生成表配置不存在");
        }
        List<GenTableColumn> columns = columnMapper.selectList(
                new LambdaQueryWrapper<GenTableColumn>()
                        .eq(GenTableColumn::getTableId, tableId)
                        .orderByAsc(GenTableColumn::getSort)
        );
        return genUtils.generateCode(table, columns);
    }
}
