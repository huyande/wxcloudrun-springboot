package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.TaskCountDto;
import com.tencent.wxcloudrun.dto.UserDailyTaskLogDto;
import com.tencent.wxcloudrun.model.UserDailyTaskLog;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.UserDailyTaskLogService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-daily-tasks")
public class UserDailyTaskLogController {

    private static final Logger logger = LoggerFactory.getLogger(UserDailyTaskLogController.class);

    private final UserDailyTaskLogService userDailyTaskLogService;
    private final WxuserService wxuserService;

    @Autowired
    public UserDailyTaskLogController(UserDailyTaskLogService userDailyTaskLogService,WxuserService wxuserService) {
        this.userDailyTaskLogService = userDailyTaskLogService;
        this.wxuserService = wxuserService;
    }

    @PostMapping("/log")
    public ApiResponse addTaskLog(
            @RequestHeader(value = "X-WX-OPENID", required = true) String openid, // Set required = true
            @RequestBody UserDailyTaskLogDto taskLogDto) {
        
        // Basic check for openid, though @RequestHeader(required=true) handles absence.
        // Service layer will perform more detailed validation if necessary.
        if (!StringUtils.hasText(openid)) {
             // This block might be redundant if required=true, as Spring would return 400.
            logger.warn("X-WX-OPENID header is missing or empty.");
            return ApiResponse.error("X-WX-OPENID header is missing or empty.");
        }

        try {
            userDailyTaskLogService.addTaskLog(openid, taskLogDto);
            //更新会员时间
            if(taskLogDto.getType().equals("exchange")){
                Optional<WxUser> userOp = wxuserService.getUser(openid);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiredAt = userOp.get().getVipExpiredAt();

                // 计算新的过期时间
                LocalDateTime newExpiredAt;
                if (expiredAt == null || expiredAt.isBefore(now)) {
                    newExpiredAt = now.plusSeconds((long)(taskLogDto.getDays() * 24 * 60 * 60));
                } else {
                    newExpiredAt = expiredAt.plusSeconds((long)(taskLogDto.getDays() * 24 * 60 * 60));
                }

                wxuserService.updateVipExpired(userOp.get().getId(), newExpiredAt);
            }
            // HTTP 201 Created is more appropriate for resource creation.
            // However, to keep it simple with your ApiResponse structure, 200 OK with success is also common.
            return ApiResponse.ok();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid argument for addTaskLog for openid {}: {}", openid, e.getMessage());
            return ApiResponse.error("40002");
        } catch (Exception e) {
            logger.error("Error adding task log for openid: " + openid, e);
            return ApiResponse.error("50001");
        }
    }

    @GetMapping("/counts")
    public ApiResponse getDailyTaskCounts(
            @RequestHeader(value = "X-WX-OPENID", required = true) String openid) { // Set required = true

        if (!StringUtils.hasText(openid)) {
            // Redundant if required=true
            logger.warn("X-WX-OPENID header is missing or empty for getDailyTaskCounts.");
            return ApiResponse.error("40001");
        }

        try {
            List<TaskCountDto> counts = userDailyTaskLogService.getDailyTaskCounts(openid);
            return ApiResponse.ok(counts);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid argument for getDailyTaskCounts for openid {}: {}", openid, e.getMessage());
            return ApiResponse.error("40002");
        } catch (Exception e) {
            logger.error("Error retrieving daily task counts for openid: " + openid, e);
            return ApiResponse.error("50002");
        }
    }

    @GetMapping("/points")
    public ApiResponse getPoints(
            @RequestHeader(value = "X-WX-OPENID", required = true) String openid) {
        return ApiResponse.ok(userDailyTaskLogService.getPoints(openid));
    }

    /**
     * 获取用户当天的任务日志
     * @param openid 用户的OpenID
     * @return 用户当天的任务日志列表
     */
    @GetMapping("/logs")
    public ApiResponse getTodayTaskLogs(
            @RequestHeader(value = "X-WX-OPENID", required = true) String openid) {
        
        if (!StringUtils.hasText(openid)) {
            logger.warn("X-WX-OPENID header is missing or empty for getTodayTaskLogs.");
            return ApiResponse.error("40001");
        }

        try {
            List<UserDailyTaskLog> logs = userDailyTaskLogService.getTodayTaskLogs(openid);
            List<UserDailyTaskLog> logsLimits =userDailyTaskLogService.getLimitedLogs(openid);
            logs.addAll(logsLimits);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("Error retrieving today task logs for openid: " + openid, e);
            return ApiResponse.error("50003");
        }
    }
}