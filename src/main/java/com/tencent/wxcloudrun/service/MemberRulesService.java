package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;

import java.util.List;
import java.util.Map;

/**
 * 会员规则服务接口
 * 支持常规模式和赛季模式
 */
public interface MemberRulesService {

    /**
     * 获取指定mid的规则列表
     * 
     * @param mId 会员ID
     * @param day 日期
     * @param seasonId 赛季ID，如果为null则返回常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 规则列表
     */
    <T> List<T> getRulesByMid(Integer mId, String day, Long seasonId, Class<T> expectedType);
    
    /**
     * 新增规则
     * 
     * @param memberRuleRequest 规则请求
     * @param seasonId 赛季ID，如果为null则创建常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 创建的规则
     */
    <T> T insert(MemberRuleRequest memberRuleRequest, Long seasonId, Class<T> expectedType);

    /**
     * 根据名称和mid获取规则
     * 
     * @param name 规则名称
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 规则
     */
    <T> T getRuleByNameAndMid(String name, Integer mid, Long seasonId, Class<T> expectedType);

    /**
     * 根据ID获取规则
     * 
     * @param id 规则ID
     * @param seasonId 赛季ID，如果为null则获取常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 规则
     */
    <T> T getRuleById(Integer id, Long seasonId, Class<T> expectedType);

    /**
     * 删除规则
     * 
     * @param id 规则ID
     * @param seasonId 赛季ID，如果为null则删除常规规则
     */
    void delete(Integer id, Long seasonId);

    /**
     * 根据ID更新规则
     * 
     * @param id 规则ID
     * @param memberRuleRequest 规则请求
     * @param seasonId 赛季ID，如果为null则更新常规规则
     */
    void updateRuleById(Integer id, MemberRuleRequest memberRuleRequest, Long seasonId);

    /**
     * 批量新增规则
     * 
     * @param memberRuleRequests 规则请求列表
     * @param seasonId 赛季ID，如果为null则创建常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 创建的规则列表
     */
    <T> List<T> insertBatch(List<MemberRuleRequest> memberRuleRequests, Long seasonId, Class<T> expectedType);

    /**
     * 更新规则
     * 
     * @param rule 规则
     * @param seasonId 赛季ID，如果为null则更新常规规则
     */
    void updateRule(MemberRules rule, Long seasonId);

    /**
     * 获取指定mid的所有活动规则（status=1）
     * 
     * @param mId 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 活动规则列表
     */
    <T> List<T> getActiveRulesByMid(Integer mId, Long seasonId, Class<T> expectedType);

    /**
     * 获取指定mid的活跃规则数量
     * 
     * @param mId 会员ID
     * @param seasonId 赛季ID，如果为null则统计常规规则
     * @return 活跃规则数量
     */
    int countActiveRulesByMid(Integer mId, Long seasonId);

    /**
     * 检查是否可以创建新规则（规则数量限制）
     * 
     * @param mId 会员ID
     * @param seasonId 赛季ID，如果为null则检查常规规则
     * @return 是否可以创建
     */
    boolean canCreateRule(Integer mId, Long seasonId);

    /**
     * 交换两个规则的排序值
     * 
     * @param currentId 当前规则ID
     * @param targetId 目标规则ID
     * @param seasonId 赛季ID，如果为null则交换常规规则
     */
    void swapRuleSort(Integer currentId, Integer targetId, Long seasonId);

    /**
     * 获取指定类型的最后一个规则（按排序）
     * 
     * @param mid 会员ID
     * @param type 规则类型
     * @param seasonId 赛季ID，如果为null则获取常规规则
     * @param expectedType 期望返回的类型，赛季模式下为SeasonRule.class，常规模式下为MemberRules.class
     * @return 最后一个规则
     */
    <T> T getLastSortByTypeAndMid(Integer mid, String type, Long seasonId, Class<T> expectedType);

    /**
     * 批量更新规则分类名称
     * 
     * @param mid 会员ID
     * @param oldType 旧分类名称
     * @param newType 新分类名称
     * @param seasonId 赛季ID，如果为null则更新常规规则
     * @return 更新的规则数量
     */
    int updateRuleType(Integer mid, String oldType, String newType, Long seasonId);

    /**
     * 获取会员的所有规则分类
     * 
     * @param mid 会员ID
     * @param seasonId 赛季ID，如果为null则获取常规规则分类
     * @return 分类列表
     */
    List<Map<String, Object>> getRuleTypes(Integer mid, Long seasonId);

    /**
     * 交换两个分类的排序值
     * 
     * @param mid 会员ID
     * @param currentType 当前分类名称
     * @param targetType 目标分类名称
     * @param seasonId 赛季ID，如果为null则交换常规规则分类
     */
    void swapTypeSort(Integer mid, String currentType, String targetType, Long seasonId);
}
