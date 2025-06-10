package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskConfig {
    private Long id;
    private String taskKey;
    private String title;
    private String description;
    private String type; // 'daily' 或 'limited_time'
    private String icon;
    private Integer points;
    private Integer maxDaily;
    
    // 限时任务相关字段
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean needReview;
    private String webviewUrl;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 