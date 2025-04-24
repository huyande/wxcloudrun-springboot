package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MemberPointLogsService {
    List<MemberPointLogs> getLogsByMidAndDate(Integer mid, LocalDateTime startAt,LocalDateTime endAt);
    MemberPointLogs insert(MemberPointLogsRequest memberPointLogs);
    Map<String,Object> insertAndCheckRule(MemberPointLogsRequest memberPointLogs);
    Integer getPointSumByMid(Integer mid);
    //根据mid获取统计用户的积分
    Integer getPointDaysByMid(Integer mid);
    List<Map<String, Object>> getPointLogsByMidAndMonth(Integer mid);
    List<Map<String, Object>> getPointLogsByMidAndSpecificMonth(Integer mid, String yearMonth);

    Integer getAllCountLogsByDayMid(Integer mid);

    Map<String, Object> getPointLogsByMid(Integer mid, Integer page);

    List<Map<String, Object>> getWeeklyPointLogs(Integer mid, LocalDateTime startTime, LocalDateTime endTime);

    Map<String, Integer> getStreakInfo(Integer mid, Integer ruleId);

    List<Map<String, Object>> getMonthlyCheckInRecords(Integer mid, Integer ruleId, String yearMonth);

    List<Map<String, Object>> getPointLogsByDateRange(Integer mid, Integer ruleId, String startDay, String endDay);

    List<Map<String, Object>> getYearlyHeatmap(Integer mid, Integer ruleId, Integer year);

    Integer getCurrentDayPointSumByMid(Integer mid, String date);

    List<Map<String, Object>> getPointlogCurrentDayDetail(Integer mid, String day);

    List<Map<String, Object>> getPointLogsByDateRangeTotal(Integer mid, String startDay, String endDay);

    List<Map<String, Object>> getYearlyHeatmapAll(Integer mid, Integer year);

    /**
     * 计算剩余积分
     * @param mid
     * @return
     */
    Integer getLastPointSum(Integer mid);

    HashMap<String, Integer> getPointInfoByMid(Integer mid);
}
