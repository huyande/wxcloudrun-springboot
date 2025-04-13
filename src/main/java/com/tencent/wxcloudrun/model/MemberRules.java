package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemberRules implements Serializable {
    private Integer id;
    private Integer mid;
    private String name;
    private String type;
    private Integer typeSort;
    private String icon;
    private Integer iconType;
    private String weeks;//打卡周期
    private String content;//规则描述
    private Integer sort;//排序
    private Integer status;//状态 0禁用 1启用
    private Integer quickScore;//设置快捷固定的积分
    private Integer enablePomodoro;//是否开启番茄闹钟
    private Integer pomodoroTime;//番茄时常
    private Integer isAchievement;//是否开启成就奖励
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
