package top.yuhanpeng.musiccard.module.entity;

import com.fasterxml.jackson.annotation.JsonValue;


public enum Type {
    IMAGE(1),
    VIDEO(2),
    FILE(3);
    @JsonValue
    private final Integer code;

    Type(Integer code) {
        this.code = code;
    }

    public static Type fromCode(Integer code) {
        for (Type type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("the file type is unknown" + code);
    }

    public Integer getCode() {
        return code;
    }
}