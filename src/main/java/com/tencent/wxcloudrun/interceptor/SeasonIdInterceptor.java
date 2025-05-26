package com.tencent.wxcloudrun.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 赛季ID拦截器
 * 从请求头获取seasonId并设置到请求属性中
 */
@Component
public class SeasonIdInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SeasonIdInterceptor.class);
    private static final Pattern SEASON_ID_PATTERN = Pattern.compile("/api/season/(\\w+)/");

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String seasonId = request.getHeader("X-Season-Id");
        String requestURI = request.getRequestURI();
        
        if (seasonId != null && !seasonId.isEmpty()) {
            logger.info("请求头中的赛季ID: {}, 路径: {}", seasonId, requestURI);
            
            // 获取现有的uri模板变量
            Map<String, String> uriVariables = (Map<String, String>) request.getAttribute(
                    "org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
            
            // 如果为null则创建新的
            if (uriVariables == null) {
                uriVariables = new HashMap<>();
            } else {
                // 创建一个可修改的副本
                uriVariables = new HashMap<>(uriVariables);
            }
            
            // 设置seasonId
            uriVariables.put("seasonId", seasonId);
            
            // 更新请求属性
            request.setAttribute(
                    "org.springframework.web.servlet.HandlerMapping.uriTemplateVariables", 
                    uriVariables);
            
            logger.info("已将请求头中的seasonId设置到路径变量中: {}", seasonId);
        }
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 后处理逻辑
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后的处理逻辑
    }
} 