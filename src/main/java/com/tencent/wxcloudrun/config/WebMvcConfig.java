package com.tencent.wxcloudrun.config;

import com.tencent.wxcloudrun.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/wishLog/**", "/member/**", "/wish/**", "/wxuser/**")  // 指定要拦截的路径
                .excludePathPatterns("/static/**");  // 排除静态资源
    }
}