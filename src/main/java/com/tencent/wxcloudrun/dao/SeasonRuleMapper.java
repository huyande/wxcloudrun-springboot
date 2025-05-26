package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SeasonRuleMapper {
    
    /**
     * 根据ID获取赛季规则
     * @param id 规则ID
     * @return 赛季规则
     */
    SeasonRule getById(Long id);
    
    /**
     * 获取某赛季的所有规则
     * @param seasonId 赛季ID
     * @return 规则列表
     */
    List<SeasonRule> getBySeasonId(Long seasonId);
    
    /**
     * 获取某赛季某成员的所有规则
     * @param seasonId 赛季ID
     * @param mId 成员ID
     * @return 规则列表
     */
    List<SeasonRule> getBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mId") Integer mId);
    
    /**
     * 获取某赛季某成员的所有规则，按日期筛选 (类似 MemberController.getRulesByMid)
     * @param seasonId 赛季ID
     * @param mId 成员ID
     * @param day 日期
     * @return 规则列表
     */
    List<SeasonRule> getRulesBySeasonIdAndMidAndDay(@Param("seasonId") Long seasonId, @Param("mId") Integer mId, @Param("day") String day);
    
    /**
     * 获取某赛季某成员的激活规则
     * @param seasonId 赛季ID
     * @param mId 成员ID
     * @return 规则列表
     */
    List<SeasonRule> getActiveRulesBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mId") Integer mId);
    
    /**
     * 根据名称和成员ID获取规则
     * @param name 规则名称
     * @param mId 成员ID
     * @param seasonId 赛季ID
     * @return 规则
     */
    SeasonRule getRuleByNameAndMid(@Param("name") String name, @Param("mId") Integer mId, @Param("seasonId") Long seasonId);
    
    /**
     * 创建规则
     * @param seasonRule 规则
     * @return 影响的行数
     */
    int insert(SeasonRule seasonRule);
    
    /**
     * 批量创建规则
     * @param seasonRules 规则列表
     * @return 影响的行数
     */
    int insertBatch(List<SeasonRule> seasonRules);
    
    /**
     * 更新规则
     * @param seasonRule 规则
     * @return 影响的行数
     */
    int update(SeasonRule seasonRule);
    
    /**
     * 更新规则排序
     * @param id 规则ID
     * @param sort 排序值
     * @return 影响的行数
     */
    int updateSort(@Param("id") Long id, @Param("sort") Integer sort);
    
    /**
     * 更新规则类型
     * @param mId 成员ID
     * @param seasonId 赛季ID
     * @param oldType 旧类型
     * @param newType 新类型
     * @return 影响的行数
     */
    int updateRuleType(@Param("mId") Integer mId, @Param("seasonId") Long seasonId, @Param("oldType") String oldType, @Param("newType") String newType);
    
    /**
     * 删除规则
     * @param id 规则ID
     * @return 影响的行数
     */
    int delete(Long id);
    
    /**
     * 获取规则类型
     * @param mId 成员ID
     * @param seasonId 赛季ID
     * @return 类型列表
     */
    List<Map<String, Integer>> getRuleTypes(@Param("mId") Integer mId, @Param("seasonId") Long seasonId);
    
    /**
     * 获取指定类型的最后一个规则（按排序）
     * @param mId 成员ID
     * @param seasonId 赛季ID
     * @param type 类型
     * @return 规则
     */
    SeasonRule getLastSortByTypeAndMid(@Param("mId") Integer mId, @Param("seasonId") Long seasonId, @Param("type") String type);
    
    /**
     * 根据赛季ID删除所有规则
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteBySeasonId(Long seasonId);

    int countActiveRulesBySeasonIdAndMid(@Param("seasonId")Long seasonId, @Param("mId")Integer mId);
}