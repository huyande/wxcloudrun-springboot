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
        MemberRules lastRule = memberRulesService.getLastSortByTypeAndMid(memberRuleRequest.getMid(),memberRuleRequest.getType());
        if(lastRule!=null){
            memberRuleRequest.setSort(lastRule.getSort()+1);
        }else{
            memberRuleRequest.setSort(0);
        }
        MemberRules memberRules = memberRulesService.insert(memberRuleRequest);
        return ApiResponse.ok(memberRules);
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
                        ruleMap.put("row_status", rule.getStatus());//由于之前的设计将status 标识成打卡的状态了，row_status 标识是否删除
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
            if(memberPointLogs.getType() ==null){
                memberPointLogs.setType(0);
            }
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
    public ApiResponse getPointMonthDay(@PathVariable Integer mid) {
        try {
            List<Map<String, Object>> logs = memberPointLogsService.getPointLogsByMidAndMonth(mid);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取会员积分详情失败", e);
            return ApiResponse.error("获取会员积分详情失败");
        }
    }

    // 根据uid获取memberlist
    @GetMapping("/memberlist/{uid}")
    public ApiResponse getMemberListByUid(@PathVariable Integer uid) {
        try {
            List<Member> members = memberService.getMembersByUid(uid);
            return ApiResponse.ok(members);
        } catch (Exception e) {
            logger.error("获取会员列表失败", e);
            return ApiResponse.error("获取会员列表失败");
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
     * @param sort 新的排序值
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
     * @param memberRequest 更新的member信息
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
}
