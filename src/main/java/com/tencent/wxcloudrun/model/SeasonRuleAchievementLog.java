package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 赛季成就日志实体类
 */
@Data
public class SeasonRuleAchievementLog implements Serializable {
    private Long id;
    private Long seasonId; // 赛季ID
    private Long sraId; // 赛季成就ID
    private Integer mid; // 成员ID
    private Integer rewardValue; // 奖励值
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 