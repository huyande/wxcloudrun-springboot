package com.tencent.wxcloudrun.service;

import java.util.List;
import com.tencent.wxcloudrun.model.SubscribeLog;

public interface SubscribeMessageService {
    /**
     * 发送订阅消息
     * @param templateId 模板ID
     * @param subscribeLog 订阅记录
     */
    void sendMessage(String templateId, SubscribeLog subscribeLog);
    
    /**
     * 批量发送订阅消息
     * @param templateId 模板ID
     * @param subscribeLogs 订阅记录列表
     */
    void sendMessages(String templateId, List<SubscribeLog> subscribeLogs);
}
