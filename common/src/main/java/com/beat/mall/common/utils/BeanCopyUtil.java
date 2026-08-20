package com.beat.mall.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class BeanCopyUtil {
    private BeanCopyUtil() {
    }

    public static <S, T> T copy(S source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new IllegalStateException("Bean copy failed", e);
        }
    }

    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(item -> copy(item, targetClass))
                .collect(Collectors.toList());
    }
}
