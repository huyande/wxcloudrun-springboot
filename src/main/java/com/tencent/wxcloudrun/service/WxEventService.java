package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.SubscribeLog;

import java.util.List;
import java.util.Map;

public interface WxEventService {
    String eventHandel(Map<String, Object> jsonMap);

    /**
     * 检查用户今天是否订阅了指定模板
     * @param openid 用户openid
     * @param templateIds 模板ID列表
     * @return 订阅记录列表
     */
    List<SubscribeLog> checkTodaySubscriptions(String openid, List<String> templateIds);
}
