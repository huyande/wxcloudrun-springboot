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
}
