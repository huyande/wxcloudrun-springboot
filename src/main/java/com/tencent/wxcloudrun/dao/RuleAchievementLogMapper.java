package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.RuleAchievementLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface RuleAchievementLogMapper {
    RuleAchievementLog getById(Integer id);
    List<RuleAchievementLog> getByMemberId(Integer mid);
    List<RuleAchievementLog> getByAchievementId(Integer raId);
    RuleAchievementLog getByAchievementAndMember(Integer raId, Integer mid);
    List<Map<String, Object>> getMemberAchievements(Integer mid);
    int insert(RuleAchievementLog log);
    int deleteById(Integer id);
    int deleteByAchievementAndMember(Integer raId, Integer mid);

    Integer getSumRewardValue(Integer mid);

    void deleteByMId(Integer mid);
} 