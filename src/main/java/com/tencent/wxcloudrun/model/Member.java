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
    private Integer uid;//创建成员的uid
    private String familyCode;
    private String avatar;//头像
    private String mode;//积分模式 0:积分模式 1:赛季模式
    private Long currentSeasonId;//当模式是赛季模式 的当前赛季id
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //这块是做身份是7的 只读模式
    private transient Integer isRead;//是否已读
    private transient Integer bindMid;//绑定成员的id 
    private transient Integer currentUid;//成员自己的uid
}
