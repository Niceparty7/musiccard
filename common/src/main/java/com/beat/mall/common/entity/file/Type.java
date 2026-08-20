package com.beat.mall.common.entity.file;

public enum Type {
    IMAGE(1, "image"),
    VIDEO(2, "video"),
    FILE(3, "file");

    private final Integer type;
    private final String name;

    Type(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public Integer getCode() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static Type fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (Type value : values()) {
            if (value.type.equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown file type code: " + code);
    }
}
