package com.ruoyi.common.core.enums;

/**
 * 逻辑删除标志
 */
public enum DeleteFlag {

    NORMAL("0"),
    DELETED("2");

    private final String code;

    DeleteFlag(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
