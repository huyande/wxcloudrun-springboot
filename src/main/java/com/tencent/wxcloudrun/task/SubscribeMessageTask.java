package com.tencent.wxcloudrun.task;

import com.tencent.wxcloudrun.dao.SubscribeLogMapper;
import com.tencent.wxcloudrun.model.SubscribeLog;
import com.tencent.wxcloudrun.service.SubscribeMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class SubscribeMessageTask {
    private final SubscribeLogMapper subscribeLogMapper;
    private final SubscribeMessageService subscribeMessageService;
    private final Logger logger;
    
    private static final LocalTime START_TIME = LocalTime.of(7, 0);
    private static final LocalTime END_TIME = LocalTime.of(22, 0);

    public SubscribeMessageTask(@Autowired SubscribeLogMapper subscribeLogMapper,
                              @Autowired SubscribeMessageService subscribeMessageService) {
        this.subscribeLogMapper = subscribeLogMapper;
        this.subscribeMessageService = subscribeMessageService;
        this.logger = LoggerFactory.getLogger(SubscribeMessageTask.class);
    }

    /**
     * 每10分钟执行一次定时任务
     * 只在7:00-22:00之间执行
     */
    @Scheduled(cron = "0 */10 7-22 * * ?")
    public void sendSubscribeMessages() {
        String templateId = "qxlklvQafBnD88U_4DnGrOLN7zRktvi2rsHS0VOYKcg";
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        // 检查当前时间是否在允许发送的时间范围内
        if (currentTime.isBefore(START_TIME) || currentTime.isAfter(END_TIME)) {
            logger.info("当前时间 {} 不在发送时间范围内 ({} - {})", currentTime, START_TIME, END_TIME);
            return;
        }

        // 查询10分钟以前的需要发送的订阅记录
        LocalDateTime endTime = now.minusMinutes(10);
        List<SubscribeLog> subscribeLogs = subscribeLogMapper.querySubscriptionsToSend(
                templateId, now, endTime);

        if (subscribeLogs.isEmpty()) {
            logger.info("没有需要发送的订阅消息");
            return;
        }

        logger.info("开始发送订阅消息，共 {} 条", subscribeLogs.size());
        subscribeMessageService.sendMessages(templateId, subscribeLogs);
    }

    /**
     * 每5分钟检查一次时间任务
     * 在6:00-23:00之间执行
     *
     */
    //0 */1 6-23 * * ?
    @Scheduled(cron = "0 */1 * * * ?")
    public void checkTimeTask() {
        logger.info("开始检查时间任务");
        subscribeMessageService.processTimeTask();
    }
}
