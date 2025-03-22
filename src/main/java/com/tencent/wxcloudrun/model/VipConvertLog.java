package com.tencent.wxcloudrun.model;

import lombok.Data;

@Data
public class VipConvertLog {
    private Integer id;

    private String sourceOpenid;

    private String channel;

    private String targetOpenid;
}
