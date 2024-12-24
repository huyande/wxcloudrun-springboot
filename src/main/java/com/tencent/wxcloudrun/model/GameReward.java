package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GameReward implements Serializable {
    private Integer id;
    private Integer uid;
    private Integer gid;      // game_config的id
    private String name;      // 奖励名称
    private String type;      // 游戏类型：wheel、slot等
    private String icon;      // 图标
    private Double weight;    // 权重
    private String rewardType; // 0 积分 1其他
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 