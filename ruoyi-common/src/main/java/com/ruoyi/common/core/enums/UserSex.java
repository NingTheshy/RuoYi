package com.ruoyi.common.core.enums;

/**
 * 用户性别
 */
public enum UserSex {

    MALE("0"),
    FEMALE("1"),
    UNKNOWN("2");

    private final String code;

    UserSex(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
