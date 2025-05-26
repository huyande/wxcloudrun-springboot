package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.RuleAchievement;
import com.tencent.wxcloudrun.model.SeasonRuleAchievement;
import com.tencent.wxcloudrun.service.RuleAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rule-achievements")
public class RuleAchievementController {

    @Autowired
    private RuleAchievementService ruleAchievementService;

    @GetMapping("/{id}")
    public ApiResponse getById(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                               @PathVariable Integer id) {
        if (seasonId != null) {
            SeasonRuleAchievement seasonRuleAchievement = ruleAchievementService.getById(id, seasonId, SeasonRuleAchievement.class);
            return ApiResponse.ok(seasonRuleAchievement);
        } else {
            RuleAchievement ruleAchievement = ruleAchievementService.getById(id, null, RuleAchievement.class);
            return ApiResponse.ok(ruleAchievement);
        }
    }

    @GetMapping
    public ApiResponse list(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        if (seasonId != null) {
            List<SeasonRuleAchievement> list = ruleAchievementService.list(seasonId, SeasonRuleAchievement.class);
            return ApiResponse.ok(list);
        } else {
            List<RuleAchievement> list = ruleAchievementService.list(null, RuleAchievement.class);
            return ApiResponse.ok(list);
        }
    }

    @GetMapping("/rule/{ruleId}")
    public ApiResponse getByRuleId(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                   @PathVariable Integer ruleId) {
        if (seasonId != null) {
            List<SeasonRuleAchievement> list = ruleAchievementService.getByRuleId(ruleId, seasonId, SeasonRuleAchievement.class);
            return ApiResponse.ok(list);
        } else {
            List<RuleAchievement> list = ruleAchievementService.getByRuleId(ruleId, null, RuleAchievement.class);
            return ApiResponse.ok(list);
        }
    }

    @PostMapping
    public ApiResponse insert(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                            @RequestBody RuleAchievement ruleAchievement) {
        Object result = ruleAchievementService.insert(ruleAchievement, seasonId);
        return ApiResponse.ok(result);
    }

    @PutMapping("/{id}")
    public ApiResponse update(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                            @PathVariable Integer id,
                            @RequestBody RuleAchievement ruleAchievement) {
        Object result = ruleAchievementService.update(id, ruleAchievement, seasonId);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                @PathVariable Integer id) {
        ruleAchievementService.deleteById(id, seasonId);
        return ApiResponse.ok();
    }
} 