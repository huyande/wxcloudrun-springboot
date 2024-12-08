package com.tencent.wxcloudrun.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.tencent.wxcloudrun.dao.SubscribeLogMapper;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.SubscribeMessageService;
import com.tencent.wxcloudrun.service.WishLogService;
import com.tencent.wxcloudrun.service.WishService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    private WishLogService wishLogService;

    @Autowired
    private WxuserService userService;

    @Autowired
    private WishService wishService;

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

    /**
     * 处理时间任务的消息发送
     */
    public void processTimeTask() {
        String templateId = "JoAR-PQ4H7Aws0FAI9cCRjA-IhCXHIdtoKqpY-h14E0";
        
        try {
            // 查询所有分钟类型的未完成任务（剩余1分钟之内的任务）
            List<WishLog> timeTasks = wishLogService.queryTimeTask();
            
            if (!timeTasks.isEmpty()) {
                logger.info("开始处理时间任务，共 {} 条", timeTasks.size());
                for (WishLog task : timeTasks) {
                    processTimeTaskMessage(task, templateId);
                }
            }else{
                logger.info("没有需要处理的倒计时任务");
            }
        } catch (Exception e) {
            logger.error("处理时间任务失败", e);
        }
    }

    /**
     * 处理单个时间任务的消息发送
     */
    private void processTimeTaskMessage(WishLog task, String templateId) {
        // 获取用户openid
        WxUser user = userService.getUserById(task.getUid());
        if (user != null && user.getOpenid() != null) {
            try {
                // 查询用户最新的订阅记录
                SubscribeLog subscribeLog = subscribeLogMapper.getLatestSubscription(user.getOpenid(), templateId);
                if (subscribeLog == null) {
                    logger.info("用户未订阅消息模板 - openid: {}, templateId: {}", user.getOpenid(), templateId);
                    return;
                }

                // 构建消息
                WxSubscribeMessage message = buildMessage(templateId, subscribeLog,task);
                if(message == null){
                    return;
                }
                // 发送消息
                String jsonBody = JSONUtil.toJsonStr(message);
                String result = HttpUtil.post(SEND_URL, jsonBody);
                
                logger.info("发送时间任务提醒消息结果 - templateId: {}, openid: {}, result: {}", 
                        templateId, user.getOpenid(), result);

                // 发送成功后删除记录
                if (subscribeLog.getId() != null) {
                    subscribeLogMapper.delete(subscribeLog.getId());
                    logger.info("删除已发送的订阅记录 - id: {}", subscribeLog.getId());
                }
                // 更新任务状态
//                task.setStatus(1); // 设置为已完成
//                wishLogService.update(task);
            } catch (Exception e) {
                logger.error("发送时间任务提醒消息失败 - templateId: {}, openid: {}", 
                        templateId, user.getOpenid(), e);
            }
        }
    }

    private WxSubscribeMessage buildMessage(String templateId, SubscribeLog subscribeLog) {
        switch (templateId) {
            case "qxlklvQafBnD88U_4DnGrOLN7zRktvi2rsHS0VOYKcg":
                return buildMeiRiTiXingTaskMessage(subscribeLog);
            default:
                return null;
        }
    }

    private WxSubscribeMessage buildMessage(String templateId, SubscribeLog subscribeLog,WishLog wishLog) {

        switch (templateId) {
            case "JoAR-PQ4H7Aws0FAI9cCRjA-IhCXHIdtoKqpY-h14E0":
                return buildDaoJiShiTaskMessage(subscribeLog,wishLog);
            default:
                return null;
        }
    }

    /**
     * 每日提醒
     * @param subscribeLog
     * @return
     */
    private WxSubscribeMessage buildMeiRiTiXingTaskMessage(SubscribeLog subscribeLog) {
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

    /**
     * 倒计时
     * @param subscribeLog
     * @return
     */
    private WxSubscribeMessage buildDaoJiShiTaskMessage(SubscribeLog subscribeLog,WishLog wishLog) {
        Map<String, WxSubscribeMessage.TemplateData> data = new HashMap<>();

        Wish wish = wishService.getWishById(wishLog.getWid());
        if(wish == null){
            return null;
        }
        // 根据实际模板字段配置
        data.put("thing1", WxSubscribeMessage.TemplateData.builder()
                .value("倒计时"+wishLog.getAmount() * wish.getUnit() + wish.getUnitType())
                .build());
        data.put("time2", WxSubscribeMessage.TemplateData.builder()
                .value(wishLog.getUpdatedAt().format(DATE_FORMATTER))
                .build());
        data.put("phrase3", WxSubscribeMessage.TemplateData.builder()
                .value("已到时")
                .build());
        data.put("thing5", WxSubscribeMessage.TemplateData.builder()
                .value(wishLog.getAmount() * wish.getUnit() + wish.getUnitType())
                .build());
        data.put("thing4", WxSubscribeMessage.TemplateData.builder()
                .value(wish.getTitle())
                .build());

        return WxSubscribeMessage.builder()
                .touser(subscribeLog.getOpenid())
                .template_id(subscribeLog.getTemplateId())
                .page("/count_down_time/count_down_time?wishLogId="+wishLog.getId())
                .data(data)
                .build();
    }

}
