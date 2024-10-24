package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Member implements Serializable {
    private Integer id;
    private String name;
    private Integer gender;
    private Integer pointTotal;//积分
    private Integer dakaDays;//打卡天数
    private Integer createUid;//创建成员的uid
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
