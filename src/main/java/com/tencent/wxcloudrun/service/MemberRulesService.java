package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;

import java.util.List;
import java.util.Map;

public interface MemberRulesService {
    List<MemberRules> getRulesByMid(Integer mId,String day);
    MemberRules insert(MemberRuleRequest memberRuleRequest);
    MemberRules getRuleByNameAndMid(String name, Integer mid);
    MemberRules getRuleById(Integer id);
    void delete(Integer id);
    void updateRuleById(Integer id, MemberRuleRequest memberRuleRequest);

    List<MemberRules> insertBatch(List<MemberRuleRequest> memberRuleRequests);

    void updateRule(MemberRules rule);

    /**
     * 获取指定mid的所有活动规则（status=1）
     * @param mId
     * @return
     */
    List<MemberRules> getActiveRulesByMid(Integer mId);
    
    /**
     * 获取指定mid的活跃规则数量
     * @param mId 会员ID
     * @return 规则数量
     */
    int countActiveRulesByMid(Integer mId);
    
    /**
     * 检查是否可以创建新规则
     * @param mId 会员ID
     * @return true可以创建，false不能创建
     */
    boolean canCreateRule(Integer mId);

    /**
     * 交换两个规则的排序值
     * @param currentId 当前规则ID
     * @param targetId 目标规则ID
     */
    void swapRuleSort(Integer currentId, Integer targetId);

    MemberRules getLastSortByTypeAndMid(Integer mid, String type);

    /**
     * 批量更新规则分类名称
     * @param mid 会员ID
     * @param oldType 旧分类名称
     * @param newType 新分类名称
     * @return 更新的记录数
     */
    int updateRuleType(Integer mid, String oldType, String newType);

    /**
     * 获取会员的所有规则分类
     * @param mid 会员ID
     * @return 分类名称列表
     */
    List<Map<String,Integer>> getRuleTypes(Integer mid);
}
