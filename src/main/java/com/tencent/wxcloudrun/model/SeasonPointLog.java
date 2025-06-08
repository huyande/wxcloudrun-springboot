package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SeasonPointLog implements Serializable {
    private Long id;
    private Long seasonId; // 关联的赛季ID
    private LocalDateTime day; // 日期 yyyy-MM-dd
    private Integer mid;
    private Integer uid;
    private Long ruleId;
    private Integer num;
    private Integer type; // 积分类型 0:规则积分 1:调整/补偿 2:赛季转换 3:愿望消耗
    private String remark; // 备注
    private Integer pomodoroTime; // 番茄时长
    private Integer conditionId; //配置完成条件积分的id
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 