package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rule-achievement-logs")
public class RuleAchievementLogController {

    @Autowired
    private RuleAchievementLogService ruleAchievementLogService;

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Integer id) {
        RuleAchievementLog log = ruleAchievementLogService.getById(id);
        return ApiResponse.ok(log);
    }

    @GetMapping("/member/{mid}")
    public ApiResponse getByMemberId(@PathVariable Integer mid) {
        List<RuleAchievementLog> logs = ruleAchievementLogService.getByMemberId(mid);
        return ApiResponse.ok(logs);
    }

    @GetMapping("/achievement/{raId}")
    public ApiResponse getByAchievementId(@PathVariable Integer raId) {
        List<RuleAchievementLog> logs = ruleAchievementLogService.getByAchievementId(raId);
        return ApiResponse.ok(logs);
    }

    @PostMapping
    public ApiResponse insert(@RequestBody RuleAchievementLog log) {
        int result = ruleAchievementLogService.insert(log);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(@PathVariable Integer id) {
        int result = ruleAchievementLogService.deleteById(id);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/achievement/{raId}/member/{mid}")
    public ApiResponse deleteByAchievementAndMember(
            @PathVariable Integer raId,
            @PathVariable Integer mid) {
        int result = ruleAchievementLogService.deleteByAchievementAndMember(raId, mid);
        return ApiResponse.ok(result);
    }
} 