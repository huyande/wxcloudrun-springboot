package com.tencent.wxcloudrun.controller;

import cn.hutool.json.JSONUtil;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.MemberDto;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("member")
public class MemberController {

    final MemberService memberService;
    final WxuserService wxuserService;
    final MemberRulesService memberRulesService;
    final MemberPointLogsService memberPointLogsService;
    final WishLogService wishLogService;
    final SeasonConfigService seasonConfigService;
    final RuleAchievementService ruleAchievementService;
    final Logger logger;

    public MemberController(MemberService memberService,
                            WxuserService wxuserService,
                            MemberRulesService memberRulesService,
                            MemberPointLogsService memberPointLogsService,
                            WishLogService wishLogService,
                            SeasonConfigService seasonConfigService,
                            RuleAchievementService ruleAchievementService) {
        this.memberService = memberService;
        this.wxuserService = wxuserService;
        this.memberRulesService = memberRulesService;
        this.memberPointLogsService = memberPointLogsService;
        this.wishLogService = wishLogService;
        this.seasonConfigService = seasonConfigService;
        this.ruleAchievementService = ruleAchievementService;
        this.logger =  LoggerFactory.getLogger(MemberController.class);
    }

    @PostMapping("/create")
    public ApiResponse create(@RequestBody MemberRequest memberRequest){
        Member member = memberService.insert(memberRequest);
        return ApiResponse.ok(member);
    }

    @PostMapping("/createRule")
    public ApiResponse createRule(@RequestBody MemberRuleRequest memberRuleRequest, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId){
        if(memberRulesService.canCreateRule(memberRuleRequest.getMid(), seasonId)){
            if (seasonId != null) {
                SeasonRule lastRule = memberRulesService.getLastSortByTypeAndMid(memberRuleRequest.getMid(), memberRuleRequest.getType(), seasonId, SeasonRule.class);
                if(lastRule != null){
                    memberRuleRequest.setSort(lastRule.getSort()+1);
                } else {
                    memberRuleRequest.setSort(0);
                }
                
                SeasonRule seasonRule = memberRulesService.insert(memberRuleRequest, seasonId, SeasonRule.class);
                return ApiResponse.ok(seasonRule);
            } else {
                MemberRules lastRule = memberRulesService.getLastSortByTypeAndMid(memberRuleRequest.getMid(), memberRuleRequest.getType(), seasonId, MemberRules.class);
                if(lastRule != null){
                    memberRuleRequest.setSort(lastRule.getSort()+1);
                } else {
                    memberRuleRequest.setSort(0);
                }
                
                MemberRules memberRules = memberRulesService.insert(memberRuleRequest, seasonId, MemberRules.class);
                return ApiResponse.ok(memberRules);
            }
        } else {
            return ApiResponse.error("规则数量已达上限(50个)，无法创建新规则，如有需要可以联系我");
        }
    }

    /**
     *  批量添加规则
     * @param memberRuleRequests
     * @return
     */
    @PostMapping("/batchAddRules")
    public ApiResponse createRules(@RequestBody List<MemberRuleRequest> memberRuleRequests, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId){
        if (seasonId != null) {
            List<SeasonRule> seasonRules = memberRulesService.insertBatch(memberRuleRequests, seasonId, SeasonRule.class);
            return ApiResponse.ok(seasonRules);
        } else {
            List<MemberRules> memberRules = memberRulesService.insertBatch(memberRuleRequests, seasonId, MemberRules.class);
            return ApiResponse.ok(memberRules);
        }
    }

