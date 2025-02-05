package com.tencent.wxcloudrun.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 动态数据源切换的AOP切面
 */
@Aspect
@Component
@Order(-1) // 保证该AOP在@Transactional之前执行
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(com.tencent.wxcloudrun.config.TargetDataSource) || @within(com.tencent.wxcloudrun.config.TargetDataSource)")
    public void dataSourcePointCut() {
    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        TargetDataSource ds = method.getAnnotation(TargetDataSource.class);
        if (ds == null) {
            ds = point.getTarget().getClass().getAnnotation(TargetDataSource.class);
        }
        
        if (ds != null) {
            DynamicDataSource.setDataSourceKey(ds.value());
        }
        
        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSourceKey();
        }
    }
} 