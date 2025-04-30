package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.AchievementCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AchievementCategoryMapper {
    
    void createAchievementCategory(AchievementCategory achievementCategory);
    
    List<AchievementCategory> getAllAchievementCategories();
    
    List<AchievementCategory> getAchievementCategoriesByCategory(@Param("categoryName") String categoryName);
    
    List<String> getAllCategoryNames();
    
    void updateAchievementCategory(AchievementCategory achievementCategory);
    
    void deleteAchievementCategory(@Param("id") Integer id);
} 