    /**
     *  查询规则更具id
     * @param id
     * @return
     */
    @GetMapping("/ruleById/{id}")
    public ApiResponse getRuleById(@PathVariable Integer id, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            if (seasonId != null) {
                SeasonRule rule = memberRulesService.getRuleById(id, seasonId, SeasonRule.class);
                if (rule != null) {
                    return ApiResponse.ok(rule);
                } else {
                    return ApiResponse.ok(null);
                }
            } else {
                MemberRules rule = memberRulesService.getRuleById(id, seasonId, MemberRules.class);
                if (rule != null) {
                    return ApiResponse.ok(rule);
                } else {
                    return ApiResponse.ok(null);
                }
            }
        } catch (Exception e) {
            logger.error("获取规则失败", e);
            return ApiResponse.error("获取规则失败");
        }
    }

    /**
     * 查询是否创建了这条规则
     * @param mid
     * @param name
     * @return
     */
    @GetMapping("/rule/{mid}")
    public ApiResponse getRuleByNameAndMid(@PathVariable Integer mid, @RequestParam String name, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            Map<String, Object> ruleMap = new HashMap<>();
            
            if (seasonId != null) {
                SeasonRule rule = memberRulesService.getRuleByNameAndMid(name, mid, seasonId, SeasonRule.class);
                if (rule != null) {
                    ruleMap.put("id", rule.getId());
                    ruleMap.put("name", rule.getName());
                    ruleMap.put("icon", rule.getIcon());
                    ruleMap.put("iconType", rule.getIconType());
                    ruleMap.put("type", rule.getType());
                    return ApiResponse.ok(ruleMap);
                } else {
                    return ApiResponse.ok(null);
                }
            } else {
                MemberRules rule = memberRulesService.getRuleByNameAndMid(name, mid, seasonId, MemberRules.class);
                if (rule != null) {
                    ruleMap.put("id", rule.getId());
                    ruleMap.put("name", rule.getName());
                    ruleMap.put("icon", rule.getIcon());
                    ruleMap.put("iconType", rule.getIconType());
                    ruleMap.put("type", rule.getType());
                    return ApiResponse.ok(ruleMap);
                } else {
                    return ApiResponse.ok(null);
                }
            }
        } catch (Exception e) {
            logger.error("获取规则失败", e);
            return ApiResponse.error("获取规则失败");
        }
    }

    @PutMapping("/updateRule/{id}")
    public ApiResponse updateRuleById(@PathVariable Integer id, @RequestBody MemberRuleRequest memberRuleRequest, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            memberRulesService.updateRuleById(id, memberRuleRequest, seasonId);
            return ApiResponse.ok("更新成功");
        } catch (Exception e) {
            logger.error("更新规则失败", e);
            return ApiResponse.error("更新规则失败");
        }
    }


    @DeleteMapping("/rule/{id}")
    public ApiResponse deleteRuleById(@PathVariable Integer id, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            memberRulesService.delete(id, seasonId);
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除规则失败", e);
            return ApiResponse.error("删除规则失败");
        }
    }

    /**
     *  根据mid获取会员规则列表
     * @param mid
     * @return
     */
    @GetMapping("/rules/{mid}")
    public ApiResponse getRulesByMid(@PathVariable Integer mid, @RequestParam String day, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            // 获取一周的开始和结束时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDate = LocalDateTime.parse(day, formatter);
            LocalDateTime weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime weekEnd = weekStart.plusDays(7).minusSeconds(1);
            
            // 获取一周内的打卡记录
            List<Map<String, Object>> weeklyLogs = memberPointLogsService.getWeeklyPointLogs(mid, weekStart, weekEnd, seasonId);
            
            // 创建规则ID到周打卡积分的映射
            Map<Integer, int[]> ruleWeekScores = new HashMap<>();
            for (Map<String, Object> log : weeklyLogs) {
                Integer ruleId = Integer.parseInt(log.get("ruleId").toString());
                int weekday = Integer.parseInt(log.get("weekday").toString());
                int totalNum = 0;
                if (log.get("totalNum") != null) {
                    // 安全地解析积分值
                    try {
                        totalNum = Integer.parseInt(log.get("totalNum").toString());
                    } catch (NumberFormatException e) {
                        logger.warn("解析规则 {} 在星期 {} 的积分失败. 原始值: {}", ruleId, weekday, log.get("totalNum"));
                        // 解析失败时保持积分为0
                    }
                }

                if (!ruleWeekScores.containsKey(ruleId)) {
                    ruleWeekScores.put(ruleId, new int[7]); // 初始化为长度为7的int数组，所有元素默认为0
                }
                ruleWeekScores.get(ruleId)[weekday] = totalNum; // 存储积分
            }
            
            Map<String, List<Map<String, Object>>> groupedRules;
            Map<String, Integer> typeSortMap;
            
            if (seasonId != null) {
                // 赛季模式处理
                List<SeasonRule> rules = memberRulesService.getRulesByMid(mid, day, seasonId, SeasonRule.class);
                
                groupedRules = rules.stream()
                    .collect(Collectors.groupingBy(SeasonRule::getType,
                        Collectors.mapping(rule -> {
                            Map<String, Object> ruleMap = new HashMap<>();
                            ruleMap.put("id", rule.getId());
                            ruleMap.put("name", rule.getName());
                            ruleMap.put("icon", rule.getIcon());
                            ruleMap.put("iconType", rule.getIconType());
                            ruleMap.put("stars", 0);
                            ruleMap.put("status", 0);
                            ruleMap.put("row_status", rule.getStatus());
                            ruleMap.put("quickScore", rule.getQuickScore());
                            ruleMap.put("content", rule.getContent());
                            ruleMap.put("enablePomodoro", rule.getEnablePomodoro());
                            ruleMap.put("pomodoroTime", rule.getPomodoroTime());
                            ruleMap.put("isTimerRule",rule.getIsTimerRule());
                            if(rule.getCompletionConditions()!=null){
                                ruleMap.put("completionConditions", JSONUtil.parseArray(rule.getCompletionConditions()));
                            }else{
                                ruleMap.put("completionConditions", null);
                            }
                            // 添加周打卡状态和积分
                            int[] weekScores = ruleWeekScores.getOrDefault(rule.getId().intValue(), new int[7]);
                            Map<String, Integer> weekStatusMap = new LinkedHashMap<>();
                            weekStatusMap.put("星期一", weekScores[1]);    // 周一
                            weekStatusMap.put("星期二", weekScores[2]);   // 周二
                            weekStatusMap.put("星期三", weekScores[3]); // 周三
                            weekStatusMap.put("星期四", weekScores[4]);  // 周四
                            weekStatusMap.put("星期五", weekScores[5]);    // 周五
                            weekStatusMap.put("星期六", weekScores[6]);  // 周六
                            weekStatusMap.put("星期日", weekScores[0]);    // 周日
                            ruleMap.put("weekStatus", weekStatusMap);
                            
                            return ruleMap;
                        }, Collectors.toList())));
                        
                // 获取分类排序信息
                typeSortMap = rules.stream()
                    .collect(Collectors.toMap(
                        SeasonRule::getType,
                        SeasonRule::getTypeSort,
                        (existing, replacement) -> existing,
                        HashMap::new
                    ));
            } else {
                // 常规模式处理
                List<MemberRules> rules = memberRulesService.getRulesByMid(mid, day, seasonId, MemberRules.class);
                
                groupedRules = rules.stream()
                    .collect(Collectors.groupingBy(MemberRules::getType,
                        Collectors.mapping(rule -> {
                            Map<String, Object> ruleMap = new HashMap<>();
                            ruleMap.put("id", rule.getId());
                            ruleMap.put("name", rule.getName());
                            ruleMap.put("icon", rule.getIcon());
                            ruleMap.put("iconType", rule.getIconType());
                            ruleMap.put("stars", 0);
                            ruleMap.put("status", 0);
                            ruleMap.put("row_status", rule.getStatus());
                            ruleMap.put("quickScore", rule.getQuickScore());
                            ruleMap.put("content", rule.getContent());
                            ruleMap.put("enablePomodoro", rule.getEnablePomodoro());
                            ruleMap.put("pomodoroTime", rule.getPomodoroTime());
                            ruleMap.put("isTimerRule",rule.getIsTimerRule());
                            if(rule.getCompletionConditions()!=null){
                                ruleMap.put("completionConditions", JSONUtil.parseArray(rule.getCompletionConditions()));
                            }else{
                                ruleMap.put("completionConditions", null);
                            }
                            // 添加周打卡状态和积分
                            int[] weekScores = ruleWeekScores.getOrDefault(rule.getId(), new int[7]);
                            Map<String, Integer> weekStatusMap = new LinkedHashMap<>();
                            weekStatusMap.put("星期一", weekScores[1]);    // 周一
                            weekStatusMap.put("星期二", weekScores[2]);   // 周二
                            weekStatusMap.put("星期三", weekScores[3]); // 周三
                            weekStatusMap.put("星期四", weekScores[4]);  // 周四
                            weekStatusMap.put("星期五", weekScores[5]);    // 周五
                            weekStatusMap.put("星期六", weekScores[6]);  // 周六
                            weekStatusMap.put("星期日", weekScores[0]);    // 周日
                            ruleMap.put("weekStatus", weekStatusMap);
                            
                            return ruleMap;
                        }, Collectors.toList())));
                        
                // 获取分类排序信息
                typeSortMap = rules.stream()
                    .collect(Collectors.toMap(
                        MemberRules::getType,
                        MemberRules::getTypeSort,
                        (existing, replacement) -> existing,
                        HashMap::new
                    ));
            }

            // 构建最终的输出格式
            List<Map<String, Object>> formattedRules = groupedRules.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> typeGroup = new HashMap<>();
                    typeGroup.put("type", entry.getKey());
                    typeGroup.put("items", entry.getValue());
                    return typeGroup;
                })
                .collect(Collectors.toList());

            // 对分类进行排序
            formattedRules.sort((a, b) -> {
                String typeA = (String) a.get("type");
                String typeB = (String) b.get("type");
                int sortA = typeSortMap.getOrDefault(typeA, Integer.MAX_VALUE);
                int sortB = typeSortMap.getOrDefault(typeB, Integer.MAX_VALUE);
                return Integer.compare(sortA, sortB);
            });    

            // 返回格式化后的规则
            return ApiResponse.ok(formattedRules);
        } catch (Exception e) {
            logger.error("获取会员规则和积分日志失败", e);
            return ApiResponse.error("获取会员规则和积分日志失败");
        }
    }

    @GetMapping("/pointlogs/{mid}")
    public ApiResponse getPointLogsByMid(@PathVariable Integer mid, @RequestParam String date, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = dateTime.toLocalDate().atTime(23, 59, 59);
            if(seasonId!=null){ 
                List<SeasonPointLog> logs = memberPointLogsService.getLogsByMidAndDate(mid, startOfDay,endOfDay, seasonId, SeasonPointLog.class);
                return ApiResponse.ok(logs);
            }else{
                List<MemberPointLogs> logs = memberPointLogsService.getLogsByMidAndDate(mid, startOfDay,endOfDay, seasonId, MemberPointLogs.class);
return ApiResponse.ok(logs);
            }
            
//            HashMap<String, Object> result = new HashMap<>();
//            result.put("logs", logs);
            
            
        } catch (Exception e) {
            logger.error("获取会员积分日志失败", e);
            return ApiResponse.error("获取会员积分日志失败");
        }
    }


    @PostMapping("/pointlogs/{mid}")
    public ApiResponse addPointLog(@PathVariable Integer mid, @RequestBody MemberPointLogsRequest memberPointLogs, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            memberPointLogs.setMid(mid);
            if(memberPointLogs.getType() ==null){//设置积分类型是打卡
                memberPointLogs.setType(0);
            }
            if(memberPointLogs.getStatus()==null){
                memberPointLogs.setStatus(0);
            }
            //如果是赛季模式，则需要判断赛季是否开始和结束
            if(seasonId!=null){
                SeasonConfig seasonConfig = seasonConfigService.getById(seasonId);
                if(seasonConfig.getEndTime()!=null && seasonConfig.getEndTime().isBefore(LocalDateTime.now())){
                    return ApiResponse.error("赛季已结束,请等待下一个赛季");
                }
                if(seasonConfig.getStartTime()!=null && seasonConfig.getStartTime().isAfter(LocalDateTime.now())){
                    return ApiResponse.error("赛季未开始,请等待赛季开始");
                }
            }
            Map<String, Object> map = memberPointLogsService.insertAndCheckRule(memberPointLogs, seasonId);
            if(map!=null && !map.isEmpty()){
                List<RuleAchievement> achievements = null;
                if(map.containsKey("achievements")){
                    achievements = (List<RuleAchievement>) map.get("achievements");
                }
                return ApiResponse.ok(achievements);
            }
            return ApiResponse.ok();
        } catch (Exception e) {
            logger.error("添加会员积分日志失败", e);
            return ApiResponse.error("添加积分失败");
        }
    }

    // 根据mid获取统计用户的积分当日的积分请求
    @GetMapping("/pointCurrentDaySum/{mid}")
    public ApiResponse getPointCurrentDaySumByMid(@PathVariable Integer mid, @RequestParam String day, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
//            Member member = memberService.getMemberById(mid);
//            Integer pointSum = memberPointLogsService.getPointSumByMid(mid);
            Integer pointCurrentSum = memberPointLogsService.getCurrentDayPointSumByMid(mid,day, seasonId);
            Integer wishSum =  wishLogService.getSumNumByMid(mid, seasonId);
            memberPointLogsService.getLastPointSum(mid, seasonId);
            HashMap<String, Object> result = new HashMap<>();
//            int  total = (pointSum == null ? 0 : pointSum) - (wishSum==null ? 0 :wishSum);
            Integer lastPointSum = memberPointLogsService.getLastPointSum(mid, seasonId);//计算剩余积分
            result.put("pointSum", lastPointSum);//剩余积分
            result.put("wishSum", wishSum);//消耗的积分
            result.put("pointCurrentSum", pointCurrentSum);//当日积分
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("获取会员积分总和失败", e);
            return ApiResponse.error("获取会员积分总和失败");
        }
    }


    @GetMapping("/pointsum/{mid}")
    public ApiResponse getPointSumByMid(@PathVariable Integer mid, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
//            Member member = memberService.getMemberById(mid);
//            Integer pointSum = memberPointLogsService.getPointSumByMid(mid);
//            Integer days = memberPointLogsService.getPointDaysByMid(mid);
//            Integer wishSum =  wishLogService.getSumNumByMid(mid);
//            HashMap<String, Object> result = new HashMap<>();
//            int  total = (pointSum == null ? 0 : pointSum) - (wishSum==null ? 0 :wishSum);
//            result.put("pointSum", total+member.getPointTotal());
//            result.put("days", days);
//            result.put("originalPointSum", (pointSum == null ? 0 : pointSum)+member.getPointTotal());//累计的积分，不减去wishSum的积分
            HashMap<String, Integer> result = memberPointLogsService.getPointInfoByMid(mid, seasonId);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("获取会员积分总和失败", e);
            return ApiResponse.error("获取会员积分总和失败");
        }
    }

    @GetMapping("/pointlogMonthDay/{mid}")
    public ApiResponse getPointMonthDay(@PathVariable Integer mid, @RequestParam(required = false) String yearMonth, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> logs;
            if (yearMonth != null && !yearMonth.isEmpty()) {
                logs = memberPointLogsService.getPointLogsByMidAndSpecificMonth(mid, yearMonth, seasonId);
            } else {
                logs = memberPointLogsService.getPointLogsByMidAndMonth(mid, seasonId);
            }
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取会员积分详情失败", e);
            return ApiResponse.error("获取会员积分详情失败");
        }
    }

    // 根据mid获取member
    @GetMapping("/logCount/{mid}")
    public ApiResponse getMemberLogCountByMid(@PathVariable Integer mid, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            Integer count = memberPointLogsService.getAllCountLogsByDayMid(mid, seasonId);
            return ApiResponse.ok(count);
        } catch (Exception e) {
            logger.error("获取会员信息失败", e);
            return ApiResponse.error("获取会员信息失败");
        }
    }

    /**
     * 更新规则的排序值
     * @param id 规则ID
     * @return
     */
    @PutMapping("/rule/updateSort/{id}")
    public ApiResponse updateRuleSort(@PathVariable Integer id, @RequestBody MemberRuleRequest memberRuleRequest, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            if (seasonId != null) {
                SeasonRule rule = memberRulesService.getRuleById(id, seasonId, SeasonRule.class);
                if (rule == null) {
                    return ApiResponse.error("规则不存在");
                }
                
                // 由于updateRule方法需要MemberRules类型参数，我们需要创建一个临时的MemberRules对象
                MemberRules tempRule = new MemberRules();
                tempRule.setId(rule.getId().intValue());
                tempRule.setSort(memberRuleRequest.getSort());
                // 设置其他必要字段
                tempRule.setName(rule.getName());
                tempRule.setType(rule.getType());
                tempRule.setMid(rule.getMId());
                tempRule.setIcon(rule.getIcon());
                tempRule.setIconType(rule.getIconType());
                tempRule.setWeeks(rule.getWeeks());
                tempRule.setContent(rule.getContent());
                tempRule.setQuickScore(rule.getQuickScore());
                tempRule.setStatus(rule.getStatus());
                tempRule.setTypeSort(rule.getTypeSort());
                tempRule.setEnablePomodoro(rule.getEnablePomodoro());
                tempRule.setPomodoroTime(rule.getPomodoroTime());
                
                memberRulesService.updateRule(tempRule, seasonId);
                
                // 重新获取更新后的规则
                rule = memberRulesService.getRuleById(id, seasonId, SeasonRule.class);
                return ApiResponse.ok(rule);
            } else {
                MemberRules rule = memberRulesService.getRuleById(id, seasonId, MemberRules.class);
                if (rule == null) {
                    return ApiResponse.error("规则不存在");
                }
                
                rule.setSort(memberRuleRequest.getSort());
                memberRulesService.updateRule(rule, seasonId);
                return ApiResponse.ok(rule);
            }
        } catch (Exception e) {
            logger.error("更新规则排序失败", e);
            return ApiResponse.error("更新规则排序失败");
        }
    }

    /**
     * 交换两个规则的排序值
     * @param currentId 当前规则ID
     * @param targetId 目标规则ID
     * @return
     */
    @PutMapping("/rule/swapSort/{currentId}/{targetId}")
    public ApiResponse swapRuleSort(@PathVariable Integer currentId, @PathVariable Integer targetId, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            memberRulesService.swapRuleSort(currentId, targetId, seasonId);
            return ApiResponse.ok("交换排序成功");
        } catch (Exception e) {
            logger.error("交换规则排序失败", e);
            return ApiResponse.error("交换规则排序失败: " + e.getMessage());
        }
    }

    /**
     * 交换两个分类的排序值
     * @param mid 会员ID
     * @param currentType 当前分类名称
     * @param targetType 目标分类名称
     * @return
     */
    @PutMapping("/rules/{mid}/swapTypeSort/{currentType}/{targetType}")
    public ApiResponse swapTypeSort(
            @PathVariable Integer mid,
            @PathVariable String currentType, 
            @PathVariable String targetType, 
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            memberRulesService.swapTypeSort(mid, currentType, targetType, seasonId);
            return ApiResponse.ok("交换分类排序成功");
        } catch (Exception e) {
            logger.error("交换分类排序失败", e);
            return ApiResponse.error("交换分类排序失败: " + e.getMessage());
        }
    }

    /**
     * 根据mid获取所有status=1的规则列表
     * @param mid
     * @return
     */
    @GetMapping("/rules/active/{mid}")
    public ApiResponse getActiveRulesByMid(@PathVariable Integer mid, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            Map<String, List<Map<String, Object>>> groupedRules;
            Map<String, Integer> typeSortMap;
            
            if (seasonId != null) {
                // 赛季模式处理
                List<SeasonRule> rules = memberRulesService.getActiveRulesByMid(mid, seasonId, SeasonRule.class);
                
                groupedRules = rules.stream()
                    .collect(Collectors.groupingBy(SeasonRule::getType,
                        Collectors.mapping(rule -> {
                            Map<String, Object> ruleMap = new HashMap<>();
                            ruleMap.put("id", rule.getId());
                            ruleMap.put("name", rule.getName());
                            ruleMap.put("icon", rule.getIcon());
                            ruleMap.put("iconType", rule.getIconType());
                            ruleMap.put("stars", 0);
                            ruleMap.put("status", 0);
                            return ruleMap;
                        }, Collectors.toList())));
                            
                // 获取分类排序信息
                typeSortMap = rules.stream()
                    .collect(Collectors.toMap(
                        SeasonRule::getType,
                        SeasonRule::getTypeSort,
                        (existing, replacement) -> existing,
                        HashMap::new
                    ));
            } else {
                // 常规模式处理
                List<MemberRules> rules = memberRulesService.getActiveRulesByMid(mid, seasonId, MemberRules.class);
                
                groupedRules = rules.stream()
                    .collect(Collectors.groupingBy(MemberRules::getType,
                        Collectors.mapping(rule -> {
                            Map<String, Object> ruleMap = new HashMap<>();
                            ruleMap.put("id", rule.getId());
                            ruleMap.put("name", rule.getName());
                            ruleMap.put("icon", rule.getIcon());
                            ruleMap.put("iconType", rule.getIconType());
                            ruleMap.put("stars", 0);
                            ruleMap.put("status", 0);
                            return ruleMap;
                        }, Collectors.toList())));
                            
                // 获取分类排序信息
                typeSortMap = rules.stream()
                    .collect(Collectors.toMap(
                        MemberRules::getType,
                        MemberRules::getTypeSort,
                        (existing, replacement) -> existing,
                        HashMap::new
                    ));
            }

            // 构建最终的输出格式
            List<Map<String, Object>> formattedRules = groupedRules.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> typeGroup = new HashMap<>();
                    typeGroup.put("type", entry.getKey());
                    typeGroup.put("items", entry.getValue());
                    return typeGroup;
                })
                .collect(Collectors.toList());

            // 对分类进行排序
            formattedRules.sort((a, b) -> {
                String typeA = (String) a.get("type");
                String typeB = (String) b.get("type");
                int sortA = typeSortMap.getOrDefault(typeA, Integer.MAX_VALUE);
                int sortB = typeSortMap.getOrDefault(typeB, Integer.MAX_VALUE);
                return Integer.compare(sortA, sortB);
            });    

            return ApiResponse.ok(formattedRules);
        } catch (Exception e) {
            logger.error("获取会员活动规则失败", e);
            return ApiResponse.error("获取会员活动规则失败");
        }
    }

    /**
     * 根据id获取member信息
     * @param id member的id
     * @return member信息
     */
    @GetMapping("/{id}")
    public ApiResponse getMemberById(@PathVariable Integer id) {
        try {
            Member member = memberService.getMemberById(id);
            if (member != null) {
                return ApiResponse.ok(member);
            } else {
                return ApiResponse.error("未找到该成员");
            }
        } catch (Exception e) {
            logger.error("获取成员信息失败", e);
            return ApiResponse.error("获取成员信息失败");
        }
    }

    /**
     * 更新member信息
     * @param id member的id
     * @param memberRequest 更新的member信息，可以包含name、gender、avatar等字段
     * @return 更新后的member信息
     */
    @PutMapping("/{id}")
    public ApiResponse updateMember(@PathVariable Integer id, @RequestBody MemberRequest memberRequest) {
        try {
            // 参数校验
//            if (memberRequest.getName() == null || memberRequest.getName().trim().isEmpty()) {
//                return ApiResponse.error("名称不能为空");
//            }
//            if (memberRequest.getGender() == null) {
//                return ApiResponse.error("性别不能为空");
//            }
//            if (memberRequest.getPointTotalCount() == null) {
//                return ApiResponse.error("积分不能为空");
//            }
            
            Member updatedMember = memberService.updateMember(id, memberRequest);
            return ApiResponse.ok(updatedMember);
        } catch (Exception e) {
            logger.error("更新成员信息失败", e);
            return ApiResponse.error("更新成员信息失败: " + e.getMessage());
        }
    }

    /**
     * 清除成员相关数据
     * @param id member的id
     * @return 清除结果
     */
    @DeleteMapping("/clearData/{id}")
    public ApiResponse clearMemberData(@PathVariable Integer id) {
        try {
            memberService.clearMemberData(id);
            seasonConfigService.clearMemberSeason(id);
            return ApiResponse.ok("数据清除成功");
        } catch (Exception e) {
            logger.error("清除成员数据失败", e);
            return ApiResponse.error("清除成员数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除成员相关数据
     * @param id member的id
     * @return 清除结果
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteMemberData(@PathVariable Integer id) {
        try {
            memberService.deleteMemberById(id);
            return ApiResponse.ok("数据清除成功");
        } catch (Exception e) {
            logger.error("清除成员数据失败", e);
            return ApiResponse.error("清除成员数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/pointlogHistory/{mid}/{page}")
    public ApiResponse getPointLogsByMid(@PathVariable Integer mid, @PathVariable Integer page, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            Map<String, Object> data  = memberPointLogsService.getPointLogsByMid(mid,page, seasonId);
            return ApiResponse.ok(data);
        } catch (Exception e) {
            logger.error("获取会员积分详情失败", e);
            return ApiResponse.error("获取会员积分详情失败");
        }
    }

    /**
     * 获取规则的连续打卡天数信息
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @return 连续打卡天数信息
     */
    @GetMapping("/streak/{mid}/{ruleId}")
    public ApiResponse getStreakInfo(@PathVariable Integer mid, @PathVariable Integer ruleId, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            Map<String, Integer> streakInfo = memberPointLogsService.getStreakInfo(mid, ruleId, seasonId);
            return ApiResponse.ok(streakInfo);
        } catch (Exception e) {
            logger.error("获取连续打卡天数失败", e);
            return ApiResponse.error("获取连续打卡天数失败");
        }
    }

    /**
     * 获取指定月的打卡记录
     * 特定规则的打卡记录
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param yearMonth 年月(格式：yyyy-MM)
     * @return 打卡记录列表
     */
    @GetMapping("/checkinLog/{mid}/{ruleId}")
    public ApiResponse getMonthlyCheckInRecords(
            @PathVariable Integer mid,
            @PathVariable Integer ruleId,
            @RequestParam String yearMonth,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> records = memberPointLogsService.getMonthlyCheckInRecords(mid, ruleId, yearMonth, seasonId);
            return ApiResponse.ok(records);
        } catch (Exception e) {
            logger.error("获取月度打卡记录失败", e);
            return ApiResponse.error("获取月度打卡记录失败");
        }
    }

    /**
     * 获取指定时间段内的打卡记录
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param startDay 开始日期(格式：yyyy-MM-dd)
     * @param endDay 结束日期(格式：yyyy-MM-dd)
     * @return 打卡记录列表
     */
    @GetMapping("/checkinLog/{mid}/{ruleId}/range")
    public ApiResponse getPointLogsByDateRange(
            @PathVariable Integer mid,
            @PathVariable Integer ruleId,
            @RequestParam String startDay,
            @RequestParam String endDay,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> records = memberPointLogsService.getPointLogsByDateRange(mid, ruleId, startDay, endDay, seasonId);
            return ApiResponse.ok(records);
        } catch (Exception e) {
            logger.error("获取时间段内打卡记录失败", e);
            return ApiResponse.error("获取时间段内打卡记录失败");
        }
    }

    /**
     * 获取指定时间段内的打卡记录 按日统计当日所以的打卡规则的总和值
     * @param mid 会员ID
     * @param startDay 开始日期(格式：yyyy-MM-dd)
     * @param endDay 结束日期(格式：yyyy-MM-dd)
     * @return 打卡记录列表
     */
    @GetMapping("/checkinLog/{mid}/rangeTotal")
    public ApiResponse getPointLogsByDateRangeTotal(
            @PathVariable Integer mid,
            @RequestParam String startDay,
            @RequestParam String endDay,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> records = memberPointLogsService.getPointLogsByDateRangeTotal(mid, startDay, endDay, seasonId);
            return ApiResponse.ok(records);
        } catch (Exception e) {
            logger.error("获取时间段内打卡记录失败", e);
            return ApiResponse.error("获取时间段内打卡记录失败");
        }
    }

    /**
     * 获取规则的年度打卡热力图数据
     * @param mid 会员ID
     * @param ruleId 规则ID
     * @param year 年份
     * @return 热力图数据
     */
    @GetMapping("/heatmap/{mid}/{ruleId}")
    public ApiResponse getYearlyHeatmap(
            @PathVariable Integer mid,
            @PathVariable Integer ruleId,
            @RequestParam(required = false) Integer year,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            // 如果未指定年份，使用当前年份
            if (year == null) {
                year = LocalDateTime.now().getYear();
            }
            List<Map<String, Object>> yearlyHeatmap = memberPointLogsService.getYearlyHeatmap(mid, ruleId, year, seasonId);
            return ApiResponse.ok(yearlyHeatmap);
        } catch (Exception e) {
            logger.error("获取热力图数据失败", e);
            return ApiResponse.error("获取热力图数据失败");
        }
    }

    /**
     * 获取规则的年度打卡热力图数据
     * @param mid 会员ID
     * @param year 年份
     * @return 热力图数据
     */
    @GetMapping("/heatmapAll/{mid}")
    public ApiResponse getYearlyHeatmapALL(
            @PathVariable Integer mid,
            @RequestParam(required = false) Integer year,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            // 如果未指定年份，使用当前年份
            if (year == null) {
                year = LocalDateTime.now().getYear();
            }
            List<Map<String, Object>> yearlyHeatmap = memberPointLogsService.getYearlyHeatmapAll(mid, year, seasonId);
            return ApiResponse.ok(yearlyHeatmap);
        } catch (Exception e) {
            logger.error("获取热力图数据失败", e);
            return ApiResponse.error("获取热力图数据失败");
        }
    }

    /**
     * 获取指定日期的积分详情
     * @param mid 会员ID
     * @param day 日期（格式：yyyy-MM-dd HH:mm:ss）
     * @return 积分详情列表
     */
    @GetMapping("/pointlogCurrentDayDetail/{mid}")
    public ApiResponse getPointlogCurrentDayDetail(@PathVariable Integer mid, @RequestParam String day, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> details = memberPointLogsService.getPointlogCurrentDayDetail(mid, day, seasonId);
            return ApiResponse.ok(details);
        } catch (Exception e) {
            logger.error("获取积分详情失败", e);
            return ApiResponse.error("获取积分详情失败");
        }
    }

    /**
     * 批量更新规则分类名称
     * @param mid 会员ID
     * @param requestMap 包含oldType和newType的请求体
     * @return 更新结果
     */
    @PutMapping("/rules/{mid}/updateType")
    public ApiResponse updateRuleType(
            @PathVariable Integer mid,
            @RequestBody Map<String, String> requestMap,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            String oldType = requestMap.get("oldType");
            String newType = requestMap.get("newType");
            int updatedCount = memberRulesService.updateRuleType(mid, oldType, newType, seasonId);
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return ApiResponse.ok(result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            logger.error("更新规则分类失败", e);
            return ApiResponse.error("更新规则分类失败: " + e.getMessage());
        }
    }

    /**
     * 获取会员的所有规则分类
     * @param mid 会员ID
     * @return 分类列表
     */
    @GetMapping("/rules/{mid}/types")
    public ApiResponse getRuleTypes(@PathVariable Integer mid, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> types = memberRulesService.getRuleTypes(mid, seasonId);
            return ApiResponse.ok(types);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            logger.error("获取规则分类失败", e);
            return ApiResponse.error("获取规则分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 专门用于更新成员头像
     * @param id 成员ID
     * @param request 包含avatar字段的请求体
     * @return 更新结果
     */
    @PutMapping("/{id}/avatar")
    public ApiResponse updateMemberAvatar(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String avatar = request.get("avatar");
            if (avatar == null || avatar.trim().isEmpty()) {
                return ApiResponse.error("头像URL不能为空");
            }
            
            MemberRequest memberRequest = new MemberRequest();
            memberRequest.setAvatar(avatar);
            
            Member updatedMember = memberService.updateMember(id, memberRequest);
            return ApiResponse.ok(updatedMember);
        } catch (Exception e) {
            logger.error("更新成员头像失败", e);
            return ApiResponse.error("更新成员头像失败: " + e.getMessage());
        }
    }

    // 根据uid获取memberlist
    @GetMapping("/memberlist/{uid}")
    public ApiResponse getMemberListByUid(@PathVariable Integer uid) {
        try {
            WxUser wxUser = wxuserService.getUserById(uid);
            List<MemberDto> memberDtos = new ArrayList<>();
            List<Member> members = memberService.getMembersByUid(uid);
            if(wxUser.getRole()!=null && wxUser.getRole()==7){//表示角色时 孩子自己
//                List<Member> filteredMembers = new ArrayList<>();
                for(Member member : members) {
                    MemberDto memberDto = new MemberDto();
                    //将member的属性赋值给memberDto
                    BeanUtils.copyProperties(member, memberDto);

                    if(member.getMode()!=null && member.getMode().equals("seasonMode")){
                        SeasonConfig seasonConfig = seasonConfigService.getById(member.getCurrentSeasonId());
                        memberDto.setSeasonConfig(seasonConfig);
                    }
                    if(member.getCurrentUid() != null &&
                            member.getCurrentUid().equals(wxUser.getId()) &&
                            member.getId().equals(member.getBindMid())) {
                        memberDtos.add(memberDto);
                    }
                }
            }else{
                for(Member member : members) {
                    MemberDto memberDto = new MemberDto();
                    //将member的属性赋值给memberDto
                    BeanUtils.copyProperties(member, memberDto);
                    if(member.getMode()!=null && member.getMode().equals("seasonMode")){
                        SeasonConfig seasonConfig = seasonConfigService.getById(member.getCurrentSeasonId());
                        memberDto.setSeasonConfig(seasonConfig);
                    }
                    memberDtos.add(memberDto);
                }
            }
            return ApiResponse.ok(memberDtos);
        } catch (Exception e) {
            logger.error("获取会员列表失败", e);
            return ApiResponse.error("获取会员列表失败");
        }
    }

    /**
     * 根据mid修改积分
     * @param mid 会员ID
     * @param memberPointLogs 积分日志请求
     * @return ApiResponse
     */
    @PostMapping("/updatePoints/{mid}")
    public ApiResponse updateMemberPoints(@PathVariable Integer mid, @RequestBody MemberPointLogsRequest memberPointLogs, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate today = LocalDate.now();
            String currentTime = today.atStartOfDay().format(formatter);
            memberPointLogs.setMid(mid);
            memberPointLogs.setType(3);
            memberPointLogs.setDay(currentTime);
            memberPointLogs.setStatus(0);
            if(seasonId!=null){
                SeasonPointLog result = memberPointLogsService.insert(memberPointLogs, seasonId, SeasonPointLog.class);
                if (result != null) {
                    return ApiResponse.ok(result);
                }
            }else{
                
                MemberPointLogs result = memberPointLogsService.insert(memberPointLogs, seasonId, MemberPointLogs.class);
                if (result != null) {
                    return ApiResponse.ok(result);
                }
            }
            
            return ApiResponse.error("修改积分失败");
        } catch (Exception e) {
            logger.error("修改会员积分失败", e);
            return ApiResponse.error("修改积分失败: " + e.getMessage());
        }
    }

    /**
     * 切换成员的模式（常规/赛季）
     * @param mid 成员ID
     * @param requestMap 请求参数，包括mode
     * @return API响应
     */
    @PostMapping("/{mid}/switchMode")
    public ApiResponse switchMode(
            @PathVariable Integer mid,
            @RequestBody Map<String, Object> requestMap) {
        try {
            String mode = (String) requestMap.get("mode");
            if (mode == null) {
                return ApiResponse.error("模式不能为空");
            }
            
            Member updatedMember = memberService.switchMode(mid, mode);
            return ApiResponse.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            logger.error("切换成员模式参数错误", e);
            return ApiResponse.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            logger.error("切换成员模式失败", e);
            return ApiResponse.error("切换成员模式失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新成员关联的赛季ID
     * @param mid 成员ID
     * @param requestMap 请求参数，包括seasonId
     * @return API响应
     */
    @PostMapping("/{mid}/updateSeasonId")
    public ApiResponse updateSeasonId(
            @PathVariable Integer mid,
            @RequestBody Map<String, Object> requestMap) {
        try {
            if (requestMap.get("seasonId") == null) {
                return ApiResponse.error("赛季ID不能为空");
            }
            Long seasonId = Long.valueOf(requestMap.get("seasonId").toString());
            
            Member updatedMember = memberService.updateCurrentSeasonId(mid, seasonId);
            return ApiResponse.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            logger.error("更新赛季ID参数错误", e);
            return ApiResponse.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            logger.error("更新赛季ID失败", e);
            return ApiResponse.error("更新赛季ID失败: " + e.getMessage());
        }
    }

    /**
     * 获取家庭组中其他成员的规则列表
     * @param mid 当前成员ID
     * @param familyCode 家庭代码
     * @return 其他成员及其规则列表
     */
    @GetMapping("/family/{mid}/rules")
    public ApiResponse getFamilyMembersRules(
            @PathVariable Integer mid,
            @RequestParam String familyCode) {
        try {
            List<Map<String, Object>> result = memberService.getFamilyMembersRules(mid, familyCode);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("获取家庭成员规则列表失败", e);
            return ApiResponse.error("获取家庭成员规则列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入规则
     * @param mid 目标成员ID
     * @param requestMap 请求参数，包含ruleIds数组
     * @return 导入结果
     */
    @PostMapping("/batchImportRules/{mid}")
    public ApiResponse batchImportRules(
            @PathVariable Integer mid,
            @RequestBody Map<String, Object> requestMap) {
        try {
            // 获取规则ID数组
            @SuppressWarnings("unchecked")
            List<Integer> ruleIds = (List<Integer>) requestMap.get("ruleIds");
            
            if (ruleIds == null || ruleIds.isEmpty()) {
                return ApiResponse.error("规则ID列表不能为空");
            }

            Map<String, Object> result = memberService.batchImportRules(mid, ruleIds);
            return ApiResponse.ok(result);
            
        } catch (IllegalArgumentException e) {
            logger.error("批量导入规则参数错误", e);
            return ApiResponse.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            logger.error("批量导入规则失败", e);
            return ApiResponse.error("批量导入规则失败: " + e.getMessage());
        }
    }

    /**
     * 导出习惯数据
     * @param mid 成员ID
     * @param seasonId 赛季ID（可选，为null时导出普通模式规则）
     * @return 导出的习惯数据
     */
    @GetMapping("/export/habits/{mid}")
    public ApiResponse exportHabits(@PathVariable Integer mid, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            List<Map<String, Object>> exportData = new ArrayList<>();
            
            if (seasonId != null) {
                // 导出赛季模式规则
                List<SeasonRule> seasonRules = memberRulesService.getActiveRulesByMid(mid, seasonId, SeasonRule.class);
                
                for (SeasonRule rule : seasonRules) {
                    Map<String, Object> ruleData = new HashMap<>();
                    ruleData.put("name", rule.getName());
                    ruleData.put("type", rule.getType());
                    ruleData.put("icon", rule.getIcon());
                    ruleData.put("iconType", rule.getIconType());
                    ruleData.put("weeks", rule.getWeeks());
                    ruleData.put("content", rule.getContent());
                    ruleData.put("sort", rule.getSort());
                    ruleData.put("quickScore", rule.getQuickScore());
                    ruleData.put("typeSort", rule.getTypeSort());
                    ruleData.put("enablePomodoro", rule.getEnablePomodoro());
                    ruleData.put("pomodoroTime", rule.getPomodoroTime());
                    ruleData.put("isAchievement", rule.getIsAchievement());
                    ruleData.put("completionConditions", rule.getCompletionConditions());
                    
                    // 如果配置了成就，查询成就数据
                    if (rule.getIsAchievement() != null && rule.getIsAchievement() == 1) {
                        List<SeasonRuleAchievement> achievements = ruleAchievementService.getByRuleId(
                            rule.getId().intValue(), seasonId, SeasonRuleAchievement.class);
                        
                        List<Map<String, Object>> achievementList = new ArrayList<>();
                        for (SeasonRuleAchievement achievement : achievements) {
                            Map<String, Object> achievementData = new HashMap<>();
                            achievementData.put("title", achievement.getTitle());
                            achievementData.put("img", achievement.getImg());
                            achievementData.put("conditionType", achievement.getConditionType());
                            achievementData.put("conditionValue", achievement.getConditionValue());
                            achievementData.put("conditionDesc", achievement.getConditionDesc());
                            achievementData.put("rewardType", achievement.getRewardType());
                            achievementData.put("rewardValue", achievement.getRewardValue());
                            achievementList.add(achievementData);
                        }
                        ruleData.put("achievements", achievementList);
                    } else {
                        ruleData.put("achievements", new ArrayList<>());
                    }
                    
                    exportData.add(ruleData);
                }
            } else {
                // 导出普通模式规则
                List<MemberRules> memberRules = memberRulesService.getActiveRulesByMid(mid, null, MemberRules.class);
                
                for (MemberRules rule : memberRules) {
                    Map<String, Object> ruleData = new HashMap<>();
                    ruleData.put("name", rule.getName());
                    ruleData.put("type", rule.getType());
                    ruleData.put("icon", rule.getIcon());
                    ruleData.put("iconType", rule.getIconType());
                    ruleData.put("weeks", rule.getWeeks());
                    ruleData.put("content", rule.getContent());
                    ruleData.put("sort", rule.getSort());
                    ruleData.put("quickScore", rule.getQuickScore());
                    ruleData.put("typeSort", rule.getTypeSort());
                    ruleData.put("enablePomodoro", rule.getEnablePomodoro());
                    ruleData.put("pomodoroTime", rule.getPomodoroTime());
                    ruleData.put("isAchievement", rule.getIsAchievement());
                    ruleData.put("completionConditions", rule.getCompletionConditions());
                    
                    // 如果配置了成就，查询成就数据
                    if (rule.getIsAchievement() != null && rule.getIsAchievement() == 1) {
                        List<RuleAchievement> achievements = ruleAchievementService.getByRuleId(
                            rule.getId(), null, RuleAchievement.class);
                        
                        List<Map<String, Object>> achievementList = new ArrayList<>();
                        for (RuleAchievement achievement : achievements) {
                            Map<String, Object> achievementData = new HashMap<>();
                            achievementData.put("title", achievement.getTitle());
                            achievementData.put("img", achievement.getImg());
                            achievementData.put("conditionType", achievement.getConditionType());
                            achievementData.put("conditionValue", achievement.getConditionValue());
                            achievementData.put("conditionDesc", achievement.getConditionDesc());
                            achievementData.put("rewardType", achievement.getRewardType());
                            achievementData.put("rewardValue", achievement.getRewardValue());
                            achievementList.add(achievementData);
                        }
                        ruleData.put("achievements", achievementList);
                    } else {
                        ruleData.put("achievements", new ArrayList<>());
                    }
                    
                    exportData.add(ruleData);
                }
            }
            
            // 按类型和排序进行分组和排序
            Map<String, Object> result = new HashMap<>();
            result.put("total", exportData.size());
            result.put("mode", seasonId != null ? "season" : "normal");
            result.put("exportTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.put("data", exportData);
            
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("导出习惯数据失败", e);
            return ApiResponse.error("导出习惯数据失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入习惯数据
     * @param mid 目标成员ID
     * @param requestMap 请求数据，包含habits数组，格式与导出数据相同
     * @param seasonId 赛季ID（可选，为null时导入到普通模式）
     * @return 导入结果
     */
    @PostMapping("/import/habits/{mid}")
    public ApiResponse importHabits(@PathVariable Integer mid, 
                                   @RequestBody Map<String, Object> requestMap,
                                   @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> habitsData = (List<Map<String, Object>>) requestMap.get("data");
            
            if (habitsData == null || habitsData.isEmpty()) {
                return ApiResponse.error("习惯数据不能为空");
            }

            List<MemberRuleRequest> memberRuleRequests = new ArrayList<>();
            List<Map<String, Object>> createdRules = new ArrayList<>();
            List<Map<String, Object>> importResults = new ArrayList<>();
            
            // 检查规则数量限制
            int currentRuleCount = memberRulesService.countActiveRulesByMid(mid, seasonId);
            if (currentRuleCount + habitsData.size() > 50) {
                return ApiResponse.error("规则数量已达上限(50个)，当前有 " + currentRuleCount + " 个规则，无法导入 " + habitsData.size() + " 个新规则");
            }
            
            // 处理每个习惯数据
            for (Map<String, Object> habitData : habitsData) {
                try {
                    // 检查必要字段
                    if (habitData.get("name") == null || habitData.get("type") == null) {
                        continue;
                    }
                    
                    String ruleName = (String) habitData.get("name");
                    
                    // 检查是否已存在同名规则
                    if (seasonId != null) {
                        SeasonRule existingRule = memberRulesService.getRuleByNameAndMid(ruleName, mid, seasonId, SeasonRule.class);
                        if (existingRule != null) {
                            Map<String, Object> skipResult = new HashMap<>();
                            skipResult.put("name", ruleName);
                            skipResult.put("status", "skipped");
                            skipResult.put("reason", "规则已存在");
                            importResults.add(skipResult);
                            continue;
                        }
                    } else {
                        MemberRules existingRule = memberRulesService.getRuleByNameAndMid(ruleName, mid, null, MemberRules.class);
                        if (existingRule != null) {
                            Map<String, Object> skipResult = new HashMap<>();
                            skipResult.put("name", ruleName);
                            skipResult.put("status", "skipped");
                            skipResult.put("reason", "规则已存在");
                            importResults.add(skipResult);
                            continue;
                        }
                    }
                    
                    // 创建 MemberRuleRequest
                    MemberRuleRequest request = new MemberRuleRequest();
                    request.setMid(mid);
                    request.setName(ruleName);
                    request.setType((String) habitData.get("type"));
                    request.setIcon((String) habitData.get("icon"));
                    request.setIconType(habitData.get("iconType") != null ? 
                        Integer.parseInt(habitData.get("iconType").toString()) : null);
                    request.setWeeks((String) habitData.get("weeks"));
                    request.setContent((String) habitData.get("content"));
                    request.setSort(habitData.get("sort") != null ? 
                        Integer.parseInt(habitData.get("sort").toString()) : 0);
                    request.setQuickScore(habitData.get("quickScore") != null ? 
                        Integer.parseInt(habitData.get("quickScore").toString()) : null);
                    request.setTypeSort(habitData.get("typeSort") != null ? 
                        Integer.parseInt(habitData.get("typeSort").toString()) : null);
                    request.setEnablePomodoro(habitData.get("enablePomodoro") != null ? 
                        Integer.parseInt(habitData.get("enablePomodoro").toString()) : null);
                    request.setPomodoroTime(habitData.get("pomodoroTime") != null ? 
                        Integer.parseInt(habitData.get("pomodoroTime").toString()) : null);
                    request.setIsAchievement(habitData.get("isAchievement") != null ? 
                        Integer.parseInt(habitData.get("isAchievement").toString()) : null);
                    request.setCompletionConditions((String) habitData.get("completionConditions"));
                    
                    // 创建规则
                    if (seasonId != null) {
                        SeasonRule createdRule = memberRulesService.insert(request, seasonId, SeasonRule.class);
                        
                        // 处理成就数据
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> achievements = (List<Map<String, Object>>) habitData.get("achievements");
                        if (achievements != null && !achievements.isEmpty() && request.getIsAchievement() != null && request.getIsAchievement() == 1) {
                            List<Map<String, Object>> createdAchievements = new ArrayList<>();
                            for (Map<String, Object> achievementData : achievements) {
                                SeasonRuleAchievement achievement = new SeasonRuleAchievement();
                                achievement.setMId(mid);
                                achievement.setSeasonId(seasonId);
                                achievement.setRuleId(createdRule.getId().intValue());
                                achievement.setTitle((String) achievementData.get("title"));
                                achievement.setImg((String) achievementData.get("img"));
                                achievement.setConditionType((String) achievementData.get("conditionType"));
                                achievement.setConditionValue(achievementData.get("conditionValue") != null ? 
                                    Integer.parseInt(achievementData.get("conditionValue").toString()) : null);
                                achievement.setConditionDesc((String) achievementData.get("conditionDesc"));
                                achievement.setRewardType((String) achievementData.get("rewardType"));
                                achievement.setRewardValue(achievementData.get("rewardValue") != null ? 
                                    Integer.parseInt(achievementData.get("rewardValue").toString()) : null);
                                
                                SeasonRuleAchievement createdAchievement = (SeasonRuleAchievement) ruleAchievementService.insert(achievement, seasonId);
                                Map<String, Object> achievementResult = new HashMap<>();
                                achievementResult.put("id", createdAchievement.getId());
                                achievementResult.put("title", createdAchievement.getTitle());
                                achievementResult.put("conditionType", createdAchievement.getConditionType());
                                achievementResult.put("conditionValue", createdAchievement.getConditionValue());
                                createdAchievements.add(achievementResult);
                            }
                            
                            Map<String, Object> successResult = new HashMap<>();
                            successResult.put("name", ruleName);
                            successResult.put("status", "success");
                            successResult.put("ruleId", createdRule.getId());
                            successResult.put("achievements", createdAchievements);
                            importResults.add(successResult);
                        } else {
                            Map<String, Object> successResult = new HashMap<>();
                            successResult.put("name", ruleName);
                            successResult.put("status", "success");
                            successResult.put("ruleId", createdRule.getId());
                            importResults.add(successResult);
                        }
                    } else {
                        MemberRules createdRule = memberRulesService.insert(request, null, MemberRules.class);
                        
                        // 处理成就数据
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> achievements = (List<Map<String, Object>>) habitData.get("achievements");
                        if (achievements != null && !achievements.isEmpty() && request.getIsAchievement() != null && request.getIsAchievement() == 1) {
                            List<Map<String, Object>> createdAchievements = new ArrayList<>();
                            for (Map<String, Object> achievementData : achievements) {
                                RuleAchievement achievement = new RuleAchievement();
                                achievement.setMId(mid);
                                achievement.setRuleId(createdRule.getId());
                                achievement.setTitle((String) achievementData.get("title"));
                                achievement.setImg((String) achievementData.get("img"));
                                achievement.setConditionType((String) achievementData.get("conditionType"));
                                achievement.setConditionValue(achievementData.get("conditionValue") != null ? 
                                    Integer.parseInt(achievementData.get("conditionValue").toString()) : null);
                                achievement.setConditionDesc((String) achievementData.get("conditionDesc"));
                                achievement.setRewardType((String) achievementData.get("rewardType"));
                                achievement.setRewardValue(achievementData.get("rewardValue") != null ? 
                                    Integer.parseInt(achievementData.get("rewardValue").toString()) : null);
                                
                                RuleAchievement createdAchievement = (RuleAchievement) ruleAchievementService.insert(achievement, null);
                                Map<String, Object> achievementResult = new HashMap<>();
                                achievementResult.put("id", createdAchievement.getId());
                                achievementResult.put("title", createdAchievement.getTitle());
                                achievementResult.put("conditionType", createdAchievement.getConditionType());
                                achievementResult.put("conditionValue", createdAchievement.getConditionValue());
                                createdAchievements.add(achievementResult);
                            }
                            
                            Map<String, Object> successResult = new HashMap<>();
                            successResult.put("name", ruleName);
                            successResult.put("status", "success");
                            successResult.put("ruleId", createdRule.getId());
                            successResult.put("achievements", createdAchievements);
                            importResults.add(successResult);
                        } else {
                            Map<String, Object> successResult = new HashMap<>();
                            successResult.put("name", ruleName);
                            successResult.put("status", "success");
                            successResult.put("ruleId", createdRule.getId());
                            importResults.add(successResult);
                        }
                    }
                } catch (Exception e) {
                    logger.error("导入单个习惯失败: " + habitData.get("name"), e);
                    Map<String, Object> failResult = new HashMap<>();
                    failResult.put("name", habitData.get("name"));
                    failResult.put("status", "failed");
                    failResult.put("reason", "导入失败: " + e.getMessage());
                    importResults.add(failResult);
                }
            }
            
            // 统计结果
            long successCount = importResults.stream().filter(r -> "success".equals(r.get("status"))).count();
            long skippedCount = importResults.stream().filter(r -> "skipped".equals(r.get("status"))).count();
            long failedCount = importResults.stream().filter(r -> "failed".equals(r.get("status"))).count();
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", habitsData.size());
            result.put("success", successCount);
            result.put("skipped", skippedCount);
            result.put("failed", failedCount);
            result.put("details", importResults);
            result.put("importTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("批量导入习惯数据失败", e);
            return ApiResponse.error("批量导入习惯数据失败: " + e.getMessage());
        }
    }
}
