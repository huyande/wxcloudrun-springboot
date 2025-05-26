package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.model.SeasonPointLog;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员积分日志服务接口
 * 支持常规模式和赛季模式
 */
public interface MemberPointLogsService {
    /**
     * 获取指定用户在指定日期范围内的日志
     * 
     * @param mid 会员ID
     * @param startAt 开始时间
     * @param endAt 结束时间
     * @param seasonId 赛季ID，如果为null则获取常规日志
     * @return 日志列表
     */
    <T> List<T> getLogsByMidAndDate(Integer mid, LocalDateTime startAt, LocalDateTime endAt, Long seasonId, Class<T> expectedType);
    
    /**
     * 添加积分日志
     * 
     * @param memberPointLogs 日志请求
     * @param seasonId 赛季ID，如果为null则添加常规日志
     * @return 添加的日志
     */
    <T> T insert(MemberPointLogsRequest memberPointLogs, Long seasonId, Class<T> expectedType);
    
    /**
     * 添加积分日志并检查规则
     * 
     * @param memberPointLogs 日志请求
     * @param seasonId 赛季ID，如果为null则操作常规数据
     * @return 返回结果
     */
    Map<String, Object> insertAndCheckRule(MemberPointLogsRequest memberPointLogs, Long seasonId);
    
    /**
     * 获取用户积分总和
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 积分总和
     */
    Integer getPointSumByMid(Integer mid, Long seasonId);
    
    /**
     * 获取用户打卡天数
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 打卡天数
     */
    Integer getPointDaysByMid(Integer mid, Long seasonId);
    
    /**
     * 获取用户按月分组的积分日志
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 按月分组的积分日志
     */
    List<Map<String, Object>> getPointLogsByMidAndMonth(Integer mid, Long seasonId);
    
    /**
     * 获取用户指定月份的积分日志
     * 
     * @param mid 会员ID
     * @param yearMonth 年月（格式：yyyy-MM）
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 指定月份的积分日志
     */
    List<Map<String, Object>> getPointLogsByMidAndSpecificMonth(Integer mid, String yearMonth, Long seasonId);
    
    /**
     * 获取用户所有按日期统计的日志数量
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 日志数量
     */
    Integer getAllCountLogsByDayMid(Integer mid, Long seasonId);
    
    /**
     * 分页获取用户的积分日志
     * 
     * @param mid 会员ID
     * @param page 页码
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 分页结果
     */
    Map<String, Object> getPointLogsByMid(Integer mid, Integer page, Long seasonId);
    
    /**
     * 获取用户一周内的打卡记录
     * 
     * @param mid 会员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 一周内的打卡记录
     */
    List<Map<String, Object>> getWeeklyPointLogs(Integer mid, LocalDateTime startTime, LocalDateTime endTime, Long seasonId);
    
    /**
     * 获取连续打卡信息
     * 
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 连续打卡信息
     */
    Map<String, Integer> getStreakInfo(Integer mid, Integer ruleId, Long seasonId);
    
    /**
     * 获取月度打卡记录
     * 
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param yearMonth 年月（格式：yyyy-MM）
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 月度打卡记录
     */
    List<Map<String, Object>> getMonthlyCheckInRecords(Integer mid, Integer ruleId, String yearMonth, Long seasonId);
    
    /**
     * 获取指定日期范围内的打卡记录
     * 
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param startDay 开始日期（格式：yyyy-MM-dd）
     * @param endDay 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 打卡记录
     */
    List<Map<String, Object>> getPointLogsByDateRange(Integer mid, Integer ruleId, String startDay, String endDay, Long seasonId);
    
    /**
     * 获取年度热力图数据
     * 
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param year 年份
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 热力图数据
     */
    List<Map<String, Object>> getYearlyHeatmap(Integer mid, Integer ruleId, Integer year, Long seasonId);
    
    /**
     * 获取当日积分总和
     * 
     * @param mid 会员ID
     * @param date 日期
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 当日积分总和
     */
    Integer getCurrentDayPointSumByMid(Integer mid, String date, Long seasonId);
    
    /**
     * 获取指定日期的积分详情
     * 
     * @param mid 会员ID
     * @param day 日期
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 积分详情
     */
    List<Map<String, Object>> getPointlogCurrentDayDetail(Integer mid, String day, Long seasonId);
    
    /**
     * 获取指定日期范围内的打卡记录总和
     * 
     * @param mid 会员ID
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 打卡记录总和
     */
    List<Map<String, Object>> getPointLogsByDateRangeTotal(Integer mid, String startDay, String endDay, Long seasonId);
    
    /**
     * 获取所有规则的年度热力图数据
     * 
     * @param mid 会员ID
     * @param year 年份
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 热力图数据
     */
    List<Map<String, Object>> getYearlyHeatmapAll(Integer mid, Integer year, Long seasonId);
    
    /**
     * 计算剩余积分
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 剩余积分
     */
    Integer getLastPointSum(Integer mid, Long seasonId);
    
    /**
     * 获取用户积分信息
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规数据
     * @return 积分信息
     */
    HashMap<String, Integer> getPointInfoByMid(Integer mid, Long seasonId);
}
