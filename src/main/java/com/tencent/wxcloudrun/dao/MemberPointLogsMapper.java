package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MemberPointLogsMapper {
    void insertOne(MemberPointLogs memberPointLogs);
    void updateById(@Param("id") Long id,
                    @Param("num") Integer num,
                    @Param("uid") Integer uid,
                    @Param("remark") String remark,
                    @Param("conditionId") String conditionId,
                    @Param("status") Integer status,
                    @Param("endAt") LocalDateTime endAt);

    void updateStatusById(@Param("id") Long id,@Param("status") Integer status);

    void delete(@Param("id") Long id);
    List<MemberPointLogs> getLogsByMidAndDate(@Param("mid") Integer mid, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    Integer getPointSumByMid(@Param("mid")Integer mid);
    MemberPointLogs getLogsByDayMidAndRuleId( @Param("day")String day,  @Param("mid")Integer mid,  @Param("ruleId")Integer ruleId);

    Integer getPointDaysByMid(@Param("mid")Integer mid);

    List<Map<String, Object>> getPointLogsByMidAndMonth(@Param("mid")Integer mid);
    
    List<Map<String, Object>> getPointLogsByMidAndSpecificMonth(@Param("mid")Integer mid, @Param("yearMonth")String yearMonth);

    Integer getAllCountLogsByDayMid(@Param("mid") Integer mid);

    void deleteByMid(@Param("mid") Integer mid);
    void deleteByRuleId(@Param("ruleId") Integer ruleId);

    List<Map<String, Object>> getPointLogsByMid(@Param("mid")Integer mid, @Param("pageSize")Integer pageSize,@Param("offset") Integer offset);

    Integer getPointLogsByMidCount(@Param("mid")Integer mid);

    List<Map<String, Object>> getWeeklyPointLogs(@Param("mid")Integer mid, @Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime);

    MemberPointLogs getYesterdayLog(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId);

    MemberPointLogs getBeforeYesterdayLog(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId);

    List<MemberPointLogs> getCheckInRecords(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId);

    Integer getPointSumByMidAndRuleId(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId);

    Integer getPointLogsCountByMidAndRuleId(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId);

    List<MemberPointLogs> getMonthlyCheckInRecords(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId, @Param("yearMonth") String yearMonth);

    List<MemberPointLogs> getPointLogsByDateRange(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId, 
        @Param("startDay") LocalDateTime startDay, @Param("endDay") LocalDateTime endDay);

    List<Map<String, Object>> getYearlyHeatmap(@Param("mid") Integer mid, @Param("ruleId") Integer ruleId, @Param("year") Integer year);

    Integer getCurrentDayPointSumByMid(@Param("mid") Integer mid, @Param("day") String day);

    List<Map<String, Object>> getPointlogCurrentDayDetail(@Param("mid") Integer mid, @Param("day") String day);

    List<MemberPointLogs> getPointLogsByDateRangeTotal(@Param("mid") Integer mid,@Param("startDay") LocalDateTime startDay, @Param("endDay") LocalDateTime endDay);

    List<Map<String, Object>> getYearlyHeatmapALL(Integer mid, Integer year);

    /**
     * 按日期和类型统计积分
     * 
     * @param mid 会员ID
     * @param day 日期
     * @return 按类型分组的积分统计
     */
    List<Map<String, Object>> getDailyPointStatsByType(@Param("mid") Integer mid, @Param("day") String day);

    /**
     * 获取某用户在指定日期范围内的每日积分总和（按天分组）
     * 
     * @param mid 会员ID
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 每日积分总和列表
     */
    List<Map<String, Object>> getDailyTotalPointsByMidAndDateRange(
            @Param("mid") Integer mid,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    /**
     * 按月份统计积分增减情况
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按月份汇总的积分统计数据
     */
    List<Map<String, Object>> getPointsStatisticsByMonth(@Param("mid") Integer mid, 
                                                        @Param("startDate") String startDate, 
                                                        @Param("endDate") String endDate);

    /**
     * 获取指定时间范围内的积分总数
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 积分总数
     */
    Integer getTotalPointsByDateRange(@Param("mid") Integer mid, 
                                     @Param("startDate") String startDate, 
                                     @Param("endDate") String endDate);
    
    /**
     * 获取指定时间范围内的打卡总数
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡总数
     */
    Integer getCheckInCountByDateRange(@Param("mid") Integer mid, 
                                      @Param("startDate") String startDate, 
                                      @Param("endDate") String endDate);
    
    /**
     * 获取指定时间范围内打卡次数前N的任务
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制返回的任务数量
     * @return 任务列表，包含任务名称和打卡次数
     */
    List<Map<String, Object>> getTopTasksByDateRange(@Param("mid") Integer mid, 
                                                   @Param("startDate") String startDate, 
                                                   @Param("endDate") String endDate,
                                                   @Param("limit") Integer limit);
    
    /**
     * 获取指定时间范围内抽奖获取的积分情况
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 抽奖获取的积分总数
     */
    Integer getLotteryPointsByDateRange(@Param("mid") Integer mid, 
                                       @Param("startDate") String startDate, 
                                       @Param("endDate") String endDate);

    /**
     * 获取指定时间范围内打卡次数总数
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡次数总数
     */
    Integer getCheckInTimesByDateRange(@Param("mid") Integer mid, 
                                      @Param("startDate") String startDate, 
                                      @Param("endDate") String endDate);

    /**
     * 获取指定时间范围内打卡类型积分占比
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按任务类型分组的积分统计数据
     */
    List<Map<String, Object>> getCheckInTypePointsRatio(@Param("mid") Integer mid, 
                                                      @Param("startDate") String startDate, 
                                                      @Param("endDate") String endDate);

    /**
     * 检查指定时间范围内的连续打卡记录
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 该日期范围内的打卡记录数量
     */
    Integer getCheckInCountBetweenDates(Integer mid, Integer ruleId, LocalDateTime startDate, LocalDateTime endDate);

    void deleteByRemark(@Param("remark") String remark,@Param("mid")Integer mid,@Param("type")Integer type);

    /**
     * 获取某成员某规则在指定日期范围内的积分总和
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 积分总和
     */
    Integer getPointSumByMidRuleIdAndDateRange(@Param("mid") Integer mid, 
                                              @Param("ruleId") Integer ruleId,
                                              @Param("startDate") String startDate, 
                                              @Param("endDate") String endDate);

    // ========== 周报功能新增查询方法 ==========

    /**
     * 获取指定时间范围内的惩罚扣分总数（所有num < 0的记录）
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 惩罚扣分总数（绝对值）
     */
    Integer getPenaltyPointsByDateRange(@Param("mid") Integer mid, 
                                       @Param("startDate") String startDate, 
                                       @Param("endDate") String endDate);

    /**
     * 获取指定时间范围内的活跃天数（有打卡记录的不同日期数量）
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 活跃天数
     */
    Integer getActiveDaysByDateRange(@Param("mid") Integer mid, 
                                    @Param("startDate") String startDate, 
                                    @Param("endDate") String endDate);

//    List<Map<String, Object>> getDailyTotalPointsByMidAndDateRange(@Param("mid") Integer mid,@Param("startDateTime") LocalDateTime startDay, @Param("endDateTime") LocalDateTime endDay);
}
