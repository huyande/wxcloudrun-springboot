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
}
