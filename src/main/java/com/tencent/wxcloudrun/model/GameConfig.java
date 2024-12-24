package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GameConfig implements Serializable {
    private Integer id;
    private Integer uid;
    private Integer point;  // 积分
    private Integer number; // 次数限制
    private String type;    // 类型
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 