package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonRuleAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeasonRuleAchievementMapper {
    /**
     * 根据ID和SeasonID获取赛季成就
     * @param id 成就ID
     * @param seasonId 赛季ID
     * @return 赛季成就
     */
    SeasonRuleAchievement getByIdAndSeasonId(@Param("id") Long id, @Param("seasonId") Long seasonId);
    
    /**
     * 获取某赛季下的所有成就
     * @param seasonId 赛季ID
     * @return 成就列表
     */
    List<SeasonRuleAchievement> getBySeasonId(Long seasonId);
    
    /**
     * 获取某赛季下某规则的所有成就
     * @param seasonId 赛季ID
     * @param ruleId 规则ID
     * @return 成就列表
     */
    List<SeasonRuleAchievement> getBySeasonIdAndRuleId(@Param("seasonId") Long seasonId, @Param("ruleId") Integer ruleId);
    
    /**
     * 获取某赛季下某规则的所有有效成就
     * @param seasonId 赛季ID
     * @param ruleId 规则ID
     * @return 成就列表
     */
    List<SeasonRuleAchievement> listActiveBySeasonIdAndRuleId(@Param("seasonId") Long seasonId, @Param("ruleId") Integer ruleId);
    
    /**
     * 获取所有赛季成就
     * @return 成就列表
     */
    List<SeasonRuleAchievement> listAll();
    
    /**
     * 插入赛季成就
     * @param seasonRuleAchievement 成就对象
     * @return 影响行数
     */
    int insert(SeasonRuleAchievement seasonRuleAchievement);
    
    /**
     * 更新赛季成就
     * @param seasonRuleAchievement 成就对象
     * @return 影响行数
     */
    int update(SeasonRuleAchievement seasonRuleAchievement);
    
    /**
     * 删除赛季成就（逻辑删除，修改status为1）
     * @param id 成就ID
     * @param seasonId 赛季ID
     * @return 影响行数
     */
    int deleteByIdAndSeasonId(@Param("id") Long id, @Param("seasonId") Long seasonId);
    
    /**
     * 根据赛季ID删除所有成就
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteBySeasonId(Long seasonId);
} 