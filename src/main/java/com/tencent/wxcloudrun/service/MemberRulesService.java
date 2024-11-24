package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;

import java.util.List;

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
     * 交换两个规则的排序值
     * @param currentId 当前规则ID
     * @param targetId 目标规则ID
     */
    void swapRuleSort(Integer currentId, Integer targetId);

    MemberRules getLastSortByTypeAndMid(Integer mid, String type);
}
