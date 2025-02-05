package com.tencent.wxcloudrun.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源实现类
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    
    private static final ThreadLocal<DataSourceKey> contextHolder = new ThreadLocal<>();

    public static void setDataSourceKey(DataSourceKey key) {
        contextHolder.set(key);
    }

    public static DataSourceKey getDataSourceKey() {
        return contextHolder.get();
    }

    public static void clearDataSourceKey() {
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
} 