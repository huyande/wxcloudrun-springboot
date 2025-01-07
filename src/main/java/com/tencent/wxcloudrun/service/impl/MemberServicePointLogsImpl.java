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
import java.time.LocalDate;

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

    @Override
    public List<Map<String, Object>> getYearlyHeatmap(Integer mid, Integer ruleId, Integer year) {
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
            
            // 如果有打卡记录，设置value为1，level为1；否则value和level都为0
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

    @Override
    public Integer getCurrentDayPointSumByMid(Integer mid, String date) {
        return memberPointLogsMapper.getCurrentDayPointSumByMid(mid, date);
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
    public List<Map<String, Object>> getPointlogCurrentDayDetail(Integer mid, String day) {
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

    @Override
    public List<Map<String, Object>> getPointLogsByDateRangeTotal(Integer mid, String startDay, String endDay) {
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
