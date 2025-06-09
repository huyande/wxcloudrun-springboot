package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class WishRequest {
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
    private Integer type;
    // 愿望状态
    private Integer status; //0 正常 1 删除 或禁止
    // 兑换限制
    private String exchangeLimit;
}
