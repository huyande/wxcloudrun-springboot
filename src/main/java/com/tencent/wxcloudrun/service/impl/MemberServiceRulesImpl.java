package com.tencent.wxcloudrun.service.impl;

import cn.hutool.core.date.DateTime;
import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.service.MemberRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import cn.hutool.core.date.DateUtil;

@Service
public class MemberServiceRulesImpl implements MemberRulesService {

    final MemberRulesMapper memberRulesMapper;
    //构造函数注入
    public MemberServiceRulesImpl(@Autowired MemberRulesMapper memberRulesMapper) {
        this.memberRulesMapper = memberRulesMapper;
    }

    @Override
    public List<MemberRules> getRulesByMid(Integer mId,String day) {
        DateTime parse = DateUtil.parse(day);
        String dayCh = DateUtil.dayOfWeekEnum(parse).toChinese();
        List<MemberRules> rules = memberRulesMapper.getRulesByMid(mId);
        List<MemberRules> result = new ArrayList<>();
        for(MemberRules rule : rules){
            if(rule.getWeeks().contains(dayCh)){
                result.add(rule);
            }
        }
        return result;
    }

    @Override
    public MemberRules insert(MemberRuleRequest memberRuleRequest) {
        MemberRules memberRules = new MemberRules();
        memberRules.setName(memberRuleRequest.getName());
        memberRules.setType(memberRuleRequest.getType());
        memberRules.setMid(memberRuleRequest.getMid());
        memberRules.setIcon(memberRuleRequest.getIcon());
        memberRules.setIconType(memberRuleRequest.getIconType());
        memberRules.setWeeks(memberRuleRequest.getWeeks());
        memberRules.setContent(memberRuleRequest.getContent());
        memberRulesMapper.insertOne(memberRules);
        return memberRules;
    }

    @Override
    public MemberRules getRuleByNameAndMid(String name, Integer mid) {
        return memberRulesMapper.getRuleByNameAndMid(name, mid);
    }

    @Override
    public MemberRules getRuleById(Integer id) {
        return memberRulesMapper.getRuleById(id);
    }

    @Override
    public void delete(Integer id) {
        memberRulesMapper.delete(id);
    }

    @Override
    public void updateRuleById(Integer id, MemberRuleRequest memberRuleRequest) {
        MemberRules memberRules = new MemberRules();
        memberRules.setId(id);
        memberRules.setName(memberRuleRequest.getName());
        memberRules.setType(memberRuleRequest.getType());
        memberRules.setIcon(memberRuleRequest.getIcon());
        memberRules.setIconType(memberRuleRequest.getIconType());
        memberRules.setWeeks(memberRuleRequest.getWeeks());
        memberRules.setContent(memberRuleRequest.getContent());
        memberRulesMapper.updateRuleById(memberRules);
    }

}
