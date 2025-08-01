package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WeeklyReportDTO;
import com.tencent.wxcloudrun.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 数据统计指标控制器
 * 提供用户积分和积分兑换相关的统计分析接口
 */
@RestController
@RequestMapping("statistics")
public class StatisticsController {

    final StatisticsService statisticsService;
    final Logger logger;

    public StatisticsController(@Autowired StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
        this.logger = LoggerFactory.getLogger(StatisticsController.class);
    }

    /**
     * 按月份统计积分增减情况
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 季节ID
     * @return API响应，包含适用于Echarts柱状图的数据结构
     */
    @GetMapping("/points/monthly/{mid}")
    public ApiResponse getPointsStatisticsByMonth(
            @PathVariable Integer mid,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            // 调用服务层方法获取按月份统计的积分数据
            Map<String, Object> result = statisticsService.getPointsStatisticsByMonth(mid, startDate, endDate, seasonId);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            // 记录错误日志
            logger.error("按月份统计积分失败", e);
            return ApiResponse.error("按月份统计积分失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取综合统计数据
     * 包括积分统计、任务完成情况、积分抽奖和心愿兑换等数据
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 季节ID
     * @return API响应，包含各类统计数据
     */
    @GetMapping("/comprehensive/{mid}")
    public ApiResponse getComprehensiveStatistics(
            @PathVariable Integer mid,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            // 调用服务层方法获取综合统计数据
            Map<String, Object> result = statisticsService.getComprehensiveStatistics(mid, startDate, endDate, seasonId);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            // 记录错误日志
            logger.error("获取综合统计数据失败", e);
            return ApiResponse.error("获取综合统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取打卡类型积分占比统计
     * 统计一段时间内不同类型打卡任务获得的积分占比
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 季节ID
     * @return API响应，包含适用于Echarts饼图的数据结构
     */
    @GetMapping("/checkin/type-ratio/{mid}")
    public ApiResponse getCheckInTypePointsRatio(
            @PathVariable Integer mid,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            // 调用服务层方法获取打卡类型积分占比数据
            Map<String, Object> result = statisticsService.getCheckInTypePointsRatio(mid, startDate, endDate, seasonId);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            // 记录错误日志
            logger.error("获取打卡类型积分占比统计失败", e);
            return ApiResponse.error("获取打卡类型积分占比统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取成就周报
     * 生成一个可视化的、个性化的周报，展示孩子在指定周的任务类型分布、积分表现和成长轨迹
     * 
     * @param mid 会员ID
     * @param date 查询周报的指定日期（格式: yyyy-MM-dd），如果未提供则默认为当前日期，系统将计算并返回包含该日期的完整周（周一至周日）的报告
     * @param seasonId 季节ID
     * @return API响应，包含成就周报数据
     */
    @GetMapping("/weekly-report/{mid}")
    public ApiResponse getWeeklyReport(
            @PathVariable Integer mid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            LocalDate reportDate = (date == null) ? LocalDate.now() : date;
            WeeklyReportDTO result = statisticsService.getWeeklyReport(mid, reportDate, seasonId);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("获取成就周报失败", e);
            return ApiResponse.error("获取成就周报失败: " + e.getMessage());
        }
    }
} 