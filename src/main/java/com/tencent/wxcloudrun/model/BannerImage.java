package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BannerImage implements Serializable {
    // 唯一标识
    private Integer id;
    // 图片地址
    private String img;
    // 点击跳转链接
    private String url;
    // 状态：0-禁用，1-启用
    private Integer status;
    // 排序
    private Integer sort;
    // 创建时间
    private LocalDateTime createdAt;
    // 更新时间
    private LocalDateTime updatedAt;
} 