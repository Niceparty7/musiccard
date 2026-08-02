package com.beat.mall.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtil {

    /**
     * 复制单个 Bean.
     *
     * @param source
     * @param targetClass
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> T copy(S source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean copy failed", e);
        }
    }

    /**
     * 复制多个 Bean.
     *
     * @param sourceList
     * @param targetClass
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(item -> copy(item, targetClass))
                .collect(Collectors.toList());
    }
}
