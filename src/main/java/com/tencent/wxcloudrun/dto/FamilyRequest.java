package com.tencent.wxcloudrun.dto;


import lombok.Data;

@Data
public class FamilyRequest {
    private String code;
    private Integer role;
    private Integer uid;
    private Integer isRead;
    private Integer bindMid;
}
