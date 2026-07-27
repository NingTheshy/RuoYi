package com.ruoyi.common.core.enums;

/**
 * 通用启停状态
 */
public enum StatusFlag {

    NORMAL("0"),
    DISABLED("1");

    private final String code;

    StatusFlag(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean matches(String value) {
        return code.equals(value);
    }
}
