package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("member")
public class MemberController {

    final MemberService memberService;
    final WxuserService wxuserService;
    final MemberRulesService memberRulesService;
    final MemberPointLogsService memberPointLogsService;
    final WishLogService wishLogService;
    final Logger logger;

    public MemberController(MemberService memberService,
                            WxuserService wxuserService,
                            MemberRulesService memberRulesService,
                            MemberPointLogsService memberPointLogsService,
                            WishLogService wishLogService) {
        this.memberService = memberService;
        this.wxuserService = wxuserService;
        this.memberRulesService = memberRulesService;
        this.memberPointLogsService = memberPointLogsService;
        this.wishLogService = wishLogService;
        this.logger =  LoggerFactory.getLogger(MemberController.class);
    }

    @PostMapping("/create")
    public ApiResponse create(@RequestBody MemberRequest memberRequest){
        Member member = memberService.insert(memberRequest);
        return ApiResponse.ok(member);
    }

    @PostMapping("/createRule")
    public ApiResponse createRule(@RequestBody MemberRuleRequest memberRuleRequest){
        if(memberRulesService.canCreateRule(memberRuleRequest.getMid())){
            MemberRules lastRule = memberRulesService.getLastSortByTypeAndMid(memberRuleRequest.getMid(),memberRuleRequest.getType());
            if(lastRule!=null){
                memberRuleRequest.setSort(lastRule.getSort()+1);
            }else{
                memberRuleRequest.setSort(0);
            }
            MemberRules memberRules = memberRulesService.insert(memberRuleRequest);
            return ApiResponse.ok(memberRules);
        }else{
            return ApiResponse.error("规则数量已达上限(50个)，无法创建新规则，如有需要可以联系我");
        }
    }


    /**
     *  批量添加规则
     * @param memberRuleRequests
     * @return
     */
    @PostMapping("/batchAddRules")
    public ApiResponse createRules(@RequestBody List<MemberRuleRequest> memberRuleRequests){
        List<MemberRules> memberRules = memberRulesService.insertBatch(memberRuleRequests);
        return ApiResponse.ok(memberRules);
    }

