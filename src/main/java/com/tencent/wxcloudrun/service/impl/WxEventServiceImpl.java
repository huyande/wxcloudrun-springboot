package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SubscribeLogMapper;
import com.tencent.wxcloudrun.model.SubscribeLog;
import com.tencent.wxcloudrun.service.WxEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class WxEventServiceImpl implements WxEventService {
    
    @Autowired
    private SubscribeLogMapper subscribeLogMapper;

    @Override
    public String eventHandel(Map<String, Object> jsonMap) {
        String event = (String) jsonMap.get("Event");
        if(event.equals("subscribe_msg_popup_event")) {
            String fromUserName = (String) jsonMap.get("FromUserName");
            List<Map<String, Object>> list = (List<Map<String, Object>>)jsonMap.get("List");
            
            if (list != null && !list.isEmpty()) {
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                
                for (Map<String, Object> item : list) {
                    String status = (String) item.get("SubscribeStatusString");
                    if ("accept".equals(status)) {
                        SubscribeLog subscribeLog = new SubscribeLog();
                        subscribeLog.setOpenid(fromUserName);
                        subscribeLog.setTemplateId((String) item.get("TemplateId"));
                        subscribeLog.setCreateTime(currentTime);
                        
                        subscribeLogMapper.insertOne(subscribeLog);
                    }
                }
            }
        }
        return "success";
    }
}
