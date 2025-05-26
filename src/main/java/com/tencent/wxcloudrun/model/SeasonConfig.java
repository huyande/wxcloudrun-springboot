package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeasonConfig {
    private Long id;
    private Integer mId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String tokenName;
    private String tokenIcon;
    private Integer conversionRate;
    private Integer initialPoints;
    private Integer status; // 0:未开始, 1:进行中, 2:已结束, 3:已归档
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 