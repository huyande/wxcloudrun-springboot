package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.service.AchievementCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievement-category")
public class AchievementCategoryController {

    private final AchievementCategoryService achievementCategoryService;

    @Autowired
    public AchievementCategoryController(AchievementCategoryService achievementCategoryService) {
        this.achievementCategoryService = achievementCategoryService;
    }

    @GetMapping
    public ApiResponse getAllCategorizedAchievements() {
        Map<String, List<String>> categorizedAchievements = achievementCategoryService.getAllCategorizedAchievements();
        return ApiResponse.ok(categorizedAchievements);
    }
} 