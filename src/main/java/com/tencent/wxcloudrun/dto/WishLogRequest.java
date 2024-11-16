package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class WishLogRequest {
    private Integer mid;
    private Integer uid;
    private Integer wid;
    private Integer point;
    private String info;
    private Integer status;
}
