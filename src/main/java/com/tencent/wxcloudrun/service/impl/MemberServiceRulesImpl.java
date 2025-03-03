package com.tencent.wxcloudrun.service.impl;

import cn.hutool.core.date.DateTime;
import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.service.MemberRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;

@Service
public class MemberServiceRulesImpl implements MemberRulesService {

    final MemberRulesMapper memberRulesMapper;
    final MemberPointLogsMapper memberPointLogsMapper;
    //构造函数注入
    public MemberServiceRulesImpl(@Autowired MemberRulesMapper memberRulesMapper,@Autowired MemberPointLogsMapper memberPointLogsMapper) {
        this.memberRulesMapper = memberRulesMapper;
        this.memberPointLogsMapper = memberPointLogsMapper;
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
        memberRules.setSort(memberRuleRequest.getSort());
        memberRules.setQuickScore(memberRuleRequest.getQuickScore());
        memberRules.setTypeSort(memberRuleRequest.getTypeSort());
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
        memberPointLogsMapper.delete(Long.valueOf(id));
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
        memberRules.setSort(memberRuleRequest.getSort());
        memberRules.setQuickScore(memberRuleRequest.getQuickScore());
        memberRules.setStatus(1);
        memberRules.setTypeSort(memberRuleRequest.getTypeSort());
        memberRulesMapper.updateRuleById(memberRules);
    }

    @Override
    public List<MemberRules> insertBatch(List<MemberRuleRequest> memberRuleRequests) {
        List<MemberRules> list = new ArrayList();
        for(MemberRuleRequest memberRuleRequest : memberRuleRequests){
            List<MemberRules> rules = memberRulesMapper.getRuleByNameAndMids(memberRuleRequest.getName(), memberRuleRequest.getMid());
            if(rules.isEmpty()){
                if(memberRuleRequest.getType().equals("学习")){
                    memberRuleRequest.setTypeSort(0);
                }else if(memberRuleRequest.getType().equals("独立")){
                    memberRuleRequest.setTypeSort(1);
                }else if(memberRuleRequest.getType().equals("生活")){
                    memberRuleRequest.setTypeSort(2);
                }else if(memberRuleRequest.getType().equals("表扬")){
                    memberRuleRequest.setTypeSort(3);
                }else if(memberRuleRequest.getType().equals("兴趣")){
                    memberRuleRequest.setTypeSort(4);
                }else if(memberRuleRequest.getType().equals("劳动")){
                    memberRuleRequest.setTypeSort(5);
                }else if(memberRuleRequest.getType().equals("批评")){
                    memberRuleRequest.setTypeSort(6);
                }
                MemberRules memberRules = insert(memberRuleRequest);
                list.add(memberRules);
            }
        }
        return list;
    }

    @Override
    public void updateRule(MemberRules rule) {
        memberRulesMapper.updateRuleById(rule);
    }

    @Override
    public List<MemberRules> getActiveRulesByMid(Integer mId) {
        return memberRulesMapper.getActiveRulesByMid(mId);
    }

    @Override
    public void swapRuleSort(Integer currentId, Integer targetId) {
        MemberRules currentRule = memberRulesMapper.getRuleById(currentId);
        MemberRules targetRule = memberRulesMapper.getRuleById(targetId);
        
        if (currentRule == null || targetRule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        memberRulesMapper.swapRuleSort(currentId, targetId, currentRule.getSort(), targetRule.getSort());
    }

    @Override
    public MemberRules getLastSortByTypeAndMid(Integer mid, String type) {
        return memberRulesMapper.getLastSortByTypeAndMid(mid,type);
    }

    @Override
    public int updateRuleType(Integer mid, String oldType, String newType) {
        if (mid == null || oldType == null || newType == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return memberRulesMapper.updateRuleTypeByMid(mid, oldType, newType);
    }

    @Override
    public List<Map<String, Integer>> getRuleTypes(Integer mid) {
        if (mid == null) {
            throw new IllegalArgumentException("会员ID不能为空");
        }
        return memberRulesMapper.getRuleTypesByMid(mid);
    }
}
