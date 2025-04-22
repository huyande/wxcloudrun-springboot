package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.RuleAchievement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RuleAchievementMapper {
    RuleAchievement getById(Integer id);
    List<RuleAchievement> list();
    List<RuleAchievement> getByRuleId(Integer ruleId);
    int insert(RuleAchievement ruleAchievement);
    int update(RuleAchievement ruleAchievement);
    void deleteById(Integer id);
} 