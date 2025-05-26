package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonRuleAchievementLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SeasonRuleAchievementLogMapper {
    /**
     * 根据ID和SeasonID获取赛季成就日志
     * @param id 日志ID
     * @param seasonId 赛季ID
     * @return 赛季成就日志
     */
    SeasonRuleAchievementLog getByIdAndSeasonId(@Param("id") Long id, @Param("seasonId") Long seasonId);
    
    /**
     * 获取某赛季下某成员的所有成就日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 日志列表
     */
    List<SeasonRuleAchievementLog> getBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季下某成就的所有成就日志
     * @param seasonId 赛季ID
     * @param sraId 赛季成就ID
     * @return 日志列表
     */
    List<SeasonRuleAchievementLog> getBySeasonIdAndSraId(@Param("seasonId") Long seasonId, @Param("sraId") Long sraId);
    
    /**
     * 检查某赛季下某成员是否已获得某成就
     * @param seasonId 赛季ID
     * @param sraId 赛季成就ID
     * @param mid 成员ID
     * @return 成就日志，如果未获得则返回null
     */
    SeasonRuleAchievementLog getBySeasonIdAndSraIdAndMid(
            @Param("seasonId") Long seasonId, 
            @Param("sraId") Long sraId, 
            @Param("mid") Integer mid);
    
    /**
     * 获取某赛季下某成员已获得的成就列表（带成就详情）
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 成就详情列表
     */
    List<Map<String, Object>> getMemberSeasonAchievements(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 插入赛季成就日志
     * @param log 日志对象
     * @return 影响行数
     */
    int insert(SeasonRuleAchievementLog log);
    
    /**
     * 删除赛季成就日志
     * @param id 日志ID
     * @param seasonId 赛季ID
     * @return 影响行数
     */
    int deleteByIdAndSeasonId(@Param("id") Long id, @Param("seasonId") Long seasonId);
    
    /**
     * 删除某赛季下某成员某成就的日志
     * @param seasonId 赛季ID
     * @param sraId 赛季成就ID
     * @param mid 成员ID
     * @return 影响行数
     */
    int deleteBySeasonIdAndSraIdAndMid(
            @Param("seasonId") Long seasonId, 
            @Param("sraId") Long sraId, 
            @Param("mid") Integer mid);
    
    /**
     * 获取成员在赛季中获得的奖励总值
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 奖励总值
     */
    Integer getSumRewardValueBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 根据赛季ID删除所有成就日志
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteBySeasonId(Long seasonId);

    Integer getSumRewardValue(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
} 