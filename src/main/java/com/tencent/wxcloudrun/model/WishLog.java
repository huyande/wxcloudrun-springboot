package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WishLog {
    // Wish对象的唯一标识
    private Integer id;
    private Integer mid;
    private Integer uid;
    private Integer wid;
    private Integer num;
    // 创建时间
    private LocalDateTime createdAt;
    // 更新时间
    private LocalDateTime updatedAt;
}
