package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 成就状态DTO
 */
@Data
public class AchievementStatusDto {
    // 成就基本信息
    private Long id;
    private Integer ruleId;
    private String title;
    private String img;
    private String conditionType;
    private Integer conditionValue;
    private String conditionDesc;
    private String rewardType;
    private Integer rewardValue;
    
    // 完成状态
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    
    // 当前进度
    private Integer currentProgress;
    private String progressDesc;
    
    // 还需要多少才能完成
    private Integer remainingProgress;
    private String remainingDesc;
} 