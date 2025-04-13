package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RuleAchievementDao;
import com.tencent.wxcloudrun.model.RuleAchievement;
import com.tencent.wxcloudrun.service.RuleAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleAchievementServiceImpl implements RuleAchievementService {

    @Autowired
    private RuleAchievementDao ruleAchievementDao;

    @Override
    public RuleAchievement getById(Integer id) {
        return ruleAchievementDao.getById(id);
    }

    @Override
    public List<RuleAchievement> list() {
        return ruleAchievementDao.list();
    }

    @Override
    public List<RuleAchievement> getByRuleId(Integer ruleId) {
        return ruleAchievementDao.getByRuleId(ruleId);
    }

    @Override
    public int insert(RuleAchievement ruleAchievement) {
        return ruleAchievementDao.insert(ruleAchievement);
    }

    @Override
    public int update(RuleAchievement ruleAchievement) {
        return ruleAchievementDao.update(ruleAchievement);
    }

    @Override
    public void deleteById(Integer id) {
        ruleAchievementDao.deleteById(id);
    }
} 