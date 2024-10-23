package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;

import java.util.List;

public interface MemberRulesService {
    List<MemberRules> getRulesByMid(Integer mId);
    MemberRules insert(MemberRuleRequest memberRuleRequest);
}
