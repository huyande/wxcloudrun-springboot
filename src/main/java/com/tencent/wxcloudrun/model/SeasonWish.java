package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SeasonWish {

    public SeasonWish() {
    }

    public SeasonWish(String title, String img, Integer needPoint, Integer unit, String unitType, Integer type,Long seasonId) {
        this.title = title;
        this.img = img;
        this.needPoint = needPoint;
        this.unit = unit;
        this.unitType = unitType;
        this.type = type;
        this.seasonId = seasonId;
    }

    // SeasonWish对象的唯一标识
    private Long id;
    // 关联的赛季ID
    private Long seasonId;
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
    /**
     * 兑换限制，存储为JSON字符串
     * <p>
     * 示例: <code>{"enabled":true, "limit":5, "unit":"WEEKLY"}</code>
     * <ul>
     *   <li><b>enabled</b>: boolean - 是否启用限制</li>
     *   <li><b>limit</b>: int - 限制次数</li>
     *   <li><b>unit</b>: String - 时间单位 (DAILY, WEEKLY, MONTHLY, YEARLY)</li>
     * </ul>
     */
    private String exchangeLimit;
    // 创建时间
    private LocalDateTime createdAt;
    // 更新时间
    private LocalDateTime updatedAt;
} 