    /**
     *  查询规则更具id
     * @param id
     * @return
     */
    @GetMapping("/ruleById/{id}")
    public ApiResponse getRuleById(@PathVariable Integer id) {
        try {
            MemberRules rule = memberRulesService.getRuleById(id);
            if (rule != null) {
                return ApiResponse.ok(rule);
            } else {
                return ApiResponse.ok(null);
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
    public ApiResponse getRuleByNameAndMid(@PathVariable Integer mid ,@RequestParam String name) {
        try {
            MemberRules rule = memberRulesService.getRuleByNameAndMid(name, mid);
            if (rule != null) {
                Map<String, Object> ruleMap = new HashMap<>();
                ruleMap.put("id", rule.getId());
                ruleMap.put("name", rule.getName());
                ruleMap.put("icon", rule.getIcon());
                ruleMap.put("iconType", rule.getIconType());
                ruleMap.put("type", rule.getType());
                return ApiResponse.ok(ruleMap);
            } else {
                return ApiResponse.ok(null);
            }
        } catch (Exception e) {
            logger.error("获取规则失败", e);
            return ApiResponse.error("获取规则失败");
        }
    }

    @PutMapping("/updateRule/{id}")
    public ApiResponse updateRuleById(@PathVariable Integer id, @RequestBody MemberRuleRequest memberRuleRequest) {
        try {
            memberRulesService.updateRuleById(id, memberRuleRequest);
            return ApiResponse.ok("更新成功");
        } catch (Exception e) {
            logger.error("更新规则失败", e);
            return ApiResponse.error("更新规则失败");
        }
    }


    @DeleteMapping("/rule/{id}")
    public ApiResponse deleteRuleById(@PathVariable Integer id) {
        try {
            memberRulesService.delete(id);
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
    public ApiResponse getRulesByMid(@PathVariable Integer mid, @RequestParam String day) {
        try {
            List<MemberRules> rules = memberRulesService.getRulesByMid(mid,day);
            
            // 获取一周的开始和结束时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDate = LocalDateTime.parse(day, formatter);
            LocalDateTime weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime weekEnd = weekStart.plusDays(7).minusSeconds(1);
            
            // 获取一周内的打卡记录
            List<Map<String, Object>> weeklyLogs = memberPointLogsService.getWeeklyPointLogs(mid, weekStart, weekEnd);
            
            // 创建规则ID到周打卡状态的映射
            Map<Integer, boolean[]> ruleWeekStatus = new HashMap<>();
            for (Map<String, Object> log : weeklyLogs) {
                Integer ruleId = Integer.parseInt(log.get("ruleId").toString());
                int weekday = Integer.parseInt(log.get("weekday").toString());
                if (!ruleWeekStatus.containsKey(ruleId)) {
                    ruleWeekStatus.put(ruleId, new boolean[7]);
                }
                ruleWeekStatus.get(ruleId)[weekday] = true;
            }
            
            // 将规则按类型分组
            Map<String, List<Map<String, Object>>> groupedRules = rules.stream()
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
                        ruleMap.put("quickScore",rule.getQuickScore());
                        ruleMap.put("content",rule.getContent());
                        ruleMap.put("enablePomodoro",rule.getEnablePomodoro());
                        ruleMap.put("pomodoroTime",rule.getPomodoroTime());
                        // 添加周打卡状态
                        boolean[] weekStatus = ruleWeekStatus.getOrDefault(rule.getId(), new boolean[7]);
                        Map<String, Boolean> weekStatusMap = new HashMap<>();
                        weekStatusMap.put("星期一", weekStatus[1]);    // 周一
                        weekStatusMap.put("星期二", weekStatus[2]);   // 周二
                        weekStatusMap.put("星期三", weekStatus[3]); // 周三
                        weekStatusMap.put("星期四", weekStatus[4]);  // 周四
                        weekStatusMap.put("星期五", weekStatus[5]);    // 周五
                        weekStatusMap.put("星期六", weekStatus[6]);  // 周六
                        weekStatusMap.put("星期日", weekStatus[0]);    // 周日
                        ruleMap.put("weekStatus", weekStatusMap);
                        
                        return ruleMap;
                    }, Collectors.toList())));

            // 构建最终的输出格式
            List<Map<String, Object>> formattedRules = groupedRules.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> typeGroup = new HashMap<>();
                    typeGroup.put("type", entry.getKey());
                    typeGroup.put("items", entry.getValue());
                    return typeGroup;
                })
                .collect(Collectors.toList());

            // 提取每种类型的一个规则，用于获取该类型的排序值
            Map<String, Integer> typeSortMap = rules.stream()
                .collect(Collectors.toMap(
                    MemberRules::getType,
                    MemberRules::getTypeSort,
                    (existing, replacement) -> existing,
                    HashMap::new
                ));

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
    public ApiResponse getPointLogsByMid(@PathVariable Integer mid, @RequestParam String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = dateTime.toLocalDate().atTime(23, 59, 59);
            List<MemberPointLogs> logs = memberPointLogsService.getLogsByMidAndDate(mid, startOfDay,endOfDay);
            
//            HashMap<String, Object> result = new HashMap<>();
//            result.put("logs", logs);
            
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取会员积分日志失败", e);
            return ApiResponse.error("获取会员积分日志失败");
        }
    }


    @PostMapping("/pointlogs/{mid}")
    public ApiResponse addPointLog(@PathVariable Integer mid, @RequestBody MemberPointLogsRequest memberPointLogs) {
        try {
            memberPointLogs.setMid(mid);
            if(memberPointLogs.getType() ==null){//设置积分类型是打卡
                memberPointLogs.setType(0);
            }
            MemberPointLogs pointLogs = memberPointLogsService.insert(memberPointLogs);
            if(pointLogs!=null){
                return ApiResponse.ok();
            }else {
                return ApiResponse.error("积分为0不能记录");
            }
        } catch (Exception e) {
            logger.error("添加会员积分日志失败", e);
            return ApiResponse.error("添加积分失败");
        }
    }

