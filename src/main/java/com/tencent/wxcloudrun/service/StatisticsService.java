package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.WeeklyReportDTO;

import java.time.LocalDate;
import java.util.Map;

/**
 * 统计服务接口
 * 提供各种数据统计分析功能
 */
public interface StatisticsService {
    
    /**
     * 按月份统计积分增减情况
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 赛季ID
     * @return 包含月份统计数据的Map，适用于Echarts柱状图展示
     *         数据结构：{
     *           "months": ["2023-01", "2023-02", ...], // 月份列表，按从小到大排序
     *           "increase": [100, 200, ...],           // 对应月份的积分增加量
     *           "decrease": [50, 80, ...]              // 对应月份的积分减少量
     *         }
     */
    Map<String, Object> getPointsStatisticsByMonth(Integer mid, String startDate, String endDate, Long seasonId);
    
    /**
     * 获取综合统计数据
     * 包括积分统计、任务完成情况、积分抽奖和心愿兑换等数据
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 赛季ID
     * @return 包含各类统计数据的Map
     *         数据结构：{
     *           "pointsStats": {                        // 积分统计
     *             "totalPoints": 1000,                  // 积分总数
     *             "consumedPoints": 500,                // 积分消耗总数
     *             "checkInCount": 30                    // 打卡总数
     *           },
     *           "topTasks": [                           // 打卡次数前5的任务
     *             {"name": "任务1", "count": 20},
     *             {"name": "任务2", "count": 15},
     *             ...
     *           ],
     *           "lotteryStats": {                       // 积分抽奖统计
     *             "lotteryCount": 10,                   // 抽奖总次数
     *             "lotteryPoints": 200                  // 抽奖获取的积分
     *           },
     *           "wishStats": {                          // 心愿兑换统计
     *             "exchangeCount": 5,                   // 兑换总次数
     *             "topWishes": [                        // 兑换排行
     *               {"name": "心愿1", "count": 3},
     *               {"name": "心愿2", "count": 2},
     *               ...
     *             ]
     *           }
     *         }
     */
    Map<String, Object> getComprehensiveStatistics(Integer mid, String startDate, String endDate, Long seasonId);
    
    /**
     * 获取打卡类型积分占比统计
     * 统计一段时间内不同类型打卡任务获得的积分占比
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 赛季ID
     * @return 包含打卡类型积分占比的Map，适用于Echarts饼图展示
     *         数据结构：{
     *           "total": 1000,                          // 总积分
     *           "data": [                               // 饼图数据
     *             {"name": "任务类型1", "value": 300},
     *             {"name": "任务类型2", "value": 200},
     *             ...
     *           ]
     *         }
     */
    Map<String, Object> getCheckInTypePointsRatio(Integer mid, String startDate, String endDate, Long seasonId);
    
    /**
     * 获取成就周报
     * 生成一个可视化的、个性化的周报，展示孩子在指定周的任务类型分布、积分表现和成长轨迹
     * 
     * @param mid 会员ID
     * @param reportDate 周报的指定日期，系统将计算包含该日期的周（周一至周日）
     * @param seasonId 赛季ID，如果为null则使用常规数据
     * @return 成就周报数据
     */
    WeeklyReportDTO getWeeklyReport(Integer mid, LocalDate reportDate, Long seasonId);
} 