package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.*;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.MemberPointLogsService;
import com.tencent.wxcloudrun.service.RuleAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDate;

@Service
public class MemberServicePointLogsImpl implements MemberPointLogsService {

    //积分日志
    final MemberPointLogsMapper memberPointLogsMapper;
    final MemberRulesMapper memberRulesMapper;
    final WishLogMapper wishLogMapper;
    final MemberMapper memberMapper;
    final RuleAchievementService ruleAchievementService;
    final RuleAchievementLogMapper ruleAchievementLogMapper;
    final SeasonPointLogMapper seasonPointLogMapper;
    final SeasonConfigMapper seasonConfigMapper;
    final SeasonWishLogMapper seasonWishLogMapper;
    final SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper;
    
    //构造函数注入
    public MemberServicePointLogsImpl(@Autowired MemberPointLogsMapper memberPointLogsMapper,
                                      @Autowired MemberRulesMapper memberRulesMapper,
                                      @Autowired WishLogMapper wishLogMapper,
                                      @Autowired MemberMapper memberMapper,
                                      @Autowired RuleAchievementService ruleAchievementService,
                                      @Autowired RuleAchievementLogMapper ruleAchievementLogMapper,
                                      @Autowired SeasonPointLogMapper seasonPointLogMapper,
                                      @Autowired SeasonConfigMapper seasonConfigMapper,
                                      @Autowired SeasonWishLogMapper seasonWishLogMapper,
                                      @Autowired SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper) {
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.memberRulesMapper = memberRulesMapper;
        this.wishLogMapper = wishLogMapper;
        this.memberMapper = memberMapper;
        this.ruleAchievementService = ruleAchievementService;
        this.ruleAchievementLogMapper = ruleAchievementLogMapper;
        this.seasonPointLogMapper = seasonPointLogMapper;
        this.seasonConfigMapper = seasonConfigMapper;
        this.seasonWishLogMapper = seasonWishLogMapper;
        this.seasonRuleAchievementLogMapper = seasonRuleAchievementLogMapper;
    }

