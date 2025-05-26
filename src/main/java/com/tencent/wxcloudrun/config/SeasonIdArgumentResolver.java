package com.tencent.wxcloudrun.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 赛季ID参数解析器
 * 处理从请求头或路径变量获取seasonId的情况
 */
@Component
public class SeasonIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(SeasonIdArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 只处理Long类型且参数名为seasonId的参数
        return parameter.getParameterType().equals(Long.class) 
                && "seasonId".equals(parameter.getParameterName());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        
        // 首先从路径变量中获取
        String pathSeasonId = webRequest.getParameter("seasonId");
        if (pathSeasonId == null) {
            // 尝试从请求属性中获取（由拦截器设置）
            Object uriSeasonId = webRequest.getAttribute("seasonId", NativeWebRequest.SCOPE_REQUEST);
            if (uriSeasonId instanceof String) {
                pathSeasonId = (String) uriSeasonId;
            }
        }
        
        // 如果路径变量中没有有效值，尝试从请求头获取
        if (pathSeasonId == null || "null".equals(pathSeasonId)) {
            String headerSeasonId = webRequest.getHeader("X-Season-Id");
            if (headerSeasonId != null && !headerSeasonId.isEmpty()) {
                try {
                    Long seasonId = Long.valueOf(headerSeasonId);
                    logger.info("从请求头获取到seasonId: {}", seasonId);
                    return seasonId;
                } catch (NumberFormatException e) {
                    logger.warn("请求头中的seasonId格式错误: {}", headerSeasonId);
                }
            }
        } else {
            try {
                if (!"null".equals(pathSeasonId)) {
                    return Long.valueOf(pathSeasonId);
                }
            } catch (NumberFormatException e) {
                logger.warn("路径变量中的seasonId格式错误: {}", pathSeasonId);
            }
        }
        
        // 如果都没有获取到有效值，返回null
        return null;
    }
} 