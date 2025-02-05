package com.tencent.wxcloudrun.config;

import java.lang.annotation.*;

/**
 * 目标数据源注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    DataSourceKey value() default DataSourceKey.PRIMARY;
} 