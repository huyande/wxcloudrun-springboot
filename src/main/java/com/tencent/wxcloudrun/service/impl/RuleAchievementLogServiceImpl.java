package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RuleAchievementLogDao;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleAchievementLogServiceImpl implements RuleAchievementLogService {

    @Autowired
    private RuleAchievementLogDao ruleAchievementLogDao;

    @Override
    public RuleAchievementLog getById(Integer id) {
        return ruleAchievementLogDao.getById(id);
    }

    @Override
    public List<RuleAchievementLog> getByMemberId(Integer mid) {
        return ruleAchievementLogDao.getByMemberId(mid);
    }

    @Override
    public List<RuleAchievementLog> getByAchievementId(Integer raId) {
        return ruleAchievementLogDao.getByAchievementId(raId);
    }

    @Override
    public int insert(RuleAchievementLog log) {
        return ruleAchievementLogDao.insert(log);
    }

    @Override
    public int deleteById(Integer id) {
        return ruleAchievementLogDao.deleteById(id);
    }

    @Override
    public int deleteByAchievementAndMember(Integer raId, Integer mid) {
        return ruleAchievementLogDao.deleteByAchievementAndMember(raId, mid);
    }
} 