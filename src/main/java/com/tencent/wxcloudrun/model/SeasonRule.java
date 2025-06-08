package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 赛季规则实体类
 * 对应赛季模式下的规则
 */
@Data
public class SeasonRule implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 赛季ID
     */
    private Long seasonId;
    
    /**
     * 会员ID
     */
    private Integer mId;
    
    /**
     * 规则名称
     */
    private String name;
    
    /**
     * 规则类型
     */
    private String type;
    
    /**
     * 规则图标
     */
    private String icon;
    
    /**
     * 图标类型
     */
    private Integer iconType;
    
    /**
     * 适用的星期（逗号分隔，如"星期一,星期二"）
     */
    private String weeks;
    
    /**
     * 规则描述内容
     */
    private String content;
    
    /**
     * 排序号
     */
    private Integer sort;
    
    /**
     * 快速打卡积分值
     */
    private Integer quickScore;
    
    /**
     * 规则状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 类型排序
     */
    private Integer typeSort;
    
    /**
     * 是否启用番茄钟：0-不启用，1-启用
     */
    private Integer enablePomodoro;
    
    /**
     * 番茄钟时间（分钟）
     */
    private Integer pomodoroTime;
    
    /**
     * 是否是成就：0-否，1-是
     */
    private Integer isAchievement;

    private String completionConditions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 