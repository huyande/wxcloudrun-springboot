package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Wish {

    public Wish() {
    }

    public Wish(String title, String img, Integer needPoint, Integer unit, String unitType, Integer type) {
        this.title = title;
        this.img = img;
        this.needPoint = needPoint;
        this.unit = unit;
        this.unitType = unitType;
        this.type = type;
    }

    // Wish对象的唯一标识
    private Integer id;
    // 关联的成员ID
    private Integer mid;
    // 愿望图片URL
    private String img;
    // 愿望标题
    private String title;
    // 愿望内容
    private String content;
    // 实现愿望所需的点数
    private Integer needPoint;
    // 单位数值
    private Integer unit;
    // 单位类型
    private String unitType;
    // 愿望类型
    private Integer type; // 0 系统 1 自己创建的
    private Integer status; // 0 开启 1 关闭
    // 创建时间
    private LocalDateTime createdAt;
    // 更新时间
    private LocalDateTime updatedAt;
}
