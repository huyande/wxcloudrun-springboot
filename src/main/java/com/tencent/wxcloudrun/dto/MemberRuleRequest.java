package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class MemberRuleRequest {
    private Integer mid;//要绑定的member Id
    private String name;//名字
    private String type;//类型
    private String icon;   //图标
    private Integer iconType;//图标类型
    private String weeks;//星期
    private String content;//内容
    private Integer sort;
    private Integer quickScore;
    private Integer typeSort;
    private Integer enablePomodoro;//是否开启番茄闹钟
    private Integer pomodoroTime;//番茄时常
    private Integer isAchievement;//是否开启成就奖励
    private String completionConditions; //完成条件配置
}
