package com.beat.mall.module.datasource;

/**
 * 数据源上下文：用 ThreadLocal 保存当前线程应使用的数据源 key
 *
 * 1. 每个请求是独立线程,路由信息互不干扰；
 * 2. 方法调用链内共享（Service -> Mapper 都能读到）；
 * 3. 必须在使用完毕后 remove(),否则 Tomcat 线程池复用线程时,
 *    会把上一个请求的路由状态带到下一个请求（串库 Bug）。
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceKey> CONTEXT = new ThreadLocal<>();

    /** 默认路由：主库 */
    private static final DataSourceKey DEFAULT_KEY = DataSourceKey.MASTER;

    public static void set(DataSourceKey key) {
        CONTEXT.set(key);
    }

    /** 获取当前数据源 key，未设置时默认主库（保证写操作落主库） */
    public static DataSourceKey get() {
        DataSourceKey key = CONTEXT.get();
        return key == null ? DEFAULT_KEY : key;
    }

    /** 清理线程上下文，必须在 finally 中调用 */
    public static void clear() {
        CONTEXT.remove();
    }
}
