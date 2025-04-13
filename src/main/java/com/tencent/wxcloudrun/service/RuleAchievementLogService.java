package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.RuleAchievementLog;
import java.util.List;

public interface RuleAchievementLogService {
    RuleAchievementLog getById(Integer id);
    List<RuleAchievementLog> getByMemberId(Integer mid);
    List<RuleAchievementLog> getByAchievementId(Integer raId);
    int insert(RuleAchievementLog log);
    int deleteById(Integer id);
    int deleteByAchievementAndMember(Integer raId, Integer mid);
} 