package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GameRewardLog implements Serializable {
    private Integer id;
    private Integer mid;          // 成员ID
    private Integer gameGroup;    // 游戏分组
    private String rewardName;    // 奖励名称
    private String rewardType;    // 奖励类型
    private String icon;          // 图标
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 