package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.service.MemberRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@Service
public class MemberServiceRulesImpl implements MemberRulesService {

    final MemberRulesMapper memberRulesMapper;
    //构造函数注入
    public MemberServiceRulesImpl(@Autowired MemberRulesMapper memberRulesMapper) {
        this.memberRulesMapper = memberRulesMapper;
    }

    @Override
    public List<MemberRules> getRulesByMid(Integer mId) {
        return memberRulesMapper.getRulesByMid(mId);
    }

    @Override
    public MemberRules insert(MemberRuleRequest memberRuleRequest) {
        MemberRules memberRules = new MemberRules();
        memberRules.setName(memberRuleRequest.getName());
        memberRules.setType(memberRuleRequest.getType());
        memberRules.setMid(memberRuleRequest.getMid());
        memberRules.setIcon(memberRuleRequest.getIcon());
        memberRules.setIconType(memberRuleRequest.getIconType());
        memberRulesMapper.insertOne(memberRules);
        return memberRules;
    }

}
