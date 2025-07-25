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
    private Integer type; //积分类型 0:规则积分 1:转盘游戏产生的积分 2是趣味计算产生的积分 3 用户自行修改的积分 4 记录课时 增加积分 5 计时打卡
    private String remark; // 备注
    private Integer pomodoroTime; // 番茄时长
    private String conditionId; //配置完成条件积分的id
    private Integer status;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 