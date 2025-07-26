package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 成就周报数据传输对象
 * 用于封装一周内孩子的任务完成情况、积分表现和成长轨迹
 */
@Data
public class WeeklyReportDTO implements Serializable {
    
    /**
     * 趋势状态常量
     */
    public static class TrendStatus {
        /** 增长 */
        public static final String UP = "UP";
        /** 下降 */
        public static final String DOWN = "DOWN";
        /** 持平 */
        public static final String EQUAL = "EQUAL";
        /** 混合状态 */
        public static final String MIXED = "MIXED";
    }
    
    /**
     * 周报时间范围描述
     * 例如："📅 周报时间范围：7月15日 - 7月21日"
     */
    private String dateRange;
    
    /**
     * 核心概览统计
     */
    private OverviewStats overview;
    
    /**
     * 每日打卡趋势图数据（用于柱状图）
     */
    private List<ChartData> dailyTrend;
    
    /**
     * 各任务类型完成次数分布（用于饼图）
     */
    private List<ChartData> taskDistribution;
    
    /**
     * 周报总结文字
     * 例如："本周孩子共打卡15次，获得积分120分..."
     */
    private String summaryText;
    
    /**
     * 高光时刻列表
     * 例如：["✅ 连续打卡3天", "✅ 首次完成阅读任务"]
     */
    private List<String> highlights;
    
    /**
     * 与上周对比数据
     */
    private ComparisonStats comparison;
    
    /**
     * 核心概览统计内部类
     */
    @Data
    public static class OverviewStats {
        /**
         * 打卡总次数
         */
        private int totalCheckIns;
        
        /**
         * 获得总积分（所有num > 0的总和）
         */
        private int pointsEarned;
        
        /**
         * 心愿消费积分
         */
        private int wishPointsSpent;
        
        /**
         * 惩罚/其他扣分（所有num < 0的总和）
         */
        private int penaltyPointsLost;
        
        /**
         * 活跃天数（有打卡行为的天数）
         */
        private int activeDays;
    }
    
    /**
     * 图表数据内部类
     */
    @Data
    public static class ChartData {
        /**
         * 数据标签
         * 例如："周一" 或 "学习类"
         */
        private String name;
        
        /**
         * 数据值
         */
        private Integer value;
        
        public ChartData() {}
        
        public ChartData(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }
    
    /**
     * 对比统计内部类
     */
    @Data
    public static class ComparisonStats {
        /**
         * 打卡次数对比
         * 例如："↑ 5次" 或 "↓ 2次"
         */
        private String checkInsComparison;
        
        /**
         * 获得积分对比
         * 例如："↑ 20分" 或 "↓ 10分"
         */
        private String pointsEarnedComparison;
        
        /**
         * 整体趋势状态
         * UP: 增长, DOWN: 下降, EQUAL: 持平, MIXED: 混合状态
         */
        private String trend;
    }
} 