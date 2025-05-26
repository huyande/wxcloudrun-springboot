package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.RuleAchievementLogMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleAchievementLogMapper;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.model.SeasonRuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RuleAchievementLogServiceImpl implements RuleAchievementLogService {

    @Autowired
    private RuleAchievementLogMapper ruleAchievementLogMapper;

    @Autowired
    private SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper;

    @Override
    public <T> T getById(Integer id, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonRuleAchievementLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for seasonal getById: " + expectedType.getName());
            }
            return expectedType.cast(seasonRuleAchievementLogMapper.getByIdAndSeasonId(id.longValue(), seasonId));
        } else {
            if (!expectedType.isAssignableFrom(RuleAchievementLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for regular getById: " + expectedType.getName());
            }
            return expectedType.cast(ruleAchievementLogMapper.getById(id));
        }
    }

    @Override
    public <T> List<T> getByMemberId(Integer mid, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonRuleAchievementLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for seasonal getByMemberId: " + expectedType.getName());
            }
            List<SeasonRuleAchievementLog> logs = seasonRuleAchievementLogMapper.getBySeasonIdAndMid(seasonId, mid);
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(RuleAchievementLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for regular getByMemberId: " + expectedType.getName());
            }
            List<RuleAchievementLog> logs = ruleAchievementLogMapper.getByMemberId(mid);
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }

    @Override
    public <T> List<T> getByAchievementId(Integer raId, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonRuleAchievementLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for seasonal getByAchievementId: " + expectedType.getName());
            }
            List<SeasonRuleAchievementLog> logs = seasonRuleAchievementLogMapper.getBySeasonIdAndSraId(seasonId, raId.longValue());
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(RuleAchievementLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for regular getByAchievementId: " + expectedType.getName());
            }
            List<RuleAchievementLog> logs = ruleAchievementLogMapper.getByAchievementId(raId);
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }

    @Override
    public List<Map<String, Object>> getMemberAchievements(Integer mid, Long seasonId) {
        if (seasonId != null) {
            return seasonRuleAchievementLogMapper.getMemberSeasonAchievements(seasonId, mid);
        } else {
            return ruleAchievementLogMapper.getMemberAchievements(mid);
        }
    }

    @Override
    @Transactional
    public Object insert(Object log, Long seasonId) {
        if (seasonId != null) {
            if (!(log instanceof SeasonRuleAchievementLog)) {
                SeasonRuleAchievementLog seasonLog = new SeasonRuleAchievementLog();
                BeanUtils.copyProperties(log, seasonLog);
                seasonLog.setSeasonId(seasonId);
                seasonLog.setCreatedAt(LocalDateTime.now());
                seasonLog.setUpdatedAt(LocalDateTime.now());
                seasonRuleAchievementLogMapper.insert(seasonLog);
                return seasonLog;
            } else {
                SeasonRuleAchievementLog seasonLog = (SeasonRuleAchievementLog) log;
                seasonLog.setSeasonId(seasonId);
                seasonLog.setCreatedAt(LocalDateTime.now());
                seasonLog.setUpdatedAt(LocalDateTime.now());
                seasonRuleAchievementLogMapper.insert(seasonLog);
                return seasonLog;
            }
        } else {
            if (!(log instanceof RuleAchievementLog)) {
                RuleAchievementLog regularLog = new RuleAchievementLog();
                BeanUtils.copyProperties(log, regularLog);
                regularLog.setCreatedAt(LocalDateTime.now());
                regularLog.setUpdatedAt(LocalDateTime.now());
                ruleAchievementLogMapper.insert(regularLog);
                return regularLog;
            } else {
                RuleAchievementLog regularLog = (RuleAchievementLog) log;
                regularLog.setCreatedAt(LocalDateTime.now());
                regularLog.setUpdatedAt(LocalDateTime.now());
                ruleAchievementLogMapper.insert(regularLog);
                return regularLog;
            }
        }
    }

    @Override
    @Transactional
    public boolean deleteById(Integer id, Long seasonId) {
        if (seasonId != null) {
            return seasonRuleAchievementLogMapper.deleteByIdAndSeasonId(id.longValue(), seasonId) > 0;
        } else {
            return ruleAchievementLogMapper.deleteById(id) > 0;
        }
    }

    @Override
    @Transactional
    public boolean deleteByAchievementAndMember(Integer raId, Integer mid, Long seasonId) {
        if (seasonId != null) {
            return seasonRuleAchievementLogMapper.deleteBySeasonIdAndSraIdAndMid(seasonId, raId.longValue(), mid) > 0;
        } else {
            return ruleAchievementLogMapper.deleteByAchievementAndMember(raId, mid) > 0;
        }
    }
} 