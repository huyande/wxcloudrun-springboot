package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RuleAchievementLog {
    private Integer id;
    private Integer raId;
    private Integer mid;
    private Integer rewardValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 