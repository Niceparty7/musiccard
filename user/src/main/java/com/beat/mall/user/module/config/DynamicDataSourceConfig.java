package com.beat.mall.user.module.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.beat.mall.user.module.datasource.DataSourceKey;
import com.beat.mall.user.module.datasource.RoutingDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 动态数据源配置：手动创建主/从两个 Druid 连接池 + 路由数据源
 * <p>
 * 路由数据源是唯一 @Primary 的 DataSource，MyBatis、事务管理器、
 * JdbcTemplate 等都依赖它；实际连接由 RoutingDataSource 依据
 * ThreadLocal 中的 key(DataSourceContextHolder) 分发到主库或从库。
 * <p>
 * 密码加密说明：
 * master.connection-properties 配置 config.decrypt=true;config.decrypt.key=公钥
 * 密码使用密文；slave 若密码为明文,则无需配置 connection-properties,
 * filters 中的 config 过滤器不会触发解密,明文密码直接使用。
 */
@Configuration
public class DynamicDataSourceConfig {
    // ===== 主库 =====
    @Value("${app.datasource.master.url}")
    private String masterUrl;
    @Value("${app.datasource.master.username}")
    private String masterUsername;
    @Value("${app.datasource.master.password}")
    private String masterPassword;
    @Value("${app.datasource.master.connection-properties:}")
    private String masterConnectionProperties;
    // ===== 从库 =====
    @Value("${app.datasource.slave.url}")
    private String slaveUrl;
    @Value("${app.datasource.slave.username}")
    private String slaveUsername;
    @Value("${app.datasource.slave.password}")
    private String slavePassword;
    @Value("${app.datasource.slave.connection-properties:}")
    private String slaveConnectionProperties;
    // ===== Druid 公共参数（沿用 application-druid.properties）=====
    @Value("${spring.datasource.druid.initial-size}")
    private int initialSize;
    @Value("${spring.datasource.druid.min-idle}")
    private int minIdle;
    @Value("${spring.datasource.druid.max-active}")
    private int maxActive;
    @Value("${spring.datasource.druid.max-wait}")
    private long maxWait;
    @Value("${spring.datasource.druid.filters}")
    private String filters;

    /**
     * 主库连接池（写）
     */
    @Bean
    public DataSource masterDataSource() {
        return buildDruidDataSource(masterUrl, masterUsername, masterPassword, masterConnectionProperties);
    }

    /**
     * 从库连接池（读）
     */
    @Bean
    public DataSource slaveDataSource() {
        return buildDruidDataSource(slaveUrl, slaveUsername, slavePassword, slaveConnectionProperties);
    }

    /**
     * 路由数据源：唯一的 @Primary 数据源，业务代码注入的都是它
     * （SqlSessionFactory、DataSourceTransactionManager 等都依赖它）
     */
    @Bean
    @Primary
    public DataSource routingDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceKey.MASTER, masterDataSource());
        targetDataSources.put(DataSourceKey.SLAVE, slaveDataSource());
        // 默认主库：未设置路由时（如写操作）走主库
        return new RoutingDataSource(targetDataSources, masterDataSource());
    }

    /**
     * 事务管理器：使用路由数据源（保证事务内读写同一数据源）
     */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource routingDataSource) {
        return new DataSourceTransactionManager(routingDataSource);
    }

    private DruidDataSource buildDruidDataSource(String url, String username, String password,
                                                 String connectionProperties) {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // 连接池参数
        ds.setInitialSize(initialSize);
        ds.setMinIdle(minIdle);
        ds.setMaxActive(maxActive);
        ds.setMaxWait(maxWait);
        // filter 链：config(密码解密)/stat(监控)/wall(防火墙)/slf4j(日志)
        // 注意：手动创建数据源后 druid starter 不再自动注入 filter，必须代码设置
        try {
            ds.setFilters(filters);
        } catch (Exception e) {
            throw new IllegalStateException("Druid filters 初始化失败: " + filters, e);
        }
        // 密码解密配置（仅配置了 connection-properties 的数据源生效，
        // 例如 config.decrypt=true;config.decrypt.key=${public-key}）
        if (connectionProperties != null && !connectionProperties.isBlank()) {
            ds.setConnectProperties(parseProperties(connectionProperties));
        }
        return ds;
    }

    /**
     * 解析 "k1=v1;k2=v2" 格式的字符串为 Properties
     */
    private Properties parseProperties(String source) {
        Properties props = new Properties();
        for (String pair : source.split(";")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                props.setProperty(pair.substring(0, idx).trim(), pair.substring(idx + 1).trim());
            }
        }
        return props;
    }
}
