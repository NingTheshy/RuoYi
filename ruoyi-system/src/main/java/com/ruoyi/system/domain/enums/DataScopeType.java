package com.ruoyi.system.domain.enums;

/**
 * 数据权限范围
 */
public enum DataScopeType {

    ALL("1"),
    CUSTOM("2"),
    DEPT("3"),
    DEPT_AND_CHILD("4"),
    SELF("5");

    private final String code;

    DataScopeType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DataScopeType fromCode(String code) {
        for (DataScopeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
