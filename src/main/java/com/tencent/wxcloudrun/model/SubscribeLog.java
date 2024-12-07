package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscribeLog {
    private Integer id;
    private String openid;
    private String templateId;
    private LocalDateTime createTime;
    private LocalDateTime sendTime;
}
