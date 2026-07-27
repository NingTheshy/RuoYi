package com.ruoyi.common.core.enums;

/**
 * 菜单显示状态
 */
public enum MenuVisible {

    SHOW("0"),
    HIDE("1");

    private final String code;

    MenuVisible(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
