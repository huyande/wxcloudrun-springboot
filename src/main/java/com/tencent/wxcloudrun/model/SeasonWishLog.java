package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeasonWishLog {
    // SeasonWishLog对象的唯一标识
    private Long id;
    // 关联的赛季ID
    private Long seasonId;
    private Integer mid;
    private Integer uid;
    private Long wid; // 指向 SeasonWish 的ID
    private Integer point;
    private String info;
    private Integer status; //0 代使用 1 已使用 -1 撤销 2 是正在运行的任务
    private Integer amount; //兑换的数量

    private String unitType; //点数类型
    private LocalDateTime endTime; //如果兑换的是时间相关的，则需要记录完成的时间是多少
    private Integer unit;
    // 创建时间
    private LocalDateTime createdAt;
    // 更新时间
    private LocalDateTime updatedAt;
} 