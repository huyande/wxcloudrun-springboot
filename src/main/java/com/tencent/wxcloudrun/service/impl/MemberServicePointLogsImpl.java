package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.service.MemberPointLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ArrayList;

@Service
public class MemberServicePointLogsImpl implements MemberPointLogsService {

    //积分日志
    final MemberPointLogsMapper memberPointLogsMapper;
    final MemberRulesMapper memberRulesMapper;
    //构造函数注入
    public MemberServicePointLogsImpl(@Autowired MemberPointLogsMapper memberPointLogsMapper,
                                    @Autowired MemberRulesMapper memberRulesMapper) {
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.memberRulesMapper = memberRulesMapper;
    }

    @Override
    public List<MemberPointLogs> getLogsByMidAndDate(Integer mid, LocalDateTime startAt,LocalDateTime endAt) {
        return memberPointLogsMapper.getLogsByMidAndDate(mid, startAt,endAt);
    }

    @Override
    public MemberPointLogs insert(MemberPointLogsRequest memberPointLogsRequest) {
        MemberPointLogs log = memberPointLogsMapper.getLogsByDayMidAndRuleId(memberPointLogsRequest.getDay(), memberPointLogsRequest.getMid(), memberPointLogsRequest.getRuleId());
        if(log!=null){
            if(memberPointLogsRequest.getNum()==0){
                memberPointLogsMapper.delete(log.getId());
                return null;
            }
            //更新
            memberPointLogsMapper.updateById(log.getId(), memberPointLogsRequest.getNum(), memberPointLogsRequest.getUid());
            return log;
        }else{
            MemberPointLogs memberPointLogs = new MemberPointLogs();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(memberPointLogsRequest.getDay(), formatter);

            memberPointLogs.setDay(dateTime);
            memberPointLogs.setMid(memberPointLogsRequest.getMid());
            memberPointLogs.setUid(memberPointLogsRequest.getUid());
            memberPointLogs.setRuleId(memberPointLogsRequest.getRuleId());
            memberPointLogs.setNum(memberPointLogsRequest.getNum());
            memberPointLogs.setType(memberPointLogsRequest.getType());
            memberPointLogsMapper.insertOne(memberPointLogs);

            return memberPointLogs;
        }
    }

    @Override
    public Integer getPointSumByMid(Integer mid) {
        return memberPointLogsMapper.getPointSumByMid(mid);
    }

    @Override
    public Integer getPointDaysByMid(Integer mid) {
        return memberPointLogsMapper.getPointDaysByMid(mid);
    }

    @Override
    public List<Map<String, Object>> getPointLogsByMidAndMonth(Integer mid) {
        List<Map<String, Object>> result = memberPointLogsMapper.getPointLogsByMidAndMonth(mid);
        return result;
    }

    @Override
    public Integer getAllCountLogsByDayMid(Integer mid) {
        return memberPointLogsMapper.getAllCountLogsByDayMid(mid);
    }

    @Override
    public Map<String, Object> getPointLogsByMid(Integer mid, Integer page) {
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

    @Override
    public List<Map<String, Object>> getWeeklyPointLogs(Integer mid, LocalDateTime startTime, LocalDateTime endTime) {
        return memberPointLogsMapper.getWeeklyPointLogs(mid, startTime, endTime);
    }

    @Override
    public Map<String, Integer> getStreakInfo(Integer mid, Integer ruleId) {
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

    @Override
    public List<Map<String, Object>> getMonthlyCheckInRecords(Integer mid, Integer ruleId, String yearMonth) {
        List<MemberPointLogs> records = memberPointLogsMapper.getMonthlyCheckInRecords(mid, ruleId, yearMonth);
        List<Map<String, Object>> result = new ArrayList<>();
        
        if (records != null) {
            for (MemberPointLogs record : records) {
                Map<String, Object> map = new HashMap<>();
                map.put("day", record.getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));  // 只返回日期中的天数
                map.put("num", record.getNum());
                result.add(map);
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getPointLogsByDateRange(Integer mid, Integer ruleId, String startDay, String endDay) {
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
