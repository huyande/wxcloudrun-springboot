package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RuleAchievement {
    private Integer id;
    private Integer mId;
    private Integer ruleId;
    private String title;
    private String img;
    private String conditionType;
    private Integer conditionValue;
    private String conditionDesc;
    private String rewardType;
    private Integer rewardValue;
    private Integer status; // 状态 0开启 1删除
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 