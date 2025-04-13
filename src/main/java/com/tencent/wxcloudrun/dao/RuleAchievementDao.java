package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.RuleAchievement;
import java.util.List;

public interface RuleAchievementDao {
    RuleAchievement getById(Integer id);
    List<RuleAchievement> list();
    List<RuleAchievement> getByRuleId(Integer ruleId);
    int insert(RuleAchievement ruleAchievement);
    int update(RuleAchievement ruleAchievement);
    void deleteById(Integer id);
} 