package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RuleAchievementLogMapper;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RuleAchievementLogServiceImpl implements RuleAchievementLogService {

    @Autowired
    private RuleAchievementLogMapper ruleAchievementLogMapper;

    @Override
    public RuleAchievementLog getById(Integer id) {
        return ruleAchievementLogMapper.getById(id);
    }

    @Override
    public List<RuleAchievementLog> getByMemberId(Integer mid) {
        return ruleAchievementLogMapper.getByMemberId(mid);
    }

    @Override
    public List<RuleAchievementLog> getByAchievementId(Integer raId) {
        return ruleAchievementLogMapper.getByAchievementId(raId);
    }

    @Override
    public int insert(RuleAchievementLog log) {
        return ruleAchievementLogMapper.insert(log);
    }

    @Override
    public int deleteById(Integer id) {
        return ruleAchievementLogMapper.deleteById(id);
    }

    @Override
    public int deleteByAchievementAndMember(Integer raId, Integer mid) {
        return ruleAchievementLogMapper.deleteByAchievementAndMember(raId, mid);
    }

    @Override
    public List<Map<String, Object>> getMemberAchievements(Integer mid) {
        return ruleAchievementLogMapper.getMemberAchievements(mid);
    }
} 