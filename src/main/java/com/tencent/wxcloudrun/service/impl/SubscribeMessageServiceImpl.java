package com.tencent.wxcloudrun.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.tencent.wxcloudrun.dao.SubscribeLogMapper;
import com.tencent.wxcloudrun.model.SubscribeLog;
import com.tencent.wxcloudrun.model.WxSubscribeMessage;
import com.tencent.wxcloudrun.service.SubscribeMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscribeMessageServiceImpl implements SubscribeMessageService {
    
    private final Logger logger = LoggerFactory.getLogger(SubscribeMessageServiceImpl.class);
    
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SubscribeLogMapper subscribeLogMapper;

    @Override
    public void sendMessage(String templateId, SubscribeLog subscribeLog) {
        try {
            WxSubscribeMessage message = buildMessage(templateId, subscribeLog);
            if (message == null) {
                logger.error("不支持的模板ID: {}", templateId);
                return;
            }

            String url = SEND_URL;
            String jsonBody = JSONUtil.toJsonStr(message);
            String result = HttpUtil.post(url, jsonBody);

            logger.info("发送订阅消息结果 - templateId: {}, openid: {}, result: {}", 
                    templateId, subscribeLog.getOpenid(), result);
                    
            // 发送成功后删除记录
            if (subscribeLog.getId() != null) {
                subscribeLogMapper.delete(subscribeLog.getId());
                logger.info("删除已发送的订阅记录 - id: {}", subscribeLog.getId());
            }
        } catch (Exception e) {
            logger.error("发送订阅消息失败 - templateId: {}, openid: {}", 
                    templateId, subscribeLog.getOpenid(), e);
        }
    }

    @Override
    public void sendMessages(String templateId, List<SubscribeLog> subscribeLogs) {
        for (SubscribeLog subscribeLog : subscribeLogs) {
            sendMessage(templateId, subscribeLog);
        }
    }

    private WxSubscribeMessage buildMessage(String templateId, SubscribeLog subscribeLog) {
        Map<String, WxSubscribeMessage.TemplateData> data = new HashMap<>();
        
        switch (templateId) {
            case "qxlklvQafBnD88U_4DnGrOLN7zRktvi2rsHS0VOYKcg":
                return buildTaskMessage(subscribeLog);
            default:
                return null;
        }
    }

    private WxSubscribeMessage buildTaskMessage(SubscribeLog subscribeLog) {
        Map<String, WxSubscribeMessage.TemplateData> data = new HashMap<>();
        
        // 根据实际模板字段配置
        data.put("thing1", WxSubscribeMessage.TemplateData.builder()
                .value("点击记录宝宝今日表现，培养好习惯")
                .build());
        data.put("time2", WxSubscribeMessage.TemplateData.builder()
                .value(subscribeLog.getSendTime().format(DATE_FORMATTER))
                .build());

        return WxSubscribeMessage.builder()
                .touser(subscribeLog.getOpenid())
                .template_id(subscribeLog.getTemplateId())
                .page("pages/index/index")
                .data(data)
                .build();
    }
}
