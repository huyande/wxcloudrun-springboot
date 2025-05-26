package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.model.SeasonRuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rule-achievement-logs")
public class RuleAchievementLogController {

    @Autowired
    private RuleAchievementLogService ruleAchievementLogService;

    @GetMapping("/{id}")
    public ApiResponse getById(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        if (seasonId != null) {
            SeasonRuleAchievementLog log = ruleAchievementLogService.getById(id, seasonId, SeasonRuleAchievementLog.class);
            return ApiResponse.ok(log);
        } else {
            RuleAchievementLog log = ruleAchievementLogService.getById(id, null, RuleAchievementLog.class);
            return ApiResponse.ok(log);
        }
    }

    @GetMapping("/member/{mid}")
    public ApiResponse getByMemberId(
            @PathVariable Integer mid,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        if (seasonId != null) {
            List<SeasonRuleAchievementLog> logs = ruleAchievementLogService.getByMemberId(mid, seasonId, SeasonRuleAchievementLog.class);
            return ApiResponse.ok(logs);
        } else {
            List<RuleAchievementLog> logs = ruleAchievementLogService.getByMemberId(mid, null, RuleAchievementLog.class);
            return ApiResponse.ok(logs);
        }
    }

    @GetMapping("/achievement/{raId}")
    public ApiResponse getByAchievementId(
            @PathVariable Integer raId,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        if (seasonId != null) {
            List<SeasonRuleAchievementLog> logs = ruleAchievementLogService.getByAchievementId(raId, seasonId, SeasonRuleAchievementLog.class);
            return ApiResponse.ok(logs);
        } else {
            List<RuleAchievementLog> logs = ruleAchievementLogService.getByAchievementId(raId, null, RuleAchievementLog.class);
            return ApiResponse.ok(logs);
        }
    }

    @GetMapping("/member-achievements/{mid}")
    public ApiResponse getMemberAchievements(
            @PathVariable Integer mid,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<Map<String, Object>> achievements = ruleAchievementLogService.getMemberAchievements(mid, seasonId);
        return ApiResponse.ok(achievements);
    }

    @PostMapping
    public ApiResponse insert(
            @RequestBody Object log,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        Object result = ruleAchievementLogService.insert(log, seasonId);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        boolean result = ruleAchievementLogService.deleteById(id, seasonId);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/achievement/{raId}/member/{mid}")
    public ApiResponse deleteByAchievementAndMember(
            @PathVariable Integer raId,
            @PathVariable Integer mid,
            @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        boolean result = ruleAchievementLogService.deleteByAchievementAndMember(raId, mid, seasonId);
        return ApiResponse.ok(result);
    }
} 