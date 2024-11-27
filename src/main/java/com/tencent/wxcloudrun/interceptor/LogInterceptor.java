package com.tencent.wxcloudrun.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        
        // 记录基本请求信息
        logger.info("=== 请求开始 ===");
        logger.info("请求路径: [{}], 方法: [{}]", requestURI, method);
        
        // 记录请求头信息
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                if (headerName.toLowerCase().contains("X-WX-OPENID")) {
                    logger.info("Header - {}: {}", headerName, request.getHeader(headerName));
                }
            }
        }

        // 记录查询参数
        if (queryString != null) {
            logger.info("Query参数: {}", queryString);
        }

        // 记录请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.isEmpty()) {
            Map<String, Object> params = new HashMap<>();
            parameterMap.forEach((key, value) -> {
                params.put(key, value.length == 1 ? value[0] : Arrays.asList(value));
            });
            logger.info("请求参数: {}", objectMapper.writeValueAsString(params));
        }

        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        logger.info("响应状态: {}", response.getStatus());
        logger.info("处理时间: {}ms", endTime - startTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            logger.error("请求处理异常: ", ex);
        }
        logger.info("=== 请求结束 ===\n");
    }
}
