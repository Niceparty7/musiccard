package com.beat.mall.common.utils;

public enum FileType {
    IMAGE(1, "image"),
    VIDEO(2, "video"),
    FILE(3, "file");

    private final Integer type;
    private final String name;

    FileType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
