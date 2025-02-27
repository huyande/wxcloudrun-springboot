package com.tencent.wxcloudrun.model;

import lombok.Data;

@Data
public class Family {

    private Integer id;
    private Integer uid;
    private String code;
    private Integer isRead;//只读 1 开启
    private Integer bindMid;
}
