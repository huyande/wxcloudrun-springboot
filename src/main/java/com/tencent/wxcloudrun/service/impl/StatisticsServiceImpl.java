package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.GameRewardLogMapper;
import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.WishLogMapper;
import com.tencent.wxcloudrun.dao.SeasonPointLogMapper;
import com.tencent.wxcloudrun.dao.SeasonWishLogMapper;
import com.tencent.wxcloudrun.dao.SeasonWishMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleAchievementLogMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleMapper;
import com.tencent.wxcloudrun.dto.WeeklyReportDTO;
import com.tencent.wxcloudrun.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final MemberPointLogsMapper memberPointLogsMapper;
    private final WishLogMapper wishLogMapper;
    private final GameRewardLogMapper gameRewardLogMapper;
    private final SeasonPointLogMapper seasonPointLogMapper;
    private final SeasonWishLogMapper seasonWishLogMapper;
    private final SeasonWishMapper seasonWishMapper;
    private final SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper;
    private final SeasonRuleMapper seasonRuleMapper;

    @Autowired
    public StatisticsServiceImpl(MemberPointLogsMapper memberPointLogsMapper,
                                WishLogMapper wishLogMapper,
                                GameRewardLogMapper gameRewardLogMapper,
                                SeasonPointLogMapper seasonPointLogMapper,
                                SeasonWishLogMapper seasonWishLogMapper,
                                SeasonWishMapper seasonWishMapper,
                                SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper,
                                SeasonRuleMapper seasonRuleMapper) {
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.wishLogMapper = wishLogMapper;
        this.gameRewardLogMapper = gameRewardLogMapper;
        this.seasonPointLogMapper = seasonPointLogMapper;
        this.seasonWishLogMapper = seasonWishLogMapper;
        this.seasonWishMapper = seasonWishMapper;
        this.seasonRuleAchievementLogMapper = seasonRuleAchievementLogMapper;
        this.seasonRuleMapper = seasonRuleMapper;
    }

    /**
     * 按月份统计积分增减情况
     * 
     * @param mid 会员ID
     * @param startDate 开始日期（格式：yyyy-MM-dd）
     * @param endDate 结束日期（格式：yyyy-MM-dd）
     * @param seasonId 赛季ID
     * @return 包含月份统计数据的Map，适用于Echarts柱状图展示
     */
    @Override
    public Map<String, Object> getPointsStatisticsByMonth(Integer mid, String startDate, String endDate, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式下，调用seasonPointLogMapper的等价方法（如无需补充实现）
            List<Map<String, Object>> monthlyStats = seasonPointLogMapper.getPointsStatisticsByMonth(seasonId, mid, startDate, endDate);
            List<String> allMonths = generateCompleteMonthsList(startDate, endDate);
            List<Integer> increaseData = new ArrayList<>();
            List<Integer> decreaseData = new ArrayList<>();
            List<Integer> netGrowthData = new ArrayList<>();
            Map<String, Map<String, Object>> monthStatsMap = new HashMap<>();
            if (monthlyStats != null) {
                for (Map<String, Object> stat : monthlyStats) {
                    String month = (String) stat.get("month");
                    monthStatsMap.put(month, stat);
                }
            }
            for (String month : allMonths) {
                Map<String, Object> stat = monthStatsMap.get(month);
                int increaseValue = 0;
                int decreaseValue = 0;
                if (stat != null) {
                    Object increaseObj = stat.get("increase");
                    Object decreaseObj = stat.get("decrease");
                    if (increaseObj instanceof Number) {
                        increaseValue = ((Number) increaseObj).intValue();
                    }
                    if (decreaseObj instanceof Number) {
                        decreaseValue = ((Number) decreaseObj).intValue();
                    }
                }
                increaseData.add(increaseValue);
                decreaseData.add(decreaseValue);
                int netGrowth = increaseValue - decreaseValue;
                netGrowthData.add(netGrowth);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("months", allMonths);
            result.put("increase", increaseData);
            result.put("decrease", decreaseData);
            result.put("netGrowth", netGrowthData);
            return result;
        } else {
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
     * @param seasonId 赛季ID
     * @return 包含各类统计数据的Map
     */
    @Override
    public Map<String, Object> getComprehensiveStatistics(Integer mid, String startDate, String endDate, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式下，所有统计均调用season相关Mapper，逻辑与原始一致
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> pointsStats = getPointsStatisticsSeason(seasonId, mid, startDate, endDate);
            result.put("pointsStats", pointsStats);
            List<Map<String, Object>> topTasks = getTopTasksSeason(seasonId, mid, startDate, endDate);
            result.put("topTasks", topTasks);
            Map<String, Object> lotteryStats = getLotteryStatisticsSeason(seasonId, mid, startDate, endDate);
            result.put("lotteryStats", lotteryStats);
            Map<String, Object> wishStats = getWishStatisticsSeason(seasonId, mid, startDate, endDate);
            result.put("wishStats", wishStats);
            return result;
        } else {
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
     * @param seasonId 赛季ID
     * @return 包含打卡类型积分占比的Map，适用于Echarts饼图展示
     */
    @Override
    public Map<String, Object> getCheckInTypePointsRatio(Integer mid, String startDate, String endDate, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式下，调用seasonPointLogMapper的等价方法
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> typePointsData = seasonPointLogMapper.getCheckInTypePointsRatio(seasonId, mid, startDate, endDate);
            int totalPoints = 0;
            if (typePointsData != null && !typePointsData.isEmpty()) {
                for (Map<String, Object> data : typePointsData) {
                    Object valueObj = data.get("value");
                    if (valueObj instanceof Number) {
                        totalPoints += ((Number) valueObj).intValue();
                    }
                }
            }
            result.put("total", totalPoints);
            result.put("data", typePointsData != null ? typePointsData : new ArrayList<>());
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> typePointsData = memberPointLogsMapper.getCheckInTypePointsRatio(mid, startDate, endDate);
            int totalPoints = 0;
            if (typePointsData != null && !typePointsData.isEmpty()) {
                for (Map<String, Object> data : typePointsData) {
                    Object valueObj = data.get("value");
                    if (valueObj instanceof Number) {
                        totalPoints += ((Number) valueObj).intValue();
                    }
                }
            }
            result.put("total", totalPoints);
            result.put("data", typePointsData != null ? typePointsData : new ArrayList<>());
            return result;
        }
    }

    /**
     * 获取成就周报
     * 
     * @param mid 会员ID
     * @param reportDate 周报的指定日期
     * @param seasonId 赛季ID
     * @return 成就周报数据
     */
    @Override
    public WeeklyReportDTO getWeeklyReport(Integer mid, LocalDate reportDate, Long seasonId) {
        // 计算周的开始和结束日期（周一到周日）
        LocalDate weekStart = reportDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = reportDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        String startDateStr = weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDateStr = weekEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 计算上周的开始和结束日期用于对比
        LocalDate lastWeekStart = weekStart.minusWeeks(1);
        LocalDate lastWeekEnd = weekEnd.minusWeeks(1);
        String lastWeekStartStr = lastWeekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String lastWeekEndStr = lastWeekEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        WeeklyReportDTO report = new WeeklyReportDTO();
        
        // 设置日期范围
        report.setDateRange(formatDateRange(weekStart, weekEnd));
        
        // 获取概览统计
        WeeklyReportDTO.OverviewStats overview = getOverviewStats(mid, startDateStr, endDateStr, seasonId);
        report.setOverview(overview);
        
        // 获取每日打卡趋势
        List<WeeklyReportDTO.ChartData> dailyTrend = getDailyTrend(mid, weekStart, weekEnd, seasonId);
        report.setDailyTrend(dailyTrend);
        
        // 获取任务类型分布
        List<WeeklyReportDTO.ChartData> taskTypeDistribution = getTaskDistribution(mid, startDateStr, endDateStr, seasonId);
        report.setTaskDistribution(taskTypeDistribution);
        
        // 生成总结文字
        String summaryText = generateSummaryText(overview);
        report.setSummaryText(summaryText);
        
        // 生成高光时刻
        List<String> highlights = generateHighlights(mid, weekStart, weekEnd, overview, seasonId);
        report.setHighlights(highlights);
        
        // 获取对比数据
        WeeklyReportDTO.ComparisonStats comparison = getComparisonStats(mid, startDateStr, endDateStr, lastWeekStartStr, lastWeekEndStr, seasonId);
        report.setComparison(comparison);
        
        return report;
    }

    /**
     * 格式化日期范围
     */
    private String formatDateRange(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月d日");
        return String.format("%s - %s",
                start.format(formatter), 
                end.format(formatter));
    }
    
    /**
     * 获取概览统计数据
     */
    private WeeklyReportDTO.OverviewStats getOverviewStats(Integer mid, String startDate, String endDate, Long seasonId) {
        WeeklyReportDTO.OverviewStats overview = new WeeklyReportDTO.OverviewStats();
        
        if (seasonId != null) {
            // 赛季模式
            // 打卡总次数
            Integer totalCheckIns = seasonPointLogMapper.getCheckInTimesByDateRange(seasonId, mid, startDate, endDate);
            overview.setTotalCheckIns(totalCheckIns != null ? totalCheckIns : 0);
            
            // 获得积分（num > 0的总和）
            Integer pointsEarned = seasonPointLogMapper.getTotalPointsByDateRange(seasonId, mid, startDate, endDate);
            overview.setPointsEarned(pointsEarned != null ? pointsEarned : 0);
            
            // 心愿消费积分
            Integer wishPointsSpent = getSeasonWishPointsSpentByDateRange(seasonId, mid, startDate, endDate);
            overview.setWishPointsSpent(wishPointsSpent != null ? wishPointsSpent : 0);
            
            // 惩罚扣分（需要新的查询方法）
            Integer penaltyPointsLost = getSeasonPenaltyPointsByDateRange(seasonId, mid, startDate, endDate);
            overview.setPenaltyPointsLost(penaltyPointsLost != null ? penaltyPointsLost : 0);
            
            // 活跃天数（需要新的查询方法）
            Integer activeDays = getSeasonActiveDaysByDateRange(seasonId, mid, startDate, endDate);
            overview.setActiveDays(activeDays != null ? activeDays : 0);
            
        } else {
            // 常规模式
            // 打卡总次数
            Integer totalCheckIns = memberPointLogsMapper.getCheckInTimesByDateRange(mid, startDate, endDate);
            overview.setTotalCheckIns(totalCheckIns != null ? totalCheckIns : 0);
            
            // 获得积分（num > 0的总和）
            Integer pointsEarned = memberPointLogsMapper.getTotalPointsByDateRange(mid, startDate, endDate);
            overview.setPointsEarned(pointsEarned != null ? pointsEarned : 0);
            
            // 心愿消费积分
            Integer wishPointsSpent = wishLogMapper.getConsumedPointsByDateRange(mid, startDate, endDate);
            overview.setWishPointsSpent(wishPointsSpent != null ? wishPointsSpent : 0);
            
            // 惩罚扣分（需要新的查询方法）
            Integer penaltyPointsLost = getPenaltyPointsByDateRange(mid, startDate, endDate);
            overview.setPenaltyPointsLost(penaltyPointsLost != null ? penaltyPointsLost : 0);
            
            // 活跃天数（需要新的查询方法）
            Integer activeDays = getActiveDaysByDateRange(mid, startDate, endDate);
            overview.setActiveDays(activeDays != null ? activeDays : 0);
        }
        
        return overview;
    }
    
    /**
     * 获取每日打卡趋势数据
     */
    private List<WeeklyReportDTO.ChartData> getDailyTrend(Integer mid, LocalDate weekStart, LocalDate weekEnd, Long seasonId) {
        List<WeeklyReportDTO.ChartData> dailyTrend = new ArrayList<>();
        String[] dayNames = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = weekStart.plusDays(i);
            String dayStr = currentDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            Integer checkInCount = 0;
            if (seasonId != null) {
                checkInCount = seasonPointLogMapper.getCheckInTimesByDateRange(seasonId, mid, dayStr, dayStr);
            } else {
                checkInCount = memberPointLogsMapper.getCheckInTimesByDateRange(mid, dayStr, dayStr);
            }
            
            dailyTrend.add(new WeeklyReportDTO.ChartData(dayNames[i], checkInCount != null ? checkInCount : 0));
        }
        
        return dailyTrend;
    }
    
    /**
     * 获取任务类型分布数据
     */
    private List<WeeklyReportDTO.ChartData> getTaskDistribution(Integer mid, String startDate, String endDate, Long seasonId) {
        List<WeeklyReportDTO.ChartData> taskTypeDistribution = new ArrayList<>();
        
        List<Map<String, Object>> typeData;
        if (seasonId != null) {
            typeData = seasonPointLogMapper.getCheckInTypePointsRatio(seasonId, mid, startDate, endDate);
        } else {
            typeData = memberPointLogsMapper.getCheckInTypePointsRatio(mid, startDate, endDate);
        }
        
        if (typeData != null) {
            for (Map<String, Object> type : typeData) {
                String typeName = (String) type.get("name");
                Integer count = ((Number) type.get("value")).intValue();
                taskTypeDistribution.add(new WeeklyReportDTO.ChartData(typeName, count));
            }
        }
        
        return taskTypeDistribution;
    }
    
    /**
     * 生成总结文字
     */
    private String generateSummaryText(WeeklyReportDTO.OverviewStats overview) {
        return String.format("本周孩子共打卡%d次，获得积分%d分，消费积分%d分，活跃%d天。", 
                overview.getTotalCheckIns(),
                overview.getPointsEarned(),
                overview.getWishPointsSpent(),
                overview.getActiveDays());
    }
    
    /**
     * 生成高光时刻
     */
    private List<String> generateHighlights(Integer mid, LocalDate weekStart, LocalDate weekEnd, WeeklyReportDTO.OverviewStats overview, Long seasonId) {
        List<String> highlights = new ArrayList<>();
        
        // 连续打卡天数检测
        if (overview.getActiveDays() >= 5) {
            highlights.add("本周活跃天数达到" + overview.getActiveDays() + "天，真棒！");
        }
        
        // 打卡次数里程碑
        if (overview.getTotalCheckIns() >= 20) {
            highlights.add("本周打卡超过20次，坚持就是胜利！");
        } else if (overview.getTotalCheckIns() >= 10) {
            highlights.add("本周打卡超过10次，保持良好习惯！");
        }
        
        // 积分获得里程碑
        if (overview.getPointsEarned() >= 100) {
            highlights.add("本周获得积分超过100分，真是积分小达人！");
        }
        
        // 如果没有特别的成就，至少给一个鼓励
        if (highlights.isEmpty()) {
            highlights.add("每一次打卡都是进步，继续加油！");
        }
        
        return highlights;
    }
    
    /**
     * 获取对比数据
     */
    private WeeklyReportDTO.ComparisonStats getComparisonStats(Integer mid, String thisWeekStart, String thisWeekEnd, 
                                                             String lastWeekStart, String lastWeekEnd, Long seasonId) {
        WeeklyReportDTO.ComparisonStats comparison = new WeeklyReportDTO.ComparisonStats();
        
        // 本周数据
        Integer thisWeekCheckIns, thisWeekPoints;
        // 上周数据
        Integer lastWeekCheckIns, lastWeekPoints;
        
        if (seasonId != null) {
            thisWeekCheckIns = seasonPointLogMapper.getCheckInTimesByDateRange(seasonId, mid, thisWeekStart, thisWeekEnd);
            thisWeekPoints = seasonPointLogMapper.getTotalPointsByDateRange(seasonId, mid, thisWeekStart, thisWeekEnd);
            lastWeekCheckIns = seasonPointLogMapper.getCheckInTimesByDateRange(seasonId, mid, lastWeekStart, lastWeekEnd);
            lastWeekPoints = seasonPointLogMapper.getTotalPointsByDateRange(seasonId, mid, lastWeekStart, lastWeekEnd);
        } else {
            thisWeekCheckIns = memberPointLogsMapper.getCheckInTimesByDateRange(mid, thisWeekStart, thisWeekEnd);
            thisWeekPoints = memberPointLogsMapper.getTotalPointsByDateRange(mid, thisWeekStart, thisWeekEnd);
            lastWeekCheckIns = memberPointLogsMapper.getCheckInTimesByDateRange(mid, lastWeekStart, lastWeekEnd);
            lastWeekPoints = memberPointLogsMapper.getTotalPointsByDateRange(mid, lastWeekStart, lastWeekEnd);
        }
        
        // 计算打卡次数对比
        thisWeekCheckIns = thisWeekCheckIns != null ? thisWeekCheckIns : 0;
        lastWeekCheckIns = lastWeekCheckIns != null ? lastWeekCheckIns : 0;
        int checkInDiff = thisWeekCheckIns - lastWeekCheckIns;
        String checkInTrend;
        if (checkInDiff > 0) {
            comparison.setCheckInsComparison("↑ " + checkInDiff + "次");
            checkInTrend = WeeklyReportDTO.TrendStatus.UP;
        } else if (checkInDiff < 0) {
            comparison.setCheckInsComparison("↓ " + Math.abs(checkInDiff) + "次");
            checkInTrend = WeeklyReportDTO.TrendStatus.DOWN;
        } else {
            comparison.setCheckInsComparison("→ 持平");
            checkInTrend = WeeklyReportDTO.TrendStatus.EQUAL;
        }
        
        // 计算积分对比
        thisWeekPoints = thisWeekPoints != null ? thisWeekPoints : 0;
        lastWeekPoints = lastWeekPoints != null ? lastWeekPoints : 0;
        int pointsDiff = thisWeekPoints - lastWeekPoints;
        String pointsTrend;
        if (pointsDiff > 0) {
            comparison.setPointsEarnedComparison("↑ " + pointsDiff + "分");
            pointsTrend = WeeklyReportDTO.TrendStatus.UP;
        } else if (pointsDiff < 0) {
            comparison.setPointsEarnedComparison("↓ " + Math.abs(pointsDiff) + "分");
            pointsTrend = WeeklyReportDTO.TrendStatus.DOWN;
        } else {
            comparison.setPointsEarnedComparison("→ 持平");
            pointsTrend = WeeklyReportDTO.TrendStatus.EQUAL;
        }
        
        // 计算整体趋势状态
        String overallTrend;
        if (checkInTrend.equals(pointsTrend)) {
            // 两个趋势一致
            overallTrend = checkInTrend;
        } else {
            // 两个趋势不一致，设为混合状态
            overallTrend = WeeklyReportDTO.TrendStatus.MIXED;
        }
        comparison.setTrend(overallTrend);
        
        return comparison;
    }
    
    // 以下方法需要新增到相应的Mapper中，现在先用临时实现
    
    /**
     * 获取赛季模式下指定日期范围内的心愿消费积分
     */
    private Integer getSeasonWishPointsSpentByDateRange(Long seasonId, Integer mid, String startDate, String endDate) {
        return seasonWishLogMapper.getConsumedPointsByDateRange(seasonId, mid, startDate, endDate);
    }
    
    /**
     * 获取赛季模式下指定日期范围内的惩罚扣分
     */
    private Integer getSeasonPenaltyPointsByDateRange(Long seasonId, Integer mid, String startDate, String endDate) {
        return seasonPointLogMapper.getPenaltyPointsByDateRange(seasonId, mid, startDate, endDate);
    }
    
    /**
     * 获取赛季模式下指定日期范围内的活跃天数
     */
    private Integer getSeasonActiveDaysByDateRange(Long seasonId, Integer mid, String startDate, String endDate) {
        return seasonPointLogMapper.getActiveDaysByDateRange(seasonId, mid, startDate, endDate);
    }
    
    /**
     * 获取常规模式下指定日期范围内的惩罚扣分
     */
    private Integer getPenaltyPointsByDateRange(Integer mid, String startDate, String endDate) {
        return memberPointLogsMapper.getPenaltyPointsByDateRange(mid, startDate, endDate);
    }
    
    /**
     * 获取常规模式下指定日期范围内的活跃天数
     */
    private Integer getActiveDaysByDateRange(Integer mid, String startDate, String endDate) {
        return memberPointLogsMapper.getActiveDaysByDateRange(mid, startDate, endDate);
    }

    // 赛季模式下：积分统计
    private Map<String, Object> getPointsStatisticsSeason(Long seasonId, Integer mid, String startDate, String endDate) {
        Map<String, Object> pointsStats = new HashMap<>();
        // 获取积分总数
        Integer totalPoints = seasonPointLogMapper.getTotalPointsByDateRange(seasonId, mid, startDate, endDate);
        pointsStats.put("totalPoints", totalPoints != null ? totalPoints : 0);
        // 获取积分消耗总数
        Integer consumedPoints = seasonWishLogMapper.getTotalPointsBySeasonIdAndMid(seasonId, mid);
        pointsStats.put("consumedPoints", consumedPoints != null ? consumedPoints : 0);
        // 获取打卡天总数
        Integer checkInCount = seasonPointLogMapper.getPointDaysBySeasonIdAndMid(seasonId, mid);
        pointsStats.put("checkInCount", checkInCount != null ? checkInCount : 0);
        // 获取打卡次数总数
        Integer checkInTimes = seasonPointLogMapper.getCheckInTimesByDateRange(seasonId, mid, startDate, endDate);
        pointsStats.put("checkInTimes", checkInTimes != null ? checkInTimes : 0);
        return pointsStats;
    }
    // 赛季模式下：打卡次数前5的任务
    private List<Map<String, Object>> getTopTasksSeason(Long seasonId, Integer mid, String startDate, String endDate) {
        List<Map<String, Object>> topTasks = seasonPointLogMapper.getTopTasksByDateRange(seasonId, mid, startDate, endDate, 5);
        return topTasks != null ? topTasks : new ArrayList<>();
    }
    // 赛季模式下：积分抽奖统计
    private Map<String, Object> getLotteryStatisticsSeason(Long seasonId, Integer mid, String startDate, String endDate) {
        Map<String, Object> lotteryStats = new HashMap<>();
        Integer lotteryCount = seasonPointLogMapper.getLotteryCountByDateRange(seasonId, mid, startDate, endDate);
        lotteryStats.put("lotteryCount", lotteryCount != null ? lotteryCount : 0);
        Integer lotteryPoints = seasonPointLogMapper.getLotteryPointsByDateRange(seasonId, mid, startDate, endDate);
        lotteryStats.put("lotteryPoints", lotteryPoints != null ? lotteryPoints : 0);
        return lotteryStats;
    }
    // 赛季模式下：心愿兑换统计
    private Map<String, Object> getWishStatisticsSeason(Long seasonId, Integer mid, String startDate, String endDate) {
        Map<String, Object> wishStats = new HashMap<>();
        Integer exchangeCount = seasonWishLogMapper.getCountBySeasonIdAndMidAndStatus(seasonId, mid, 1); // 1为已兑换
        wishStats.put("exchangeCount", exchangeCount != null ? exchangeCount : 0);
        List<Map<String, Object>> topWishes = seasonWishLogMapper.getTopWishesByDateRange(seasonId, mid, startDate, endDate, 5);
        wishStats.put("topWishes", topWishes != null ? topWishes : new ArrayList<>());
        return wishStats;
    }
} 