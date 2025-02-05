package com.tencent.wxcloudrun.model.drugRecord;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WxUsers {
    private Integer id;
    private String openid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 