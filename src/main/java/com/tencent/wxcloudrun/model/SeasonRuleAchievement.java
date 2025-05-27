package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 赛季成就实体类
 */
@Data
public class SeasonRuleAchievement implements Serializable {
    private Long id;
    private Integer mId;
    private Long seasonId; // 赛季ID
    private Integer ruleId; // 规则ID
    private String title; // 成就标题
    private String img; // 成就图片
    private String conditionType; // 条件类型，如"连续"、"累计"、"积分"
    private Integer conditionValue; // 条件值
    private String rewardType; // 奖励类型
    private Integer rewardValue; // 奖励值
    private Integer status; // 状态 0开启 1删除
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