    // 根据mid获取统计用户的积分当日的积分请求
    @GetMapping("/pointCurrentDaySum/{mid}")
    public ApiResponse getPointCurrentDaySumByMid(@PathVariable Integer mid, @RequestParam String day) {
        try {
//            Member member = memberService.getMemberById(mid);
//            Integer pointSum = memberPointLogsService.getPointSumByMid(mid);
            Integer pointCurrentSum = memberPointLogsService.getCurrentDayPointSumByMid(mid,day);
            Integer wishSum =  wishLogService.getSumNumByMid(mid);
            memberPointLogsService.getLastPointSum(mid);
            HashMap<String, Object> result = new HashMap<>();
//            int  total = (pointSum == null ? 0 : pointSum) - (wishSum==null ? 0 :wishSum);
            Integer lastPointSum = memberPointLogsService.getLastPointSum(mid);//计算剩余积分
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
    public ApiResponse getPointSumByMid(@PathVariable Integer mid) {
        try {
            Member member = memberService.getMemberById(mid);
            Integer pointSum = memberPointLogsService.getPointSumByMid(mid);
            Integer days = memberPointLogsService.getPointDaysByMid(mid);
            Integer wishSum =  wishLogService.getSumNumByMid(mid);
            HashMap<String, Object> result = new HashMap<>();
            int  total = (pointSum == null ? 0 : pointSum) - (wishSum==null ? 0 :wishSum);
            result.put("pointSum", total+member.getPointTotal());
            result.put("days", days);
            result.put("originalPointSum", (pointSum == null ? 0 : pointSum)+member.getPointTotal());//累计的积分，不减去wishSum的积分
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("获取会员积分总和失败", e);
            return ApiResponse.error("获取会员积分总和失败");
        }
    }

    @GetMapping("/pointlogMonthDay/{mid}")
    public ApiResponse getPointMonthDay(@PathVariable Integer mid, @RequestParam(required = false) String yearMonth) {
        try {
            List<Map<String, Object>> logs;
            if (yearMonth != null && !yearMonth.isEmpty()) {
                // 如果提供了yearMonth参数，查询指定月份的数据
                logs = memberPointLogsService.getPointLogsByMidAndSpecificMonth(mid, yearMonth);
            } else {
                // 没有提供yearMonth参数，查询当月数据
                logs = memberPointLogsService.getPointLogsByMidAndMonth(mid);
            }
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取会员积分详情失败", e);
            return ApiResponse.error("获取会员积分详情失败");
        }
    }

    // 根据mid获取member
    @GetMapping("/logCount/{mid}")
    public ApiResponse getMemberLogCountByMid(@PathVariable Integer mid) {
        try {
            Integer count = memberPointLogsService.getAllCountLogsByDayMid(mid);
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
    public ApiResponse updateRuleSort(@PathVariable Integer id, @RequestBody MemberRuleRequest memberRuleRequest) {
        try {
            MemberRules rule = memberRulesService.getRuleById(id);
            if (rule == null) {
                return ApiResponse.error("规则不存在");
            }
            rule.setSort(memberRuleRequest.getSort());
            memberRulesService.updateRule(rule);
            return ApiResponse.ok(rule);
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
    public ApiResponse swapRuleSort(@PathVariable Integer currentId, @PathVariable Integer targetId) {
        try {
            memberRulesService.swapRuleSort(currentId, targetId);
            return ApiResponse.ok("交换排序成功");
        } catch (Exception e) {
            logger.error("交换规则排序失败", e);
            return ApiResponse.error("交换规则排序失败: " + e.getMessage());
        }
    }

    /**
     * 根据mid获取所有status=1的规则列表
     * @param mid
     * @return
     */
    @GetMapping("/rules/active/{mid}")
    public ApiResponse getActiveRulesByMid(@PathVariable Integer mid) {
        try {
            List<MemberRules> rules = memberRulesService.getActiveRulesByMid(mid);
            // 将规则按类型分组
            Map<String, List<Map<String, Object>>> groupedRules = rules.stream()
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

            // 构建最终的输出格式
            List<Map<String, Object>> formattedRules = groupedRules.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> typeGroup = new HashMap<>();
                    typeGroup.put("type", entry.getKey());
                    typeGroup.put("items", entry.getValue());
                    return typeGroup;
                })
                .collect(Collectors.toList());

            // 提取每种类型的一个规则，用于获取该类型的排序值
            Map<String, Integer> typeSortMap = rules.stream()
                .collect(Collectors.toMap(
                    MemberRules::getType,
                    MemberRules::getTypeSort,
                    (existing, replacement) -> existing,
                    HashMap::new
                ));

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
    public ApiResponse getPointLogsByMid(@PathVariable Integer mid, @PathVariable Integer page) {
        try {
            Map<String, Object> data  = memberPointLogsService.getPointLogsByMid(mid,page);
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
    public ApiResponse getStreakInfo(@PathVariable Integer mid, @PathVariable Integer ruleId) {
        try {
            Map<String, Integer> streakInfo = memberPointLogsService.getStreakInfo(mid, ruleId);
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
            @RequestParam String yearMonth) {
        try {
            List<Map<String, Object>> records = memberPointLogsService.getMonthlyCheckInRecords(mid, ruleId, yearMonth);
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
            @RequestParam String endDay) {
        try {
            List<Map<String, Object>> records = memberPointLogsService.getPointLogsByDateRange(mid, ruleId, startDay, endDay);
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
            @RequestParam String endDay) {
        try {
            List<Map<String, Object>> records = memberPointLogsService.getPointLogsByDateRangeTotal(mid, startDay, endDay);
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
            @RequestParam(required = false) Integer year) {
        try {
            // 如果未指定年份，使用当前年份
            if (year == null) {
                year = LocalDateTime.now().getYear();
            }
            List<Map<String, Object>> yearlyHeatmap = memberPointLogsService.getYearlyHeatmap(mid, ruleId, year);
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
            @RequestParam(required = false) Integer year) {
        try {
            // 如果未指定年份，使用当前年份
            if (year == null) {
                year = LocalDateTime.now().getYear();
            }
            List<Map<String, Object>> yearlyHeatmap = memberPointLogsService.getYearlyHeatmapAll(mid, year);
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
    public ApiResponse getPointlogCurrentDayDetail(@PathVariable Integer mid, @RequestParam String day) {
        try {
            List<Map<String, Object>> details = memberPointLogsService.getPointlogCurrentDayDetail(mid, day);
            return ApiResponse.ok(details);
        } catch (Exception e) {
            logger.error("获取积分详情失败", e);
            return ApiResponse.error("获取积分详情失败");
        }
    }

    /**
     * 批量更新规则分类名称
     * @param mid 会员ID
     * @param oldType 旧分类名称
     * @param newType 新分类名称
     * @return 更新结果
     */
    @PutMapping("/rules/{mid}/updateType")
    public ApiResponse updateRuleType(
            @PathVariable Integer mid,
            @RequestBody Map<String, String> requestMap) {
        try {
            String oldType = requestMap.get("oldType");
            String newType = requestMap.get("newType");
            int updatedCount = memberRulesService.updateRuleType(mid, oldType, newType);
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
    public ApiResponse getRuleTypes(@PathVariable Integer mid) {
        try {
            List<Map<String, Integer>> types = memberRulesService.getRuleTypes(mid);
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
            List<Member> members = memberService.getMembersByUid(uid);
            if(wxUser.getRole()!=null && wxUser.getRole()==7){//表示角色时 孩子自己
                List<Member> filteredMembers = new ArrayList<>();
                for(Member member : members) {
                    if(member.getCurrentUid() != null &&
                            member.getCurrentUid().equals(wxUser.getId()) &&
                            member.getId().equals(member.getBindMid())) {
                        filteredMembers.add(member);
                    }
                }
                if(filteredMembers.size()!=0){
                    members = filteredMembers;
                }
            }
            return ApiResponse.ok(members);
        } catch (Exception e) {
            logger.error("获取会员列表失败", e);
            return ApiResponse.error("获取会员列表失败");
        }
    }
}
