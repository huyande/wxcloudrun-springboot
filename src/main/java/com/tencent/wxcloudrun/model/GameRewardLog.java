package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 优秀记录日志
 */
@Data
public class GameRewardLog implements Serializable {
    private Integer id;
    private Integer mid;          // 成员ID
    private Integer gameGroup;    // 游戏分组 0 转盘
    private String rewardName;    // 奖励名称
    private String rewardType;    // 奖励类型
    private String icon;          // 图标
    private Integer status; //状态 0 未兑现 1 已兑现
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 