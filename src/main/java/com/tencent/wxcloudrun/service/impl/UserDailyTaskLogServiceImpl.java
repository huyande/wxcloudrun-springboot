package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.UserDailyTaskLogMapper;
import com.tencent.wxcloudrun.dto.TaskCountDto;
import com.tencent.wxcloudrun.dto.UserDailyTaskLogDto;
import com.tencent.wxcloudrun.model.UserDailyTaskLog;
import com.tencent.wxcloudrun.service.UserDailyTaskLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserDailyTaskLogServiceImpl implements UserDailyTaskLogService {

    private static final Logger logger = LoggerFactory.getLogger(UserDailyTaskLogServiceImpl.class);

    private final UserDailyTaskLogMapper userDailyTaskLogMapper;

    @Autowired
    public UserDailyTaskLogServiceImpl(UserDailyTaskLogMapper userDailyTaskLogMapper) {
        this.userDailyTaskLogMapper = userDailyTaskLogMapper;
    }

    @Override
    public void addTaskLog(String openid, UserDailyTaskLogDto taskLogDto,String familyCode) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot add task log.");
            throw new IllegalArgumentException("OpenID cannot be empty.");
        }
        if (taskLogDto == null || !StringUtils.hasText(taskLogDto.getType())) {
            logger.warn("Task log DTO is null or type is missing for openid: {}", openid);
            throw new IllegalArgumentException("Task type cannot be empty.");
        }

        UserDailyTaskLog log = new UserDailyTaskLog();
        log.setOpenid(openid);
        log.setType(taskLogDto.getType());
        log.setPoints(taskLogDto.getPoints());
        
        // 设置状态，如果未指定则默认为completed
        String status = StringUtils.hasText(taskLogDto.getStatus()) ? taskLogDto.getStatus() : "completed";
        log.setStatus(status);
        
        // 设置审核内容
//        if (StringUtils.hasText(taskLogDto.getReviewContent())) {
//            log.setReviewContent(taskLogDto.getReviewContent());
//        }
        if(status=="pending"){
            log.setReviewContent(familyCode);
        }
        
        try {
            userDailyTaskLogMapper.insert(log);
            logger.info("Successfully added task log for openid: {}, type: {}, status: {}", 
                       openid, taskLogDto.getType(), status);
        } catch (Exception e) {
            logger.error("Error adding task log for openid: " + openid + ", type: " + taskLogDto.getType(), e);
            throw new RuntimeException("Failed to add task log.", e);
        }
    }

    @Override
    public List<TaskCountDto> getDailyTaskCounts(String openid) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot get daily task counts.");
            throw new IllegalArgumentException("OpenID cannot be empty.");
        }

        LocalDate today = LocalDate.now(); // 获取当前日期
        try {
            List<Map<String, Object>> results = userDailyTaskLogMapper.countTasksByType(openid);
            if (results == null) {
                return Collections.emptyList();
            }
            return results.stream()
                    .map(result -> {
                        String type = (String) result.get("type");
                        // COUNT(*) 在不同数据库驱动下可能返回 Long 或 BigInteger
                        Object countObj = result.get("count");
                        Long count = 0L;
                        if (countObj instanceof Number) {
                            count = ((Number) countObj).longValue();
                        } else if (countObj != null) {
                            try {
                                count = Long.parseLong(countObj.toString());
                            } catch (NumberFormatException e) {
                                logger.warn("Could not parse count for type {} and openid {}: {}", type, openid, countObj);
                            }
                        }
                        return new TaskCountDto(type, count);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting daily task counts for openid: " + openid, e);
            throw new RuntimeException("Failed to get daily task counts.", e);
        }
    }

    @Override
    public Integer getPoints(String openid) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot get points.");
            return 0;
        }
        
        try {
            Integer points = userDailyTaskLogMapper.getPoints(openid);
            return points != null ? points : 0;
        } catch (Exception e) {
            logger.error("Error getting points for openid: " + openid, e);
            return 0;
        }
    }

    @Override
    public List<UserDailyTaskLog> getTodayTaskLogs(String openid) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot get today task logs.");
            return Collections.emptyList();
        }
        
        try {
            return userDailyTaskLogMapper.findTodayLogsByOpenid(openid);
        } catch (Exception e) {
            logger.error("Error getting today task logs for openid: " + openid, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<UserDailyTaskLog> getLimitedLogs(String openid) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot get today task logs.");
            return Collections.emptyList();
        }

        try {
            return userDailyTaskLogMapper.findLimitedLogsByOpenid(openid);
        } catch (Exception e) {
            logger.error("Error getting today task logs for openid: " + openid, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void clearTaskLogs() {
        //分组查询满足要清楚数据的openid
        List<UserDailyTaskLog> list = userDailyTaskLogMapper.findClearLog();
        for(UserDailyTaskLog log : list){
            Integer sum = userDailyTaskLogMapper.getClearLogSum(log.getOpenid());
            if(sum>=0){
                userDailyTaskLogMapper.delClearLogs(log.getOpenid());
            }else if(sum<0){
                userDailyTaskLogMapper.delClearLogs(log.getOpenid());
                UserDailyTaskLog log_ = new UserDailyTaskLog();
                log_.setOpenid(log.getOpenid());
                log_.setStatus("completed");
                log_.setType("ad");
                log_.setRemark("数据清除接转");
                log_.setPoints(Math.abs(sum));
                userDailyTaskLogMapper.insert(log);
            }
        }
    }

    @Override
    public List<UserDailyTaskLog> getPendingReviewLogs(String reviewContent) {
        try {
            return userDailyTaskLogMapper.findPendingReviewLogs(reviewContent);
        } catch (Exception e) {
            logger.error("Error getting pending review logs for reviewContent: " + reviewContent, e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateLogStatus(Long id, String status) {
        if (id == null) {
            logger.warn("Log ID cannot be null for status update.");
            return false;
        }
        if (!StringUtils.hasText(status)) {
            logger.warn("Status cannot be empty for status update.");
            return false;
        }

        try {
            int result = userDailyTaskLogMapper.updateStatus(id, status);
            return result > 0;
        } catch (Exception e) {
            logger.error("Error updating log status for id: " + id, e);
            return false;
        }
    }
}
