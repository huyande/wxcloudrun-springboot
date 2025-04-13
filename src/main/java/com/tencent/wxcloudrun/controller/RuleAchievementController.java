package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.RuleAchievement;
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
    public ApiResponse getById(@PathVariable Integer id) {
        RuleAchievement ruleAchievement = ruleAchievementService.getById(id);
        return ApiResponse.ok(ruleAchievement);
    }

    @GetMapping
    public ApiResponse list() {
        List<RuleAchievement> list = ruleAchievementService.list();
        return ApiResponse.ok(list);
    }

    @GetMapping("/rule/{ruleId}")
    public ApiResponse getByRuleId(@PathVariable Integer ruleId) {
        List<RuleAchievement> list = ruleAchievementService.getByRuleId(ruleId);
        return ApiResponse.ok(list);
    }

    @PostMapping
    public ApiResponse insert(@RequestBody RuleAchievement ruleAchievement) {
        ruleAchievementService.insert(ruleAchievement);
        return ApiResponse.ok(ruleAchievement.getId());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Integer id, @RequestBody RuleAchievement ruleAchievement) {
        ruleAchievement.setId(id);
        int result = ruleAchievementService.update(ruleAchievement);
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(@PathVariable Integer id) {
        ruleAchievementService.deleteById(id);
        return ApiResponse.ok();
    }
} 