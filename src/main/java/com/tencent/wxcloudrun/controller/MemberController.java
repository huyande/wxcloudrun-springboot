package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import com.tencent.wxcloudrun.service.MemberRulesService;
import com.tencent.wxcloudrun.service.MemberPointLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    final Logger logger;

    public MemberController(MemberService memberService,WxuserService wxuserService,MemberRulesService memberRulesService,MemberPointLogsService memberPointLogsService){
        this.memberService = memberService;
        this.wxuserService = wxuserService;
        this.memberRulesService = memberRulesService;
        this.memberPointLogsService = memberPointLogsService;
        this.logger =  LoggerFactory.getLogger(MemberController.class);
    }

    @PostMapping("/create")
    public ApiResponse create(@RequestBody MemberRequest memberRequest){
        Member member = memberService.insert(memberRequest);
        return ApiResponse.ok(member);
    }

    @PostMapping("/createRule")
    public ApiResponse createRule(@RequestBody MemberRuleRequest memberRuleRequest){
        MemberRules memberRules = memberRulesService.insert(memberRuleRequest);
        return ApiResponse.ok(memberRules);
    }   
    @GetMapping("/rules/{mid}")
    public ApiResponse getRulesByMid(@PathVariable Integer mid) {
        try {
            List<MemberRules> rules = memberRulesService.getRulesByMid(mid);
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
            memberPointLogsService.insert(memberPointLogs);
            return ApiResponse.ok();
        } catch (Exception e) {
            logger.error("添加会员积分日志失败", e);
            return ApiResponse.error("添加会员积分日志失败");
        }
    }

    // 根据mid获取统计用户的积分
    @GetMapping("/pointsum/{mid}")
    public ApiResponse getPointSumByMid(@PathVariable Integer mid) {
        try {
            Integer pointSum = memberPointLogsService.getPointSumByMid(mid);
            Integer days = memberPointLogsService.getPointDaysByMid(mid);

            HashMap<String, Object> result = new HashMap<>();
            result.put("pointSum", pointSum);
            result.put("days", days);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("获取会员积分总和失败", e);
            return ApiResponse.error("获取会员积分总和失败");
        }
    }

}
