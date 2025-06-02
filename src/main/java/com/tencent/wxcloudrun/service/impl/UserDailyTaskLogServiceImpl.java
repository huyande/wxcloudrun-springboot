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
    public void addTaskLog(String openid, UserDailyTaskLogDto taskLogDto) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot add task log.");
            // 根据业务需求，这里可以抛出异常，例如 IllegalArgumentException
            // throw new IllegalArgumentException("OpenID cannot be empty.");
            return; // 或者直接返回，不记录日志
        }
        if (taskLogDto == null || !StringUtils.hasText(taskLogDto.getType())) {
            logger.warn("Task log DTO is null or type is missing for openid: {}", openid);
            // throw new IllegalArgumentException("Task type cannot be empty.");
            return;
        }

        UserDailyTaskLog log = new UserDailyTaskLog();
        log.setOpenid(openid);
        log.setType(taskLogDto.getType());
        log.setPoints(taskLogDto.getPoints());
        
        try {
            userDailyTaskLogMapper.insert(log);
            logger.info("Successfully added task log for openid: {}, type: {}", openid, taskLogDto.getType());
        } catch (Exception e) {
            logger.error("Error adding task log for openid: " + openid + ", type: " + taskLogDto.getType(), e);
            // 根据需要，可以向上层抛出自定义异常
            // throw new RuntimeException("Failed to add task log.", e);
        }
    }

    @Override
    public List<TaskCountDto> getDailyTaskCounts(String openid) {
        if (!StringUtils.hasText(openid)) {
            logger.warn("OpenID is missing, cannot get daily task counts.");
            // 根据业务需求，这里可以抛出异常或返回空列表
            // throw new IllegalArgumentException("OpenID cannot be empty.");
            return Collections.emptyList();
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
            // 根据需要，可以向上层抛出自定义异常
            // throw new RuntimeException("Failed to get daily task counts.", e);
            return Collections.emptyList(); // 发生异常时返回空列表，或根据策略处理
        }
    }

    @Override
    public Integer getPoints(String openid) {
        return userDailyTaskLogMapper.getPoints(openid);
    }
}
