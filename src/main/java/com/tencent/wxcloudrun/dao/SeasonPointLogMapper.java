package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonPointLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SeasonPointLogMapper {
    
    /**
     * 根据ID获取赛季积分日志
     * @param id 日志ID
     * @return 赛季积分日志
     */
    SeasonPointLog getById(Long id);
    
    /**
     * 获取某赛季某成员的所有积分日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 日志列表
     */
    List<SeasonPointLog> getBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员某日期范围的积分日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 日志列表
     */
    List<SeasonPointLog> getLogsBySeasonIdMidAndDateRange(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * 获取某赛季某成员某规则某日期范围的积分日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 日志列表
     */
    List<SeasonPointLog> getLogsBySeasonIdMidRuleIdAndDateRange(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("ruleId") Long ruleId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * 获取周级别的积分统计（以星期几分组）
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param weekStart 周开始时间
     * @param weekEnd 周结束时间
     * @return 按规则ID和星期几分组的积分统计
     */
    List<Map<String, Object>> getWeeklyPointLogs(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd);
    
    /**
     * 获取某赛季某成员的当日积分总和
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param day 日期
     * @return 积分总和
     */
    Integer getCurrentDayPointSumBySeasonIdAndMid(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("day") String day);
    
    /**
     * 获取某赛季某成员的总积分
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 积分总和
     */
    Integer getPointSumBySeasonIdAndMid(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员的打卡天数
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 打卡天数
     */
    Integer getPointDaysBySeasonIdAndMid(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员的积分概要信息
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 积分概要信息
     */
    Map<String, Integer> getPointInfoBySeasonIdAndMid(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid);
    
    /**
     * 创建积分日志
     * @param seasonPointLog 积分日志
     * @return 影响的行数
     */
    int insert(SeasonPointLog seasonPointLog);
    
    /**
     * 更新积分日志
     * @param seasonPointLog 积分日志
     * @return 影响的行数
     */
    int update(SeasonPointLog seasonPointLog);
    
    /**
     * 删除积分日志
     * @param id 日志ID
     * @return 影响的行数
     */
    int delete(Long id);
    
    /**
     * 获取某赛季某成员某规则的连续打卡信息
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 连续打卡信息
     */
    Map<String, Integer> getStreakInfo(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("ruleId") Long ruleId);
    
    /**
     * 分页获取某赛季某成员的积分日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param offset 偏移量
     * @param limit 限制数
     * @return 日志列表
     */
    List<SeasonPointLog> getLogsByPage(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);
    
    /**
     * 获取某赛季某成员在日期范围内的总积分
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param startDateTime 开始日期时间
     * @param endDateTime 结束日期时间
     * @return 积分总和
     */
    Integer getTotalPointsByDateRange(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("startDate") String startDateTime,
            @Param("endDate") String endDateTime);
            
    /**
     * 获取某赛季某成员的积分日志总数
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 积分日志总数
     */
    Integer getCountBySeasonIdAndMid(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员的积分记录总天数
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 积分记录总天数
     */
    Integer getTotalLogDays(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid);
    
    /**
     * 获取某赛季下某成员某规则的打卡日志，按日期倒序排列
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 日志列表（包含日期信息）
     */
    List<Map<String, Object>> getCheckInLogsBySeasonIdAndMidAndRuleId(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("ruleId") Long ruleId);
    
    /**
     * 获取某赛季下某成员某规则的积分日志数量
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 日志数量
     */
    Integer getPointLogsCountBySeasonIdMidAndRuleId(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("ruleId") Long ruleId);
    
    /**
     * 获取某赛季下某成员某规则的积分总和
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 积分信息，包含积分总和
     */
    Map<String, Object> getPointSumBySeasonIdMidAndRuleId(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("ruleId") Long ruleId);

    /**
     * 获取某赛季下某成员某规则的积分总和 (直接返回值)
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 积分总和 (num 字段)
     */
    Integer getSumNumByMidAndRuleIdAndSeasonId(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("ruleId") Long ruleId);

    /**
     * 获取某赛季某成员某规则在日期范围内的打卡天数（每日仅计一次）
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡天数
     */
    Integer getCheckInCountByMidAndRuleIdAndSeasonIdAndDateRange(
        @Param("seasonId") Long seasonId,
        @Param("mid") Integer mid,
        @Param("ruleId") Integer ruleId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    // 赛季统计：按月份统计积分增减情况
    List<Map<String, Object>> getPointsStatisticsByMonth(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // 赛季统计：打卡类型积分占比
    List<Map<String, Object>> getCheckInTypePointsRatio(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // 赛季统计：打卡次数前N的任务
    List<Map<String, Object>> getTopTasksByDateRange(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("limit") Integer limit);
    // 赛季统计：抽奖总次数
    Integer getLotteryCountByDateRange(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // 赛季统计：抽奖获取的积分
    Integer getLotteryPointsByDateRange(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate);
    // 赛季统计：打卡总次数
    Integer getCheckInTimesByDateRange(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据赛季ID删除所有积分日志
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteBySeasonId(Long seasonId);

    SeasonPointLog getLogsBySeasonIdMidAndDayAndRuleId(@Param("day")String day, @Param("seasonId")Long seasonId, @Param("mid")Integer mid,@Param("ruleId")Integer ruleId);

    /**
     * 获取某赛季下某成员某日的积分日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param day 日期
     * @return 日志列表
     */
    List<Map<String, Object>> getPointlogCurrentDayDetail(@Param("seasonId")Long seasonId, @Param("mid")Integer mid, @Param("day")String day);

    List<Map<String, Object>> getPointLogsByMidAndSpecificMonth(@Param("mid")Integer mid, @Param("yearMonth")String yearMonth, @Param("seasonId")Long seasonId);

    void deleteBySeasonIdAndReamrk(@Param("seasonId")Long seasonId, @Param("remark")String remark,@Param("mid")Integer mid,@Param("type")Integer type);
}