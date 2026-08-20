package com.beat.mall.user.module.annotation;

import java.lang.annotation.*;

/**
 * 只读标记注解：标注在只执行查询的方法上，AOP 会将其路由到从库
 * <p>
 * 用法：
 * @ReadOnly public List<User> list() { ... }
 * <p>
 * 约定：不标注此注解的方法默认走主库（MASTER）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReadOnly {
}
