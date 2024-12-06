package com.tencent.wxcloudrun.model;

import lombok.Data;

@Data
public class SubscribeLog {
    private Integer id;
    private String openid;
    private String templateId;
    private String createTime;
}
