package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.AchievementCategory;

import java.util.List;
import java.util.Map;

public interface AchievementCategoryService {
    
    void createAchievementCategory(AchievementCategory achievementCategory);
    
    List<AchievementCategory> getAllAchievementCategories();
    
    List<AchievementCategory> getAchievementCategoriesByCategory(String categoryName);
    
    Map<String, List<String>> getAllCategorizedAchievements();
    
    void updateAchievementCategory(AchievementCategory achievementCategory);
    
    void deleteAchievementCategory(Integer id);
} 