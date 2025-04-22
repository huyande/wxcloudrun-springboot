package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.RuleAchievement;
import java.util.List;
import java.util.Map;

public interface RuleAchievementService {
    RuleAchievement getById(Integer id);
    List<RuleAchievement> list();
    List<RuleAchievement> getByRuleId(Integer ruleId);
    int insert(RuleAchievement ruleAchievement);
    int update(RuleAchievement ruleAchievement);
    void deleteById(Integer id);
    
    /**
     * 判断是否满足成就规则
     * @param ruleId 规则ID
     * @param mid 成员ID
     * @return 满足的成就列表,包含成就信息和奖励信息
     */
    List<RuleAchievement> checkAchievementRules(Integer ruleId, Integer mid);
} 