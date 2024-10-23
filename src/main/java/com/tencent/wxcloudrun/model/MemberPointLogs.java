package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemberPointLogs implements Serializable {
    private Long id;
    private LocalDateTime day;// 日期 yyyy-MM-dd
    private Integer mid;
    private Integer uid;
    private Integer ruleId;
    private Integer num;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
