package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.AchievementCategoryMapper;
import com.tencent.wxcloudrun.model.AchievementCategory;
import com.tencent.wxcloudrun.service.AchievementCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AchievementCategoryServiceImpl implements AchievementCategoryService {

    private final AchievementCategoryMapper achievementCategoryMapper;

    @Autowired
    public AchievementCategoryServiceImpl(AchievementCategoryMapper achievementCategoryMapper) {
        this.achievementCategoryMapper = achievementCategoryMapper;
    }

    @Override
    public void createAchievementCategory(AchievementCategory achievementCategory) {
        achievementCategoryMapper.createAchievementCategory(achievementCategory);
    }

    @Override
    public List<AchievementCategory> getAllAchievementCategories() {
        return achievementCategoryMapper.getAllAchievementCategories();
    }

    @Override
    public List<AchievementCategory> getAchievementCategoriesByCategory(String categoryName) {
        return achievementCategoryMapper.getAchievementCategoriesByCategory(categoryName);
    }

    @Override
    public Map<String, List<String>> getAllCategorizedAchievements() {
        // 获取所有成就分类数据（忽略状态字段，查询所有数据）
        List<AchievementCategory> allCategories = achievementCategoryMapper.getAllAchievementCategories();
        
        // 使用Java 8 Stream API将成就按分类名称分组
        // 只提取分类名称和成就名称，忽略状态和时间字段
        Map<String, List<String>> result = allCategories.stream()
                .collect(Collectors.groupingBy(
                        AchievementCategory::getCategoryName,
                        Collectors.mapping(AchievementCategory::getAchievementName, Collectors.toList())
                ));
        
        return result;
    }

    @Override
    public void updateAchievementCategory(AchievementCategory achievementCategory) {
        achievementCategoryMapper.updateAchievementCategory(achievementCategory);
    }

    @Override
    public void deleteAchievementCategory(Integer id) {
        achievementCategoryMapper.deleteAchievementCategory(id);
    }
} 