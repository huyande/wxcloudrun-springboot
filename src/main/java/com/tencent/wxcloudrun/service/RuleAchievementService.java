package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.RuleAchievement;
import java.util.List;

public interface RuleAchievementService {
    <T> T getById(Integer id, Long seasonId, Class<T> expectedType);
    <T> List<T> list(Long seasonId, Class<T> expectedType);
    <T> List<T> getByRuleId(Integer ruleId, Long seasonId, Class<T> expectedType);

    Object insert(Object achievementData, Long seasonId);

    Object update(Integer id, Object achievementData, Long seasonId);

    void deleteById(Integer id, Long seasonId);
    
    /**
     * 判断是否满足成就规则
     * @param ruleId 规则ID
     * @param mid 成员ID
     * @param seasonId 赛季ID (optional)
     * @param expectedType 期望返回的成就类型 (RuleAchievement.class or SeasonRuleAchievement.class)
     * @return 满足的成就列表,包含成就信息和奖励信息
     */
    <T> List<T> checkAchievementRules(Integer ruleId, Integer mid,String remark, Long seasonId, Class<T> expectedType);
    
    /**
     * 根据规则ID查询所有成就配置及完成状态
     * @param ruleId 规则ID
     * @param mid 成员ID
     * @param seasonId 赛季ID (optional)
     * @return 成就状态列表，包含完成状态和进度信息
     */
    List<com.tencent.wxcloudrun.dto.AchievementStatusDto> getAchievementStatusByRuleId(Integer ruleId, Integer mid, Long seasonId);
} 