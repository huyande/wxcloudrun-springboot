package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.RuleAchievementLog;
import java.util.List;
import java.util.Map;

public interface RuleAchievementLogService {
    RuleAchievementLog getById(Integer id);
    List<RuleAchievementLog> getByMemberId(Integer mid);
    List<RuleAchievementLog> getByAchievementId(Integer raId);
    int insert(RuleAchievementLog log);
    int deleteById(Integer id);
    int deleteByAchievementAndMember(Integer raId, Integer mid);
    
    /**
     * 查询成员的成就日志和成就配置数据
     * @param mid 成员ID
     * @return 成就列表,包含成就名称、图片、获取时间等信息
     */
    List<Map<String, Object>> getMemberAchievements(Integer mid);
} 