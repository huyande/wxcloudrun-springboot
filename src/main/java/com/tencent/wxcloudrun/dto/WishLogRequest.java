package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishLogRequest {
    private Integer mid;
    private Integer uid;
    private Integer wid;
    private Integer point;
    private String info;
    private Integer status;
    private String unitType;
    private String endTime;
    private Integer amount;
    private Integer unit;
}
