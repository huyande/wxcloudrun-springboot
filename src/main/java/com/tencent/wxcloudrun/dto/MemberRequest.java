package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class MemberRequest {
    private Integer role;
    private Integer uid;//要绑定的user Id
    private String name;//名字
    private Integer gender;//性别
    private Integer pointTotalCount;//初始化积分
    private String avatar;//头像URL
}
