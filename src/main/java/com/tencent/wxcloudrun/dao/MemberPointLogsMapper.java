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
    void updateById(@Param("id") Long id, @Param("num") Integer num, @Param("uid") Integer uid);
    void delete(@Param("id") Long id);
    List<MemberPointLogs> getLogsByMidAndDate(@Param("mid") Integer mid, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    Integer getPointSumByMid(@Param("mid")Integer mid);
    MemberPointLogs getLogsByDayMidAndRuleId( @Param("day")String day,  @Param("mid")Integer mid,  @Param("ruleId")Integer ruleId);

    Integer getPointDaysByMid(@Param("mid")Integer mid);

    List<Map<String, Object>> getPointLogsByMidAndMonth(@Param("mid")Integer mid);

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
}
