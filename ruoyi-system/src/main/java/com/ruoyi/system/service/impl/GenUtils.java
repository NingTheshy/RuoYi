package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.system.domain.dto.resp.GenPreviewResp;
import com.ruoyi.system.domain.entity.GenTable;
import com.ruoyi.system.domain.entity.GenTableColumn;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class GenUtils {

    private static final Logger log = LoggerFactory.getLogger(GenUtils.class);

    private final JdbcTemplate jdbcTemplate;
    private final VelocityEngine velocityEngine;

    public GenUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.velocityEngine = new VelocityEngine();
        this.velocityEngine.setProperty(VelocityEngine.RESOURCE_LOADERS, "classpath");
        this.velocityEngine.setProperty("resource.loader.classpath.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        this.velocityEngine.init();
    }

    public List<GenTableColumn> getTableColumns(String tableName) {
        String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT, DATA_TYPE, IS_NULLABLE, " +
                "COLUMN_KEY, EXTRA FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GenTableColumn column = new GenTableColumn();
            column.setColumnName(rs.getString("COLUMN_NAME"));
            column.setColumnComment(rs.getString("COLUMN_COMMENT"));
            String dataType = rs.getString("DATA_TYPE");
            column.setColumnType(dataType);
            column.setJavaType(mapJavaType(dataType));
            column.setJavaField(camelCase(rs.getString("COLUMN_NAME")));
            column.setIsPk("PRI".equals(rs.getString("COLUMN_KEY")) ? "Y" : "N");
            column.setIsIncrement(rs.getString("EXTRA") != null &&
                    rs.getString("EXTRA").contains("auto_increment") ? "Y" : "N");
            column.setIsRequired("NO".equals(rs.getString("IS_NULLABLE")) ? "Y" : "N");
            column.setIsList("Y");
            column.setIsQuery("N".equals(column.getIsPk()) ? "Y" : "N");
            column.setQueryType("EQ");
            column.setHtmlType("input");
            column.setSort(rowNum);
            return column;
        }, tableName);
    }

    public GenPreviewResp previewCode(GenTable table, List<GenTableColumn> columns) {
        Map<String, String> files = generateAllFiles(table, columns);
        GenPreviewResp resp = new GenPreviewResp();
        resp.setFiles(files);
        return resp;
    }

    public byte[] generateCode(GenTable table, List<GenTableColumn> columns) {
        Map<String, String> files = generateAllFiles(table, columns);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Map.Entry<String, String> entry : files.entrySet()) {
                zos.putNextEntry(new ZipEntry(entry.getKey()));
                zos.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ServiceException("生成代码失败: " + e.getMessage());
        }
    }

    public String generateClassName(String tableName) {
        String name = tableName.replaceFirst("sys_", "");
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("_")) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1));
                }
            }
        }
        return sb.toString();
    }

    private Map<String, String> generateAllFiles(GenTable table, List<GenTableColumn> columns) {
        Map<String, String> files = new LinkedHashMap<>();
        VelocityContext context = buildContext(table, columns);

        String packagePath = table.getPackageName().replace(".", "/");
        String className = table.getClassName();
        String entityName = className;
        String serviceName = className + "Service";
        String serviceImplName = className + "ServiceImpl";
        String mapperName = className + "Mapper";
        String convertName = className + "Convert";
        String controllerName = className + "Controller";
        String dtoPath = packagePath + "/domain/dto";
        String entityPath = packagePath + "/domain/entity";
        String mapperPath = packagePath + "/mapper";
        String servicePath = packagePath + "/service";
        String serviceImplPath = servicePath + "/impl";
        String convertPath = packagePath + "/convert";
        String controllerPath = "com/ruoyi/admin/web/" + table.getModuleName();

        files.put(entityPath + "/" + entityName + ".java",
                renderTemplate("entity.java.vm", context));
        files.put(dtoPath + "/req/" + className + "QueryReq.java",
                renderTemplate("queryReq.java.vm", context));
        files.put(dtoPath + "/req/" + className + "CreateReq.java",
                renderTemplate("createReq.java.vm", context));
        files.put(dtoPath + "/req/" + className + "UpdateReq.java",
                renderTemplate("updateReq.java.vm", context));
        files.put(dtoPath + "/resp/" + className + "Resp.java",
                renderTemplate("resp.java.vm", context));
        files.put(mapperPath + "/" + mapperName + ".java",
                renderTemplate("mapper.java.vm", context));
        files.put("resources/mapper/" + mapperName + ".xml",
                renderTemplate("mapper.xml.vm", context));
        files.put(servicePath + "/" + serviceName + ".java",
                renderTemplate("service.java.vm", context));
        files.put(serviceImplPath + "/" + serviceImplName + ".java",
                renderTemplate("serviceImpl.java.vm", context));
        files.put(convertPath + "/" + convertName + ".java",
                renderTemplate("convert.java.vm", context));
        files.put(controllerPath + "/" + controllerName + ".java",
                renderTemplate("controller.java.vm", context));

        return files;
    }

    private VelocityContext buildContext(GenTable table, List<GenTableColumn> columns) {
        VelocityContext context = new VelocityContext();
        context.put("table", table);
        context.put("columns", columns);
        context.put("className", table.getClassName());
        context.put("tableName", table.getTableName());
        context.put("tableComment", table.getTableComment());
        context.put("packageName", table.getPackageName());
        context.put("moduleName", table.getModuleName());
        context.put("businessName", table.getBusinessName());
        context.put("functionName", table.getFunctionName());
        context.put("functionAuthor", table.getFunctionAuthor());
        context.put("classNameLower", camelCase(table.getTableName()));
        context.put("columnsWithoutPk", columns.stream()
                .filter(c -> !"Y".equals(c.getIsPk()))
                .collect(Collectors.toList()));
        context.put("listColumns", columns.stream()
                .filter(c -> "Y".equals(c.getIsList()))
                .collect(Collectors.toList()));
        context.put("queryColumns", columns.stream()
                .filter(c -> "Y".equals(c.getIsQuery()))
                .collect(Collectors.toList()));
        context.put("editColumns", columns.stream()
                .filter(c -> "Y".equals(c.getIsEdit()))
                .collect(Collectors.toList()));
        context.put("insertColumns", columns.stream()
                .filter(c -> "Y".equals(c.getIsInsert()))
                .collect(Collectors.toList()));
        return context;
    }

    private String renderTemplate(String templateName, VelocityContext context) {
        try {
            StringWriter writer = new StringWriter();
            velocityEngine.getTemplate("template/" + templateName, "UTF-8").merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("[代码生成] 渲染模板失败: template={}", templateName, e);
            return "// Template not found: " + templateName;
        }
    }

    private String mapJavaType(String dataType) {
        if (dataType == null) {
            return "String";
        }
        switch (dataType.toLowerCase()) {
            case "bigint":
                return "Long";
            case "int":
            case "integer":
            case "tinyint":
            case "smallint":
                return "Integer";
            case "decimal":
            case "double":
                return "java.math.BigDecimal";
            case "float":
                return "Float";
            case "bit":
                return "Boolean";
            case "date":
            case "datetime":
            case "timestamp":
                return "java.time.LocalDateTime";
            case "time":
                return "java.time.LocalTime";
            case "year":
                return "Integer";
            default:
                return "String";
        }
    }

    private String camelCase(String underscore) {
        if (underscore == null || underscore.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < underscore.length(); i++) {
            char c = underscore.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else {
                if (nextUpper && sb.length() > 0) {
                    sb.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    sb.append(sb.length() == 0 ? Character.toLowerCase(c) : c);
                }
            }
        }
        return sb.toString();
    }
}
