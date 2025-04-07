package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.GameRewardLogMapper;
import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.WishLogMapper;
import com.tencent.wxcloudrun.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final MemberPointLogsMapper memberPointLogsMapper;
    private final WishLogMapper wishLogMapper;
    private final GameRewardLogMapper gameRewardLogMapper;

    @Autowired
    public StatisticsServiceImpl(MemberPointLogsMapper memberPointLogsMapper,
                                WishLogMapper wishLogMapper,
                                GameRewardLogMapper gameRewardLogMapper) {
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.wishLogMapper = wishLogMapper;
        this.gameRewardLogMapper = gameRewardLogMapper;
    }

    /**
     * 按月份统计积分增减情况
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 包含月份统计数据的Map，适用于Echarts柱状图展示
     */
    @Override
    public Map<String, Object> getPointsStatisticsByMonth(Integer mid, String startDate, String endDate) {
        // 直接通过mapper查询按月份统计的积分数据
        List<Map<String, Object>> monthlyStats = memberPointLogsMapper.getPointsStatisticsByMonth(mid, startDate, endDate);
        
        // 生成从开始月份到结束月份的完整月份列表
        List<String> allMonths = generateCompleteMonthsList(startDate, endDate);
        
        // 初始化结果数据结构
        List<Integer> increaseData = new ArrayList<>();
        List<Integer> decreaseData = new ArrayList<>();
        List<Integer> netGrowthData = new ArrayList<>();
        
        // 用于快速查找月份对应的统计数据
        Map<String, Map<String, Object>> monthStatsMap = new HashMap<>();
        if (monthlyStats != null) {
            for (Map<String, Object> stat : monthlyStats) {
                String month = (String) stat.get("month");
                monthStatsMap.put(month, stat);
            }
        }
        
        // 填充每个月份的数据，确保没有数据的月份也有默认值0
        for (String month : allMonths) {
            Map<String, Object> stat = monthStatsMap.get(month);
            int increaseValue = 0;
            int decreaseValue = 0;
            
            if (stat != null) {
                // 该月有数据
                Object increaseObj = stat.get("increase");
                Object decreaseObj = stat.get("decrease");
                
                // 处理不同类型的返回值（可能是Long或Integer）
                if (increaseObj instanceof Number) {
                    increaseValue = ((Number) increaseObj).intValue();
                }
                
                if (decreaseObj instanceof Number) {
                    decreaseValue = ((Number) decreaseObj).intValue();
                }
            }
            
            // 添加到对应数组
            increaseData.add(increaseValue);
            decreaseData.add(decreaseValue);
            
            // 计算并添加净增长值（增加量减去减少量）
            int netGrowth = increaseValue - decreaseValue;
            netGrowthData.add(netGrowth);
        }
        
        // 构建最终结果
        Map<String, Object> result = new HashMap<>();
        result.put("months", allMonths);
        result.put("increase", increaseData);
        result.put("decrease", decreaseData);
        result.put("netGrowth", netGrowthData);
        
        return result;
    }
    
    /**
     * 生成从开始日期到结束日期的完整月份列表
     * 
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 有序的月份列表（格式：yyyy-MM）
     */
    private List<String> generateCompleteMonthsList(String startDate, String endDate) {
        List<String> months = new ArrayList<>();
        
        // 解析日期
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, dateFormatter);
        LocalDate end = LocalDate.parse(endDate, dateFormatter);
        
        // 获取开始和结束的年月
        YearMonth startYearMonth = YearMonth.from(start);
        YearMonth endYearMonth = YearMonth.from(end);
        
        // 月份格式化器
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // 循环添加每个月份
        YearMonth currentYearMonth = startYearMonth;
        while (!currentYearMonth.isAfter(endYearMonth)) {
            months.add(currentYearMonth.format(monthFormatter));
            currentYearMonth = currentYearMonth.plusMonths(1);
        }
        
        return months;
    }

    /**
     * 获取综合统计数据
     * 包括积分统计、任务完成情况、积分抽奖和心愿兑换等数据
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 包含各类统计数据的Map
     */
    @Override
    public Map<String, Object> getComprehensiveStatistics(Integer mid, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 积分统计
        Map<String, Object> pointsStats = getPointsStatistics(mid, startDate, endDate);
        result.put("pointsStats", pointsStats);
        
        // 2. 打卡次数前5的任务
        List<Map<String, Object>> topTasks = getTopTasks(mid, startDate, endDate);
        result.put("topTasks", topTasks);
        
        // 3. 积分抽奖统计
        Map<String, Object> lotteryStats = getLotteryStatistics(mid, startDate, endDate);
        result.put("lotteryStats", lotteryStats);
        
        // 4. 心愿兑换统计
        Map<String, Object> wishStats = getWishStatistics(mid, startDate, endDate);
        result.put("wishStats", wishStats);
        
        return result;
    }
    
    /**
     * 获取积分统计数据
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 积分统计数据
     */
    private Map<String, Object> getPointsStatistics(Integer mid, String startDate, String endDate) {
        Map<String, Object> pointsStats = new HashMap<>();
        
        // 获取积分总数
        Integer totalPoints = memberPointLogsMapper.getTotalPointsByDateRange(mid, startDate, endDate);
        pointsStats.put("totalPoints", totalPoints != null ? totalPoints : 0);
        
        // 获取积分消耗总数
        Integer consumedPoints = wishLogMapper.getConsumedPointsByDateRange(mid, startDate, endDate);
        pointsStats.put("consumedPoints", consumedPoints != null ? consumedPoints : 0);
        
        // 获取打卡天总数
        Integer checkInCount = memberPointLogsMapper.getCheckInCountByDateRange(mid, startDate, endDate);
        pointsStats.put("checkInCount", checkInCount != null ? checkInCount : 0);
        
        //获取打卡次数总数
        Integer checkInTimes = memberPointLogsMapper.getCheckInTimesByDateRange(mid, startDate, endDate);
        pointsStats.put("checkInTimes", checkInTimes != null ? checkInTimes : 0);
        return pointsStats;
    }
    
    /**
     * 获取打卡次数前5的任务
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 任务列表
     */
    private List<Map<String, Object>> getTopTasks(Integer mid, String startDate, String endDate) {
        // 获取打卡次数前5的任务
        List<Map<String, Object>> topTasks = memberPointLogsMapper.getTopTasksByDateRange(mid, startDate, endDate, 5);
        return topTasks != null ? topTasks : new ArrayList<>();
    }
    
    /**
     * 获取积分抽奖统计数据
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 积分抽奖统计数据
     */
    private Map<String, Object> getLotteryStatistics(Integer mid, String startDate, String endDate) {
        Map<String, Object> lotteryStats = new HashMap<>();
        
        // 获取抽奖总次数
        Integer lotteryCount = gameRewardLogMapper.getLotteryCountByDateRange(mid, startDate, endDate);
        lotteryStats.put("lotteryCount", lotteryCount != null ? lotteryCount : 0);
        
        // 获取抽奖获取的积分
        Integer lotteryPoints = memberPointLogsMapper.getLotteryPointsByDateRange(mid, startDate, endDate);
        lotteryStats.put("lotteryPoints", lotteryPoints != null ? lotteryPoints : 0);
        
        return lotteryStats;
    }
    
    /**
     * 获取心愿兑换统计数据
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 心愿兑换统计数据
     */
    private Map<String, Object> getWishStatistics(Integer mid, String startDate, String endDate) {
        Map<String, Object> wishStats = new HashMap<>();
        
        // 获取兑换总次数
        Integer exchangeCount = wishLogMapper.getExchangeCountByDateRange(mid, startDate, endDate);
        wishStats.put("exchangeCount", exchangeCount != null ? exchangeCount : 0);
        
        // 获取兑换排行
        List<Map<String, Object>> topWishes = wishLogMapper.getTopWishesByDateRange(mid, startDate, endDate, 5);
        wishStats.put("topWishes", topWishes != null ? topWishes : new ArrayList<>());
        
        return wishStats;
    }

    /**
     * 获取打卡类型积分占比统计
     * 统计一段时间内不同类型打卡任务获得的积分占比
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @return 包含打卡类型积分占比的Map，适用于Echarts饼图展示
     */
    @Override
    public Map<String, Object> getCheckInTypePointsRatio(Integer mid, String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取按任务类型分组的积分数据
        List<Map<String, Object>> typePointsData = memberPointLogsMapper.getCheckInTypePointsRatio(mid, startDate, endDate);
        
        // 计算总积分
        int totalPoints = 0;
        if (typePointsData != null && !typePointsData.isEmpty()) {
            for (Map<String, Object> data : typePointsData) {
                Object valueObj = data.get("value");
                if (valueObj instanceof Number) {
                    totalPoints += ((Number) valueObj).intValue();
                }
            }
        }
        
        // 构建结果
        result.put("total", totalPoints);
        result.put("data", typePointsData != null ? typePointsData : new ArrayList<>());
        
        return result;
    }
} 