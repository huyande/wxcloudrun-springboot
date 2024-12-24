package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberPointLogsRequest {
    private String day;//日期
    private Integer mid;//要绑定的member Id
    private Integer num;//积分
    private Integer uid;//创建人的id
    private Integer ruleId;//规则id
    private Integer type;//类型 0 规则 1 游戏
}
