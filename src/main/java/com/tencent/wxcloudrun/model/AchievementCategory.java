package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AchievementCategory {
    private Integer id;
    private String categoryName;
    private String achievementName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 