    @Override
    public <T> List<T> getLogsByMidAndDate(Integer mid, LocalDateTime startAt, LocalDateTime endAt, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            // 赛季模式
            List<SeasonPointLog> seasonLogs = seasonPointLogMapper.getLogsBySeasonIdMidAndDateRange(seasonId, mid, startAt, endAt);
            if (expectedType.isAssignableFrom(SeasonPointLog.class)) {
                return (List<T>) seasonLogs;
            }
            return new ArrayList<>(); // 类型不匹配返回空列表
        } else {
            // 常规模式
            List<MemberPointLogs> logs = memberPointLogsMapper.getLogsByMidAndDate(mid, startAt, endAt);
            if (expectedType.isAssignableFrom(MemberPointLogs.class)) {
                return (List<T>) logs;
            }
            return new ArrayList<>(); // 类型不匹配返回空列表
        }
    }

    @Override
    public <T> T insert(MemberPointLogsRequest memberPointLogsRequest, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            // 赛季模式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(memberPointLogsRequest.getDay(), formatter);
            
            // 首先检查是否存在记录
            List<SeasonPointLog> existingLogs = seasonPointLogMapper.getLogsBySeasonIdMidAndDateRange(
                seasonId, 
                memberPointLogsRequest.getMid(), 
                dateTime, 
                dateTime.plusDays(1).minusNanos(1)
            );
            
            SeasonPointLog existingLog = null;
            if (existingLogs != null && !existingLogs.isEmpty()) {
                for (SeasonPointLog log : existingLogs) {
                    if (log.getRuleId() != null && log.getRuleId().equals(memberPointLogsRequest.getRuleId().longValue())) {
                        existingLog = log;
                        break;
                    }
                }
            }
            
            if (existingLog != null) {
                // 更新现有记录
                if (memberPointLogsRequest.getNum() == 0) {
                    seasonPointLogMapper.delete(existingLog.getId());
                    if (expectedType.isAssignableFrom(SeasonPointLog.class)) {
                        return (T) existingLog;
                    }
                    return null;
                }
                
                existingLog.setNum(memberPointLogsRequest.getNum());
                existingLog.setUid(memberPointLogsRequest.getUid());
                existingLog.setRemark(memberPointLogsRequest.getRemark());
                seasonPointLogMapper.update(existingLog);
                
                if (expectedType.isAssignableFrom(SeasonPointLog.class)) {
                    return (T) existingLog;
                }
                return null;
            } else {
                // 创建新记录
                if (memberPointLogsRequest.getNum() == 0 && memberPointLogsRequest.getType() == 0) {
                    return null;
                }
                
                SeasonPointLog seasonPointLog = new SeasonPointLog();
                seasonPointLog.setSeasonId(seasonId);
                seasonPointLog.setDay(dateTime);
                seasonPointLog.setMid(memberPointLogsRequest.getMid());
                seasonPointLog.setUid(memberPointLogsRequest.getUid());
                if(memberPointLogsRequest.getRuleId()!=null){
                    seasonPointLog.setRuleId(memberPointLogsRequest.getRuleId().longValue());
                }
                seasonPointLog.setNum(memberPointLogsRequest.getNum());
                seasonPointLog.setType(memberPointLogsRequest.getType());
                seasonPointLog.setRemark(memberPointLogsRequest.getRemark());
                if (memberPointLogsRequest.getPomodoroTime() != null) {
                    seasonPointLog.setPomodoroTime(memberPointLogsRequest.getPomodoroTime());
                }
                
                seasonPointLogMapper.insert(seasonPointLog);
                
                if (expectedType.isAssignableFrom(SeasonPointLog.class)) {
                    return (T) seasonPointLog;
                }
                return null;
            }
        } else {
            // 常规模式
            MemberPointLogs log = memberPointLogsMapper.getLogsByDayMidAndRuleId(memberPointLogsRequest.getDay(), memberPointLogsRequest.getMid(), memberPointLogsRequest.getRuleId());
            if(log!=null){
                if(memberPointLogsRequest.getNum()==0){
                    memberPointLogsMapper.delete(log.getId());
                    if (expectedType.isAssignableFrom(MemberPointLogs.class)) {
                        return (T) log;
                    }
                    return null;
                }
                //更新
                memberPointLogsMapper.updateById(log.getId(), memberPointLogsRequest.getNum(), memberPointLogsRequest.getUid(),memberPointLogsRequest.getRemark(),memberPointLogsRequest.getConditionId());
                if (expectedType.isAssignableFrom(MemberPointLogs.class)) {
                    return (T) log;
                }
                return null;
            }else{
                if(memberPointLogsRequest.getNum()==0 && memberPointLogsRequest.getType()==0){
                    return null;
                }
                MemberPointLogs memberPointLogs = new MemberPointLogs();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(memberPointLogsRequest.getDay(), formatter);

                memberPointLogs.setDay(dateTime);
                memberPointLogs.setMid(memberPointLogsRequest.getMid());
                memberPointLogs.setUid(memberPointLogsRequest.getUid());
                memberPointLogs.setRuleId(memberPointLogsRequest.getRuleId());
                memberPointLogs.setNum(memberPointLogsRequest.getNum());
                memberPointLogs.setType(memberPointLogsRequest.getType());
                memberPointLogs.setRemark(memberPointLogsRequest.getRemark());
                if(memberPointLogsRequest.getPomodoroTime()!=null){
                    memberPointLogs.setPomodoroTime(memberPointLogsRequest.getPomodoroTime());
                }
                memberPointLogsMapper.insertOne(memberPointLogs);

                if (expectedType.isAssignableFrom(MemberPointLogs.class)) {
                    return (T) memberPointLogs;
                }
                return null;
            }
        }
    }

    @Override
    public Map<String, Object> insertAndCheckRule(MemberPointLogsRequest memberPointLogsRequest, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            Map<String, Object> res = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(memberPointLogsRequest.getDay(), formatter);
//
//            // 查找现有记录
//            List<SeasonPointLog> existingLogs = seasonPointLogMapper.getLogsBySeasonIdMidAndDateRange(
//                seasonId,
//                memberPointLogsRequest.getMid(),
//                dateTime,
//                dateTime.plusDays(1).minusNanos(1)
//            );
//
//            SeasonPointLog existingLog = null;
//            if (existingLogs != null && !existingLogs.isEmpty()) {
//                for (SeasonPointLog log : existingLogs) {
//                    if (log.getRuleId() != null && log.getRuleId().equals(memberPointLogsRequest.getRuleId().longValue())) {
//                        existingLog = log;
//                        break;
//                    }
//                }
//            }

            SeasonPointLog seasonLog =  seasonPointLogMapper.getLogsBySeasonIdMidAndDayAndRuleId(memberPointLogsRequest.getDay(),seasonId,memberPointLogsRequest.getMid(),memberPointLogsRequest
                    .getRuleId());
            
            if (seasonLog != null) {
                // 更新现有记录
                res.put("log", seasonLog);
                if (memberPointLogsRequest.getNum() == 0) {
                    seasonPointLogMapper.delete(seasonLog.getId());
                    return res;
                }

                seasonLog.setNum(memberPointLogsRequest.getNum());
                seasonLog.setUid(memberPointLogsRequest.getUid());
                seasonLog.setRemark(memberPointLogsRequest.getRemark());
                if(memberPointLogsRequest.getConditionId()!=null){
                    seasonLog.setConditionId(memberPointLogsRequest.getConditionId());
                }
                seasonPointLogMapper.update(seasonLog);
                return res;
            } else {
                // 创建新记录
                if (memberPointLogsRequest.getNum() == 0 && memberPointLogsRequest.getType() == 0) {
                    return null;
                }
                
                SeasonPointLog seasonPointLog = new SeasonPointLog();
                seasonPointLog.setSeasonId(seasonId);
                seasonPointLog.setDay(dateTime);
                seasonPointLog.setMid(memberPointLogsRequest.getMid());
                seasonPointLog.setUid(memberPointLogsRequest.getUid());
                if(memberPointLogsRequest.getRuleId()!=null){
                    seasonPointLog.setRuleId(memberPointLogsRequest.getRuleId().longValue());
                }
                seasonPointLog.setNum(memberPointLogsRequest.getNum());
                seasonPointLog.setType(memberPointLogsRequest.getType());
                seasonPointLog.setRemark(memberPointLogsRequest.getRemark());
                if (memberPointLogsRequest.getPomodoroTime() != null) {
                    seasonPointLog.setPomodoroTime(memberPointLogsRequest.getPomodoroTime());
                }

                if(memberPointLogsRequest.getConditionId()!=null){
                    seasonPointLog.setConditionId(memberPointLogsRequest.getConditionId());
                }
                
                seasonPointLogMapper.insert(seasonPointLog);
                res.put("log", seasonPointLog);
                
                // 检查成就规则
                // 注意：这里假设RuleAchievementService也已经支持了赛季功能
                // 如果RuleAchievementService还没有支持赛季，这里需要相应调整
                if (memberPointLogsRequest.getRuleId() != null && memberPointLogsRequest.getNum() > 0) {
                    // 调用赛季版本的成就检查逻辑
                    List<SeasonRuleAchievement> rules = ruleAchievementService.checkAchievementRules(
                        memberPointLogsRequest.getRuleId(),
                        memberPointLogsRequest.getMid(),
                        memberPointLogsRequest.getRemark(),
                        seasonId,
                        SeasonRuleAchievement.class
                    );
                    if (rules != null && !rules.isEmpty()) {
                        res.put("achievements", rules);
                    }
                }
                
                return res;
            }
        } else {
            // 常规模式
            Map<String,Object> res = new HashMap<>();
            MemberPointLogs log = memberPointLogsMapper.getLogsByDayMidAndRuleId(memberPointLogsRequest.getDay(), memberPointLogsRequest.getMid(), memberPointLogsRequest.getRuleId());
            if(log!=null){
                res.put("log",log);
                if(memberPointLogsRequest.getNum()==0){
                    memberPointLogsMapper.delete(log.getId());
                    return res;
                }
                //更新
                memberPointLogsMapper.updateById(log.getId(), memberPointLogsRequest.getNum(), memberPointLogsRequest.getUid(),memberPointLogsRequest.getRemark(),memberPointLogsRequest.getConditionId());
                return res;
            }else{
                if(memberPointLogsRequest.getNum()==0 && memberPointLogsRequest.getType()==0){
                    return null;
                }
                MemberPointLogs memberPointLogs = new MemberPointLogs();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(memberPointLogsRequest.getDay(), formatter);

                memberPointLogs.setDay(dateTime);
                memberPointLogs.setMid(memberPointLogsRequest.getMid());
                memberPointLogs.setUid(memberPointLogsRequest.getUid());
                memberPointLogs.setRuleId(memberPointLogsRequest.getRuleId());
                memberPointLogs.setNum(memberPointLogsRequest.getNum());
                memberPointLogs.setType(memberPointLogsRequest.getType());
                memberPointLogs.setRemark(memberPointLogsRequest.getRemark());
                if(memberPointLogsRequest.getPomodoroTime()!=null){
                    memberPointLogs.setPomodoroTime(memberPointLogsRequest.getPomodoroTime());
                }
                if(memberPointLogsRequest.getConditionId()!=null){
                    memberPointLogs.setConditionId(memberPointLogsRequest.getConditionId());
                }
                memberPointLogsMapper.insertOne(memberPointLogs);
                res.put("log",memberPointLogs);
                if(memberPointLogsRequest.getRuleId()!=null && memberPointLogsRequest.getNum()>0){
                    List<RuleAchievement> rules = ruleAchievementService.checkAchievementRules(memberPointLogsRequest.getRuleId(), memberPointLogsRequest.getMid(),memberPointLogsRequest.getRemark(), seasonId, RuleAchievement.class);
                    if(rules!=null && rules.size()!=0){
                        res.put("achievements",rules);
                    }
                }
                return res;
            }
        }
    }

    @Override
    public Integer getPointSumByMid(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return seasonPointLogMapper.getPointSumBySeasonIdAndMid(seasonId, mid);
        } else {
            // 常规模式
            return memberPointLogsMapper.getPointSumByMid(mid);
        }
    }

    @Override
    public Integer getPointDaysByMid(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return seasonPointLogMapper.getPointDaysBySeasonIdAndMid(seasonId, mid);
        } else {
            // 常规模式
            return memberPointLogsMapper.getPointDaysByMid(mid);
        }
    }

    @Override
    public List<Map<String, Object>> getPointLogsByMidAndMonth(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 这里需要确保SeasonPointLogMapper中有相应的方法
            // 如果没有，可能需要添加或调整实现
            List<Map<String, Object>> result = seasonPointLogMapper.getPointsStatisticsByMonth(
                seasonId, 
                mid, 
                null, // 开始日期，这里传null表示不限制开始日期
                null  // 结束日期，这里传null表示不限制结束日期
            );
            return result;
        } else {
            // 常规模式
            List<Map<String, Object>> result = memberPointLogsMapper.getPointLogsByMidAndMonth(mid);
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getPointLogsByMidAndSpecificMonth(Integer mid, String yearMonth, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 构造日期范围
//            String startDate = yearMonth + "-01";
//            // 计算月末日期
//            int year = Integer.parseInt(yearMonth.substring(0, 4));
//            int month = Integer.parseInt(yearMonth.substring(5));
//            int lastDay;
//            switch (month) {
//                case 2:
//                    lastDay = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
//                    break;
//                case 4:
//                case 6:
//                case 9:
//                case 11:
//                    lastDay = 30;
//                    break;
//                default:
//                    lastDay = 31;
//            }
//            String endDate = yearMonth + "-" + lastDay;
//
//            // 调用SeasonPointLogMapper的方法
//            List<Map<String, Object>> result = seasonPointLogMapper.getPointsStatisticsByMonth(
//                seasonId,
//                mid,
//                startDate,
//                endDate
//            );
            List<Map<String, Object>> result = seasonPointLogMapper.getPointLogsByMidAndSpecificMonth(mid, yearMonth,seasonId);
            return result;
        } else {
            // 常规模式
            List<Map<String, Object>> result = memberPointLogsMapper.getPointLogsByMidAndSpecificMonth(mid, yearMonth);
            return result;
        }
    }

    @Override
    public Integer getAllCountLogsByDayMid(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return seasonPointLogMapper.getTotalLogDays(seasonId, mid);
        } else {
            // 常规模式
            return memberPointLogsMapper.getAllCountLogsByDayMid(mid);
        }
    }

    @Override
    public Map<String, Object> getPointLogsByMid(Integer mid, Integer page, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            Map<String, Object> map = new HashMap<>();
            int pageSize = 10;
            if (page == null) {
                page = 1;
            }
            Integer offset = (page - 1) * pageSize;
            
            // 这里需要确保SeasonPointLogMapper中有相应的方法
            List<SeasonPointLog> seasonLogs = seasonPointLogMapper.getLogsByPage(
                seasonId,
                mid,
                offset,
                pageSize
            );
            
            // 将SeasonPointLog对象转换为Map
            List<Map<String, Object>> logs = new ArrayList<>();
            for (SeasonPointLog log : seasonLogs) {
                Map<String, Object> logMap = new HashMap<>();
                logMap.put("id", log.getId());
                logMap.put("day", log.getDay());
                logMap.put("mid", log.getMid());
                logMap.put("uid", log.getUid());
                logMap.put("ruleId", log.getRuleId());
                logMap.put("num", log.getNum());
                logMap.put("type", log.getType());
                logMap.put("remark", log.getRemark());
                logMap.put("seasonId", log.getSeasonId());
                if (log.getPomodoroTime() != null) {
                    logMap.put("pomodoroTime", log.getPomodoroTime());
                }
                logs.add(logMap);
            }
            
            Integer count = seasonPointLogMapper.getCountBySeasonIdAndMid(seasonId, mid);
            map.put("total", count);
            map.put("rows", logs);
            return map;
        } else {
            // 常规模式
            Map<String, Object> map = new HashMap<>();
            int pageSize = 10;
            if (page == null) {
                page = 1;
            }
            Integer offset = (page - 1) * pageSize;
            List<Map<String, Object>> logs = memberPointLogsMapper.getPointLogsByMid(mid, pageSize, offset);
            Integer count = memberPointLogsMapper.getPointLogsByMidCount(mid);
            map.put("total", count);
            map.put("rows", logs);
            return map;
        }
    }

    @Override
    public List<Map<String, Object>> getWeeklyPointLogs(Integer mid, LocalDateTime startTime, LocalDateTime endTime, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return seasonPointLogMapper.getWeeklyPointLogs(seasonId, mid, startTime, endTime);
        } else {
            // 常规模式
            return memberPointLogsMapper.getWeeklyPointLogs(mid, startTime, endTime);
        }
    }

    @Override
    public Map<String, Integer> getStreakInfo(Integer mid, Integer ruleId, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 直接调用SeasonPointLogMapper中的getStreakInfo方法，或自己计算
            Map<String, Integer> streakInfo = seasonPointLogMapper.getStreakInfo(seasonId, mid, ruleId.longValue());
            
            // 如果Mapper中的实现不完整，这里可以补充计算
            if (streakInfo == null) {
                streakInfo = new HashMap<>();
                streakInfo.put("currentStreak", 0);
                streakInfo.put("longestStreak", 0);
            }
            
            // 获取总积分和总记录数
            Map<String, Object> pointSumInfo = seasonPointLogMapper.getPointSumBySeasonIdMidAndRuleId(seasonId, mid, ruleId.longValue());
            Integer totalPoints = pointSumInfo != null ? ((Number) pointSumInfo.get("point_sum")).intValue() : 0;
            Integer totalRecords = seasonPointLogMapper.getPointLogsCountBySeasonIdMidAndRuleId(seasonId, mid, ruleId.longValue());
            
            streakInfo.put("totalPoints", totalPoints);
            streakInfo.put("totalRecords", totalRecords != null ? totalRecords : 0);
            
            return streakInfo;
        } else {
            // 常规模式
            List<MemberPointLogs> records = memberPointLogsMapper.getCheckInRecords(mid, ruleId);
            Map<String, Integer> result = new HashMap<>();
            
            // 如果没有记录，直接返回0
            if (records == null || records.isEmpty()) {
                result.put("currentStreak", 0);
                result.put("longestStreak", 0);
                return result;
            }

            int currentStreak = 0;
            int longestStreak = 0;
            int currentCount = 0;
            LocalDateTime lastDate = null;

            // 计算当前连续天数和最长连续天数
            for (int i = 0; i < records.size(); i++) {
                LocalDateTime currentDate = records.get(i).getDay();
                
                if (lastDate == null) {
                    // 第一条记录
                    currentCount = 1;
                    lastDate = currentDate;
                } else {
                    // 计算两个日期之间的天数差
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(currentDate, lastDate);
                    
                    if (daysBetween == 1) {
                        // 日期连续
                        currentCount++;
                    } else {
                        // 日期不连续，重新开始计数
                        if (currentCount > longestStreak) {
                            longestStreak = currentCount;
                        }
                        currentCount = 1;
                    }
                    lastDate = currentDate;
                }
            }

            // 处理最后一段连续记录
            if (currentCount > longestStreak) {
                longestStreak = currentCount;
            }

            // 当前连续天数就是第一段连续的天数
            currentStreak = records.get(0).getDay().equals(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)) ? 
                currentCount : 0;
            // 计算总积分
            Integer totalPoints = memberPointLogsMapper.getPointSumByMidAndRuleId(mid, ruleId);
            Integer totalRecords = memberPointLogsMapper.getPointLogsCountByMidAndRuleId(mid, ruleId);

            result.put("currentStreak", currentStreak);
            result.put("longestStreak", longestStreak);
            result.put("totalPoints",totalPoints);
            result.put("totalRecords",totalRecords);
            
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getMonthlyCheckInRecords(Integer mid, Integer ruleId, String yearMonth, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 构造日期范围
            String startDate = yearMonth + "-01";
            // 计算月末日期
            int year = Integer.parseInt(yearMonth.substring(0, 4));
            int month = Integer.parseInt(yearMonth.substring(5));
            int lastDay;
            switch (month) {
                case 2:
                    lastDay = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    lastDay = 30;
                    break;
                default:
                    lastDay = 31;
            }
            String endDate = yearMonth + "-" + lastDay;
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDate + " 00:00:00", formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate + " 23:59:59", formatter);
            
            // 调用SeasonPointLogMapper的方法获取记录
            List<SeasonPointLog> seasonRecords = seasonPointLogMapper.getLogsBySeasonIdMidRuleIdAndDateRange(
                seasonId,
                mid,
                ruleId.longValue(),
                startDateTime,
                endDateTime
            );
            
            // 转换结果格式
            List<Map<String, Object>> result = new ArrayList<>();
            if (seasonRecords != null) {
                for (SeasonPointLog record : seasonRecords) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    map.put("num", record.getNum());
                    map.put("remark", record.getRemark());
                    result.add(map);
                }
            }
            
            return result;
        } else {
            // 常规模式
            List<MemberPointLogs> records = memberPointLogsMapper.getMonthlyCheckInRecords(mid, ruleId, yearMonth);
            List<Map<String, Object>> result = new ArrayList<>();
            
            if (records != null) {
                for (MemberPointLogs record : records) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));  // 只返回日期中的天数
                    map.put("num", record.getNum());
                    map.put("remark",record.getRemark());
                    result.add(map);
                }
            }
            
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getPointLogsByDateRange(Integer mid, Integer ruleId, String startDay, String endDay, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDay + " 00:00:00", formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDay + " 23:59:59", formatter);
            
            List<SeasonPointLog> seasonRecords = seasonPointLogMapper.getLogsBySeasonIdMidRuleIdAndDateRange(
                seasonId,
                mid,
                ruleId.longValue(),
                startDateTime,
                endDateTime
            );
            
            List<Map<String, Object>> result = new ArrayList<>();
            if (seasonRecords != null) {
                for (SeasonPointLog record : seasonRecords) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    map.put("num", record.getNum());
                    result.add(map);
                }
            }
            
            return result;
        } else {
            // 常规模式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDay + " 00:00:00", formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDay + " 23:59:59", formatter);
            
            List<MemberPointLogs> records = memberPointLogsMapper.getPointLogsByDateRange(mid, ruleId, startDateTime, endDateTime);
            List<Map<String, Object>> result = new ArrayList<>();
            
            if (records != null) {
                for (MemberPointLogs record : records) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    map.put("num", record.getNum());
                    result.add(map);
                }
            }
            
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getYearlyHeatmap(Integer mid, Integer ruleId, Integer year, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 这里需要针对赛季数据进行热力图生成
            // 由于SeasonPointLogMapper中可能没有直接提供年度热力图的方法，我们可以自己查询和构建
            
            // 构建年度的日期范围
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(year + "-01-01 00:00:00", formatter);
            LocalDateTime endDateTime;
            if (year == LocalDate.now().getYear()) {
                endDateTime = LocalDateTime.now();
            } else {
                endDateTime = LocalDateTime.parse(year + "-12-31 23:59:59", formatter);
            }
            
            // 查询该日期范围内的打卡记录
            List<SeasonPointLog> seasonRecords = seasonPointLogMapper.getLogsBySeasonIdMidRuleIdAndDateRange(
                seasonId,
                mid,
                ruleId.longValue(),
                startDateTime,
                endDateTime
            );
            
            // 将打卡记录转换为Map，方便查找
            Map<String, Integer> checkInMap = new HashMap<>();
            if (seasonRecords != null) {
                for (SeasonPointLog record : seasonRecords) {
                    String dateStr = record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    checkInMap.put(dateStr, 1);
                }
            }
            
            // 生成完整的日期序列
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = year == LocalDate.now().getYear() ? 
                LocalDate.now() : LocalDate.of(year, 12, 31);
            
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                
                // 如果有打卡记录，设置value为1，level为3；否则value和level都为0
                if (checkInMap.containsKey(dateStr)) {
                    item.put("value", 1);
                    item.put("level", 3);
                } else {
                    item.put("value", 0);
                    item.put("level", 0);
                }
                
                result.add(item);
                currentDate = currentDate.plusDays(1);
            }
            
            return result;
        } else {
            // 常规模式
            // 获取打卡记录
            List<Map<String, Object>> records = memberPointLogsMapper.getYearlyHeatmap(mid, ruleId, year);
            
            // 将打卡记录转换为Map，方便查找
            Map<String, Integer> checkInMap = new HashMap<>();
            for (Map<String, Object> record : records) {
                checkInMap.put((String) record.get("check_date"), 1);
            }
            
            // 生成完整的日期序列
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = year == LocalDate.now().getYear() ? 
                LocalDate.now() : LocalDate.of(year, 12, 31);
            
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                
                // 如果有打卡记录，设置value为1，level为3；否则value和level都为0
                if (checkInMap.containsKey(dateStr)) {
                    item.put("value", 1);
                    item.put("level", 3);
                } else {
                    item.put("value", 0);
                    item.put("level", 0);
                }
                
                result.add(item);
                currentDate = currentDate.plusDays(1);
            }
            
            return result;
        }
    }
    
    @Override
    public Integer getCurrentDayPointSumByMid(Integer mid, String date, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return seasonPointLogMapper.getCurrentDayPointSumBySeasonIdAndMid(seasonId, mid, date);
        } else {
            // 常规模式
            return memberPointLogsMapper.getCurrentDayPointSumByMid(mid, date);
        }
    }

    /**
     * 生成鲜艳的HSL颜色并转换为十六进制
     * @param index 颜色索引
     * @return 十六进制颜色代码
     */
    private String generateVibrantColor(int index) {
        // 使用黄金角度来分配色相，确保颜色分布均匀
        double goldenAngle = 137.508; // 黄金角度
        double hue = (index * goldenAngle) % 360;
        // 固定使用高饱和度和亮度，使颜色鲜艳
        double saturation = 0.8; // 80%饱和度
        double lightness = 0.6;  // 60%亮度
        
        // 将HSL转换为RGB
        double c = (1 - Math.abs(2 * lightness - 1)) * saturation;
        double x = c * (1 - Math.abs((hue / 60) % 2 - 1));
        double m = lightness - c/2;
        
        double r, g, b;
        if (hue < 60) {
            r = c; g = x; b = 0;
        } else if (hue < 120) {
            r = x; g = c; b = 0;
        } else if (hue < 180) {
            r = 0; g = c; b = x;
        } else if (hue < 240) {
            r = 0; g = x; b = c;
        } else if (hue < 300) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }
        
        // 转换为0-255范围
        int red = (int) Math.round((r + m) * 255);
        int green = (int) Math.round((g + m) * 255);
        int blue = (int) Math.round((b + m) * 255);
        
        // 转换为十六进制
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    @Override
    public List<Map<String, Object>> getPointlogCurrentDayDetail(Integer mid, String day, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 构建日期范围
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // LocalDateTime startDateTime = LocalDateTime.parse(day, formatter);
            // LocalDateTime endDateTime = LocalDateTime.parse(day + " 23:59:59", formatter);
            
            // // 查询该赛季该日期的积分详情
            // List<SeasonPointLog> seasonRecords = seasonPointLogMapper.getLogsBySeasonIdMidAndDateRange(
            //     seasonId,
            //     mid,
            //     startDateTime,
            //     endDateTime
            // );
            
            // // 转换结果格式
            // List<Map<String, Object>> records = new ArrayList<>();
            // if (seasonRecords != null) {
            //     for (SeasonPointLog record : seasonRecords) {
            //         Map<String, Object> map = new HashMap<>();
            //         map.put("id", record.getId());
            //         map.put("day", record.getDay());
            //         map.put("mid", record.getMid());
            //         map.put("uid", record.getUid());
            //         map.put("ruleId", record.getRuleId());
            //         map.put("num", record.getNum());
            //         map.put("type", record.getType());
            //         map.put("remark", record.getRemark());
            //         records.add(map);
            //     }
            // }

            List<Map<String, Object>> records = seasonPointLogMapper.getPointlogCurrentDayDetail(seasonId, mid, day);

            // 添加颜色样式
            int colorIndex = 0;
            for (Map<String, Object> record : records) {
                Map<String, String> itemStyle = new HashMap<>();
                itemStyle.put("color", generateVibrantColor(colorIndex));
                record.put("itemStyle", itemStyle);
                colorIndex++;
            }
            
            return records;
        } else {
            // 常规模式
            List<Map<String, Object>> records = memberPointLogsMapper.getPointlogCurrentDayDetail(mid, day);
            
            int colorIndex = 0;
            for (Map<String, Object> record : records) {
                Map<String, String> itemStyle = new HashMap<>();
                itemStyle.put("color", generateVibrantColor(colorIndex));
                record.put("itemStyle", itemStyle);
                colorIndex++;
            }
            
            return records;
        }
    }

    @Override
    public List<Map<String, Object>> getPointLogsByDateRangeTotal(Integer mid, String startDay, String endDay, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDay + " 00:00:00", formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDay + " 23:59:59", formatter);
            
            // 查询该赛季该日期范围内的所有打卡记录
            List<SeasonPointLog> seasonRecords = seasonPointLogMapper.getLogsBySeasonIdMidAndDateRange(
                seasonId,
                mid,
                startDateTime,
                endDateTime
            );
            
            // 转换结果格式
            List<Map<String, Object>> result = new ArrayList<>();
            if (seasonRecords != null) {
                for (SeasonPointLog record : seasonRecords) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    map.put("num", record.getNum());
                    result.add(map);
                }
            }
            
            return result;
        } else {
            // 常规模式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startDay + " 00:00:00", formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDay + " 23:59:59", formatter);

            List<MemberPointLogs> records = memberPointLogsMapper.getPointLogsByDateRangeTotal(mid, startDateTime, endDateTime);
            List<Map<String, Object>> result = new ArrayList<>();

            if (records != null) {
                for (MemberPointLogs record : records) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    map.put("num", record.getNum());
                    result.add(map);
                }
            }

            return result;
        }
    }

    @Override
    public Integer getLastPointSum(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 查询赛季总积分
            Integer pointSum = seasonPointLogMapper.getPointSumBySeasonIdAndMid(seasonId, mid);
            
            // 查询赛季愿望消耗积分
            // 这里需要确保有对应的方法或表结构
            // 假设有一个方法查询赛季愿望消耗
            Integer wishPointSum = 0; // 需要实际实现
            
            // 查询赛季成就奖励
            // 假设有一个方法查询赛季成就奖励
            Integer rewardValueSum = 0; // 需要实际实现
            
            // 计算总积分
            int total = (pointSum == null ? 0 : pointSum) - (wishPointSum == null ? 0 : wishPointSum) + (rewardValueSum == null ? 0 : rewardValueSum);
            
            return total;
        } else {
            // 常规模式
            Integer pointSum = memberPointLogsMapper.getPointSumByMid(mid);
            Integer wishPointSum = wishLogMapper.getSumNumByMid(mid);
            Member member = memberMapper.getMemberById(mid);
            Integer rewardValueSum = ruleAchievementLogMapper.getSumRewardValue(mid);
            int  total = (pointSum == null ? 0 : pointSum) - (wishPointSum==null ? 0 :wishPointSum) + (rewardValueSum==null?0:rewardValueSum);
            int memberInitPoint = member.getPointTotal()==null?0:member.getPointTotal();
            return total + memberInitPoint;
        }
    }

    @Override
    public HashMap<String, Integer> getPointInfoByMid(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 查询赛季积分信息
            Integer pointSum = seasonPointLogMapper.getPointSumBySeasonIdAndMid(seasonId, mid);
            
            // 查询赛季愿望消耗积分
            Integer wishPointSum = seasonWishLogMapper.getSumNumBySeasonIdAndMid(seasonId, mid);
            
            
            // 查询赛季成就奖励
            Integer rewardValueSum = seasonRuleAchievementLogMapper.getSumRewardValue(seasonId,mid);

            // 查询赛季初始积分
            SeasonConfig seasonConfig = seasonConfigMapper.getById(seasonId);
            Integer seasonInitPoint = seasonConfig.getInitialPoints()==null?0:seasonConfig.getInitialPoints();
            
            
            // 计算积分总和
            int total = (pointSum == null ? 0 : pointSum) - (wishPointSum == null ? 0 : wishPointSum) + (rewardValueSum == null ? 0 : rewardValueSum) + seasonInitPoint;
            
            // 查询打卡天数
            Integer days = seasonPointLogMapper.getPointDaysBySeasonIdAndMid(seasonId, mid);

            // 查询当前日期的积分
            Integer currentDayPoint = seasonPointLogMapper.getCurrentDayPointSumBySeasonIdAndMid(seasonId, mid, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            HashMap<String, Integer> result = new HashMap<>();
            result.put("pointSum", total);
            result.put("days", days != null ? days : 0);
            result.put("originalPointSum", total); // 赛季模式下没有会员初始积分，因此originalPointSum和pointSum相同
            result.put("currentDayPoint", currentDayPoint);
            return result;
        } else {
            // 常规模式
            Integer pointSum = memberPointLogsMapper.getPointSumByMid(mid);
            Integer wishPointSum = wishLogMapper.getSumNumByMid(mid);
            Member member = memberMapper.getMemberById(mid);
            Integer rewardValueSum = ruleAchievementLogMapper.getSumRewardValue(mid);
            int memberInitPoint = member.getPointTotal()==null?0:member.getPointTotal();
            int  total = (pointSum == null ? 0 : pointSum) - (wishPointSum==null ? 0 :wishPointSum) + (rewardValueSum==null?0:rewardValueSum) + memberInitPoint;
            Integer days = memberPointLogsMapper.getPointDaysByMid(mid);

            // 查询当前日期的积分
            Integer currentDayPoint = memberPointLogsMapper.getCurrentDayPointSumByMid(mid, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            HashMap<String, Integer> result = new HashMap<>();
            result.put("pointSum",total);
            result.put("days", days);
            result.put("originalPointSum", total+member.getPointTotal());//累计的积分，不减去wishSum的积分
            result.put("currentDayPoint", currentDayPoint);
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> getYearlyHeatmapAll(Integer mid, Integer year, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            // 构建年度的日期范围
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(year + "-01-01 00:00:00", formatter);
            LocalDateTime endDateTime;
            if (year == LocalDate.now().getYear()) {
                endDateTime = LocalDateTime.now();
            } else {
                endDateTime = LocalDateTime.parse(year + "-12-31 23:59:59", formatter);
            }
            
            // 查询该赛季该日期范围内的所有打卡记录
            // 注意这里需要查询所有规则的记录，不限制ruleId
            List<SeasonPointLog> seasonRecords = seasonPointLogMapper.getLogsBySeasonIdMidAndDateRange(
                seasonId,
                mid,
                startDateTime,
                endDateTime
            );
            
            // 将打卡记录按日期分组并统计数量
            Map<String, Long> checkInMap = new HashMap<>();
            if (seasonRecords != null) {
                for (SeasonPointLog record : seasonRecords) {
                    String dateStr = record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    Long count = checkInMap.getOrDefault(dateStr, 0L);
                    checkInMap.put(dateStr, count + (record.getNum() != null ? record.getNum() : 0));
                }
            }
            
            // 生成完整的日期序列
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = year == LocalDate.now().getYear() ? 
                LocalDate.now() : LocalDate.of(year, 12, 31);
            
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                
                // 设置积分值和等级
                if (checkInMap.containsKey(dateStr)) {
                    Long value = checkInMap.get(dateStr);
                    item.put("value", value);
                    if (value > 0 && value <= 10) {
                        item.put("level", 1);
                    } else if (value > 10 && value <= 20) {
                        item.put("level", 2);
                    } else {
                        item.put("level", 3);
                    }
                } else {
                    item.put("value", 0);
                    item.put("level", 0);
                }
                
                result.add(item);
                currentDate = currentDate.plusDays(1);
            }
            
            return result;
        } else {
            // 常规模式
            // 获取打卡记录
            List<Map<String, Object>> records = memberPointLogsMapper.getYearlyHeatmapALL(mid, year);

            // 将打卡记录转换为Map，方便查找
            Map<String, Long> checkInMap = new HashMap<>();
            for (Map<String, Object> record : records) {
                checkInMap.put((String) record.get("check_date"), (Long)record.get("value"));
            }

            // 生成完整的日期序列
            List<Map<String, Object>> result = new ArrayList<>();
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = year == LocalDate.now().getYear() ?
                    LocalDate.now() : LocalDate.of(year, 12, 31);

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);

                // 如果有打卡记录，设置value为checkInMap对应的值，并根据值设置level
                if (checkInMap.containsKey(dateStr)) {
                    item.put("value", checkInMap.get(dateStr));
                    if(checkInMap.get(dateStr)>0 && checkInMap.get(dateStr)<=10){
                        item.put("level", 1);
                    }else if(checkInMap.get(dateStr)>10 && checkInMap.get(dateStr)<=20){
                        item.put("level", 2);
                    }else{
                        item.put("level", 3);
                    }
                } else {
                    item.put("value", 0);
                    item.put("level", 0);
                }

                result.add(item);
                currentDate = currentDate.plusDays(1);
            }

            return result;
        }
    }

}
