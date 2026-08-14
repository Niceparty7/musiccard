package com.beat.mall.module.aspect;

import com.beat.mall.module.datasource.DataSourceContextHolder;
import com.beat.mall.module.datasource.DataSourceKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 读写分离 AOP 切面
 * 规则：
 * 方法标注 @ReadOnly        -> 路由到从库 SLAVE
 * 方法未标注 @ReadOnly      -> 路由到主库 MASTER（默认）
 * 关键点：
 * 1. @Order 要低于事务切面优先级（数值越小优先级越高），
 * 确保先切库、再开事务，事务才能拿到正确数据源的连接；
 * 2. finally 中必须 clear()，防止线程池复用导致串库。
 */
@Aspect
@Component
@Order(0)
public class DataSourceAspect {

    @Pointcut("@annotation(com.beat.mall.module.annotation.ReadOnly)")
    public void readOnlyPointcut() {
    }

    @Around("readOnlyPointcut()")
    public Object aroundReadOnly(ProceedingJoinPoint joinPoint) throws Throwable {
        DataSourceContextHolder.set(DataSourceKey.SLAVE);
        try {
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}