package com.hfut.ai.enums;

/**
 * 功能类型
 * @author GM
 */
public enum ChatType {
    CHAT("chat"),
    SERVICE("service"),
    PDF("pdf");

    private final String value;

    ChatType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
