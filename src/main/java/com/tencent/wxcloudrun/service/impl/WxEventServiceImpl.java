package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SubscribeLogMapper;
import com.tencent.wxcloudrun.model.SubscribeLog;
import com.tencent.wxcloudrun.service.WxEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class WxEventServiceImpl implements WxEventService {
    
    private static final LocalTime EARLIEST_SEND_TIME = LocalTime.of(10, 0);
    private static final LocalTime LATEST_SEND_TIME = LocalTime.of(22, 0);
    
    private static final Set<String> TASK_TEMPLATE_IDS = new HashSet<>(Arrays.asList(
            "qxlklvQafBnD88U_4DnGrOLN7zRktvi2rsHS0VOYKcg"
    ));
    
    @Autowired
    private SubscribeLogMapper subscribeLogMapper;

    @Override
    public String eventHandel(Map<String, Object> jsonMap) {
        String event = (String) jsonMap.get("Event");
        if(event.equals("subscribe_msg_popup_event")) {
            String fromUserName = (String) jsonMap.get("FromUserName");
            Object listObj = jsonMap.get("List");
            
            LocalDateTime now = LocalDateTime.now();
            
            // 处理单个订阅对象的情况
            if (listObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) listObj;
                processSubscribeItem(item, fromUserName, now);
            } 
            // 处理多个订阅对象的情况
            else if (listObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
                for (Map<String, Object> item : list) {
                    processSubscribeItem(item, fromUserName, now);
                }
            }
        }
        return "success";
    }

    /**
     * 处理单个订阅项
     */
    private void processSubscribeItem(Map<String, Object> item, String fromUserName, LocalDateTime now) {
        String status = (String) item.get("SubscribeStatusString");
        if ("accept".equals(status)) {
            String templateId = (String) item.get("TemplateId");
            SubscribeLog subscribeLog = new SubscribeLog();
            subscribeLog.setOpenid(fromUserName);
            subscribeLog.setTemplateId(templateId);
            
            // 只有特定模板ID才计算发送时间
            if (TASK_TEMPLATE_IDS.contains(templateId)) {
                LocalDateTime sendTime = calculateNextSendTime(now);
                subscribeLog.setSendTime(sendTime);
            }

            subscribeLogMapper.insertOne(subscribeLog);
        }
    }

    /**
     * 计算下次发送时间
     * 规则：
     * 1. 如果当前时间在 7:00-22:00 之间，设置为明天同一时间
     * 2. 如果当前时间在 7:00 之前，设置为明天 7:00
     * 3. 如果当前时间在 22:00 之后，设置为明天 7:00    
     */
    private LocalDateTime calculateNextSendTime(LocalDateTime currentTime) {
        LocalTime currentTimeOfDay = currentTime.toLocalTime();
        
        if (currentTimeOfDay.isBefore(EARLIEST_SEND_TIME)) {
            // 当前时间在7:00之前，设置为明天7:00
            return currentTime.plusDays(1).with(EARLIEST_SEND_TIME);
        } else if (currentTimeOfDay.isAfter(LATEST_SEND_TIME)) {
            // 当前时间在22:00之后，设置为明天7:00
            return currentTime.plusDays(1).with(EARLIEST_SEND_TIME);
        } else {
            // 当前时间在7:00-22:00之间，设置为明天同一时间
            return currentTime.plusDays(1);
        }
    }

    @Override
    public List<SubscribeLog> checkTodaySubscriptions(String openid, List<String> templateIds) {
        return subscribeLogMapper.queryTodaySubscriptions(openid, templateIds);
    }
}
