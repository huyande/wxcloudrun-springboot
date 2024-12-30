package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MemberPointLogsService {
    List<MemberPointLogs> getLogsByMidAndDate(Integer mid, LocalDateTime startAt,LocalDateTime endAt);
    MemberPointLogs insert(MemberPointLogsRequest memberPointLogs);
    Integer getPointSumByMid(Integer mid);
    //根据mid获取统计用户的积分
    Integer getPointDaysByMid(Integer mid);
    List<Map<String, Object>> getPointLogsByMidAndMonth(Integer mid);

    Integer getAllCountLogsByDayMid(Integer mid);

    Map<String, Object> getPointLogsByMid(Integer mid, Integer page);

    List<Map<String, Object>> getWeeklyPointLogs(Integer mid, LocalDateTime startTime, LocalDateTime endTime);

    Map<String, Integer> getStreakInfo(Integer mid, Integer ruleId);

    List<Map<String, Object>> getMonthlyCheckInRecords(Integer mid, Integer ruleId, String yearMonth);

    List<Map<String, Object>> getPointLogsByDateRange(Integer mid, Integer ruleId, String startDay, String endDay);
}
