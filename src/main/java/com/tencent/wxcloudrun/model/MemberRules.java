package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemberRules implements Serializable {
    private Integer id;
    private Integer mid;
    private String name;
    private String type;
    private String icon;
    private Integer iconType;
    private String weeks;//打卡周期
    private String content;//规则描述
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}