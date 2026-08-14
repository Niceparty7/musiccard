package com.beat.mall.module.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 * 路由数据源：Spring 提供的动态数据源基类
 * AbstractRoutingDataSource 持有多个真实数据源（targetDataSources），
 * 每次调用 getConnection() 时先执行 determineCurrentLookupKey()，
 * 根据返回值决定返回哪一个真实数据源的连接。
 * 我们只需把 ThreadLocal 中的 key 返回即可实现读写分离。
 */
public class RoutingDataSource extends AbstractRoutingDataSource {
    public RoutingDataSource(Map<Object, Object> targetDataSources, Object defaultTargetDataSource) {
        setTargetDataSources(targetDataSources);
        setDefaultTargetDataSource(defaultTargetDataSource);
        // 初始化默认数据源解析，必须在 set 之后调用
        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.get();
    }
}