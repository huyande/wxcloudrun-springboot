package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dao.RuleAchievementLogMapper;
import com.tencent.wxcloudrun.dao.RuleAchievementMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleAchievementMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleAchievementLogMapper;
import com.tencent.wxcloudrun.dao.SeasonPointLogMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleMapper;
import com.tencent.wxcloudrun.model.SeasonRule;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.model.RuleAchievement;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.model.SeasonRuleAchievement;
import com.tencent.wxcloudrun.model.SeasonRuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementService;
import com.tencent.wxcloudrun.dto.AchievementStatusDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RuleAchievementServiceImpl implements RuleAchievementService {

    private final RuleAchievementMapper ruleAchievementMapper;
    private final SeasonRuleAchievementMapper seasonRuleAchievementMapper;
    private final RuleAchievementLogMapper ruleAchievementLogMapper;
    private final MemberPointLogsMapper memberPointLogsMapper;
    private final MemberRulesMapper memberRulesMapper;
    private final SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper;
    private final SeasonPointLogMapper seasonPointLogMapper;
    private final SeasonRuleMapper seasonRuleMapper;
    @Autowired
    public RuleAchievementServiceImpl(RuleAchievementMapper ruleAchievementMapper,
                                      SeasonRuleAchievementMapper seasonRuleAchievementMapper,
                                      RuleAchievementLogMapper ruleAchievementLogMapper,
                                      MemberPointLogsMapper memberPointLogsMapper,
                                      MemberRulesMapper memberRulesMapper,
                                      SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper,
                                      SeasonPointLogMapper seasonPointLogMapper,
                                      SeasonRuleMapper seasonRuleMapper) {
        this.ruleAchievementMapper = ruleAchievementMapper;
        this.seasonRuleAchievementMapper = seasonRuleAchievementMapper;
        this.ruleAchievementLogMapper = ruleAchievementLogMapper;
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.memberRulesMapper = memberRulesMapper;
        this.seasonRuleAchievementLogMapper = seasonRuleAchievementLogMapper;
        this.seasonPointLogMapper = seasonPointLogMapper;
        this.seasonRuleMapper = seasonRuleMapper;
    }

    @Override
    public <T> T getById(Integer id, Long seasonId, Class<T> expectedType) {
        if (id == null) return null;
        if (seasonId != null) {
            SeasonRuleAchievement seasonEntity = seasonRuleAchievementMapper.getByIdAndSeasonId(id.longValue(), seasonId);
            if (seasonEntity != null) {
                 if (expectedType.isAssignableFrom(SeasonRuleAchievement.class)) {
                    return expectedType.cast(seasonEntity);
                }
            }
            return null;
        } else {
            RuleAchievement entity = ruleAchievementMapper.getById(id);
            if (entity != null && expectedType.isAssignableFrom(RuleAchievement.class)) {
                return expectedType.cast(entity);
            }
            return null;
        }
    }

    @Override
    public <T> List<T> list(Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            List<SeasonRuleAchievement> seasonEntities = seasonRuleAchievementMapper.getBySeasonId(seasonId);
             if (expectedType.isAssignableFrom(SeasonRuleAchievement.class)) {
                return seasonEntities.stream().map(expectedType::cast).collect(Collectors.toList());
            }
            throw new IllegalArgumentException("Invalid expected type for season list: " + expectedType.getName());
        } else {
            List<RuleAchievement> entities = ruleAchievementMapper.list();
            if (expectedType.isAssignableFrom(RuleAchievement.class)) {
                return entities.stream().map(expectedType::cast).collect(Collectors.toList());
            }
            throw new IllegalArgumentException("Invalid expected type for regular list: " + expectedType.getName());
        }
    }

    @Override
    public <T> List<T> getByRuleId(Integer ruleId, Long seasonId, Class<T> expectedType) {
        if (ruleId == null) return new ArrayList<>();
        if (seasonId != null) {
            List<SeasonRuleAchievement> seasonEntities = seasonRuleAchievementMapper.getBySeasonIdAndRuleId(seasonId, ruleId);
             if (expectedType.isAssignableFrom(SeasonRuleAchievement.class)) {
                return seasonEntities.stream().map(expectedType::cast).collect(Collectors.toList());
            }
             throw new IllegalArgumentException("Invalid expected type for season list by ruleId: " + expectedType.getName());
        } else {
            List<RuleAchievement> entities = ruleAchievementMapper.getByRuleId(ruleId);
             if (expectedType.isAssignableFrom(RuleAchievement.class)) {
                return entities.stream().map(expectedType::cast).collect(Collectors.toList());
            }
            throw new IllegalArgumentException("Invalid expected type for regular list by ruleId: " + expectedType.getName());
        }
    }

    @Override
    @Transactional
    public Object insert(Object achievementData, Long seasonId) {
        if (seasonId != null) {
            SeasonRuleAchievement seasonEntity = new SeasonRuleAchievement();
            BeanUtils.copyProperties(achievementData, seasonEntity);
            if (achievementData instanceof RuleAchievement && ((RuleAchievement) achievementData).getId() != null) {
                
            }
            seasonEntity.setSeasonId(seasonId);
            seasonEntity.setId(null);
            seasonEntity.setStatus(0);

            seasonRuleAchievementMapper.insert(seasonEntity);
            return seasonEntity;
        } else {
            if (!(achievementData instanceof RuleAchievement)) {
                throw new IllegalArgumentException("Data must be of type RuleAchievement for non-seasonal insert.");
            }
            RuleAchievement entity = (RuleAchievement) achievementData;
            entity.setId(null);

            ruleAchievementMapper.insert(entity);
            return entity;
        }
    }

    @Override
    @Transactional
    public Object update(Integer id, Object achievementData, Long seasonId) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null for update.");
        if (seasonId != null) {
            SeasonRuleAchievement seasonEntity = seasonRuleAchievementMapper.getByIdAndSeasonId(id.longValue(), seasonId);
            if (seasonEntity == null) {
                throw new RuntimeException("SeasonRuleAchievement not found with id: " + id + " for seasonId: " + seasonId);
            }
            BeanUtils.copyProperties(achievementData, seasonEntity, "id", "seasonId");
            
            int updatedCount = seasonRuleAchievementMapper.update(seasonEntity);
            return (updatedCount > 0) ? seasonEntity : null;
        } else {
            RuleAchievement entity = ruleAchievementMapper.getById(id);
            if (entity == null) {
                throw new RuntimeException("RuleAchievement not found with id: " + id);
            }
            if (!(achievementData instanceof RuleAchievement)) {
                throw new IllegalArgumentException("Data must be of type RuleAchievement for non-seasonal update.");
            }
            BeanUtils.copyProperties(achievementData, entity, "id");

            int updatedCount = ruleAchievementMapper.update(entity);
            return (updatedCount > 0) ? entity : null;
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id, Long seasonId) {
        if (id == null) return;
        if (seasonId != null) {
            seasonRuleAchievementMapper.deleteByIdAndSeasonId(id.longValue(), seasonId);
        } else {
            ruleAchievementMapper.deleteById(id);
        }
    }
    
    @Override
    public <T> List<T> checkAchievementRules(Integer ruleId, Integer mid,String remark, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            SeasonRule seasonRule = seasonRuleMapper.getById(ruleId.longValue());
            if (seasonRule == null || seasonRule.getIsAchievement() == null || seasonRule.getIsAchievement() == 0) {
                return new ArrayList<>();
            }

            if (!expectedType.isAssignableFrom(SeasonRuleAchievement.class)) {
                throw new IllegalArgumentException("Invalid expected type for seasonal checkAchievementRules: " + expectedType.getName());
            }

            List<SeasonRuleAchievement> resultEntities = new ArrayList<>();
            List<SeasonRuleAchievement> seasonAchievements = seasonRuleAchievementMapper.getBySeasonIdAndRuleId(seasonId, ruleId);

            for (SeasonRuleAchievement achievement : seasonAchievements) {
                SeasonRuleAchievementLog existingLog = seasonRuleAchievementLogMapper.getBySeasonIdAndSraIdAndMid(seasonId, achievement.getId(), mid);
                if (existingLog != null) {
                    continue;
                }

                boolean isAchieved = false;
                switch (achievement.getConditionType()) {
                    case "连续":
                        Integer consecutiveDays = calculateConsecutiveCheckIns(mid, ruleId, achievement.getConditionValue(), seasonId);
                        if (consecutiveDays != null && consecutiveDays >= achievement.getConditionValue()) {
                            isAchieved = true;
                        }
                        break;
                    case "累计":
                        Integer cumulativeCount = seasonPointLogMapper.getPointLogsCountBySeasonIdMidAndRuleId(seasonId, mid, ruleId.longValue());
                        if (cumulativeCount != null && cumulativeCount >= achievement.getConditionValue()) {
                            isAchieved = true;
                        }
                        break;
                    case "积分":
                        Map<String, Object> pointsData = seasonPointLogMapper.getPointSumBySeasonIdMidAndRuleId(seasonId, mid, ruleId.longValue());
                        if (pointsData != null && pointsData.get("pointSum") != null) {
                            Integer pointsSum = ((Number) pointsData.get("pointSum")).intValue();
                            if (pointsSum >= achievement.getConditionValue()) {
                                isAchieved = true;
                            }
                        }
                        break;
                    case "备注":
                        if(achievement.getConditionDesc()!=null && achievement.getConditionDesc().equals(remark)){
                            isAchieved = true;
                        }
                        break;
                }

                if (isAchieved) {
                    resultEntities.add(achievement);
                    SeasonRuleAchievementLog newLog = new SeasonRuleAchievementLog();
                    newLog.setMid(mid);
                    newLog.setSraId(achievement.getId());
                    newLog.setSeasonId(seasonId);
                    newLog.setRewardValue(achievement.getRewardValue());
                    seasonRuleAchievementLogMapper.insert(newLog);
                }
            }
            return resultEntities.stream().map(expectedType::cast).collect(Collectors.toList());

        } else {
            MemberRules memberRule = memberRulesMapper.getRuleById(ruleId);
            if(memberRule!=null && memberRule.getIsAchievement() !=null && memberRule.getIsAchievement()==0){
                return new ArrayList<>();
            }
            List<RuleAchievement> resultEntities = new ArrayList<>();
            
            List<RuleAchievement> achievements = ruleAchievementMapper.getByRuleId(ruleId);
            
            for (RuleAchievement achievement : achievements) {
                if (ruleAchievementLogMapper.getByAchievementAndMember(achievement.getId(), mid) != null) {
                    continue;
                }
                boolean isAchieved = false;
                
                switch (achievement.getConditionType()) {
                    case "连续":
                        Integer consecutiveDays = calculateConsecutiveCheckIns(mid, ruleId, achievement.getConditionValue(), null);
                        if (consecutiveDays >= achievement.getConditionValue()) {
                            isAchieved = true;
                        }
                        break;
                    case "累计":
                        Integer cumulativeCount = memberPointLogsMapper.getPointLogsCountByMidAndRuleId(mid, ruleId);
                        if (cumulativeCount != null && cumulativeCount >= achievement.getConditionValue()) {
                            isAchieved = true;
                        }
                        break;
                    case "积分":
                        Integer pointsSum = memberPointLogsMapper.getPointSumByMidAndRuleId(mid, ruleId);
                        if (pointsSum != null && pointsSum >= achievement.getConditionValue()) {
                            isAchieved = true;
                        }
                        break;
                    case "备注":
                        if(achievement.getConditionDesc()!=null && achievement.getConditionDesc().equals(remark)){
                            isAchieved = true;
                        }
                        break;
                }
                
                if (isAchieved) {
                    resultEntities.add(achievement);
                    RuleAchievementLog ruleAchievementLog = new RuleAchievementLog();
                    ruleAchievementLog.setMid(mid);
                    ruleAchievementLog.setRaId(achievement.getId());
                    ruleAchievementLog.setRewardValue(achievement.getRewardValue());
                    ruleAchievementLogMapper.insert(ruleAchievementLog);
                }
            }
            if (expectedType.isAssignableFrom(RuleAchievement.class)) {
                 return resultEntities.stream().map(expectedType::cast).collect(Collectors.toList());
            }
            throw new IllegalArgumentException("Invalid expected type for non-seasonal checkAchievementRules: " + expectedType.getName());
        }
    }
    
    /**
     * 计算连续打卡天数 - extended for potential seasonality
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @param conditionValue 目标连续天数
     * @param seasonId 赛季ID (null for non-seasonal)
     * @return 实际连续打卡天数
     */
    private Integer calculateConsecutiveCheckIns(Integer mid, Integer ruleId, Integer conditionValue, Long seasonId) {
        LocalDateTime endDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime startDate = endDate.minusDays(conditionValue - 1).withHour(0).withMinute(0).withSecond(0);
        
        if (seasonId != null) {
            Integer checkInCount = seasonPointLogMapper.getCheckInCountByMidAndRuleIdAndSeasonIdAndDateRange(seasonId,mid, ruleId, startDate, endDate);
            return checkInCount;
        } else {
            return memberPointLogsMapper.getCheckInCountBetweenDates(mid, ruleId, startDate, endDate);
        }
    }

    /**
     * 计算连续打卡天数（旧方法，保留作为参考）
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 当前连续打卡天数
     */
    private Integer calculateConsecutiveCheckInsOld(Integer mid, Integer ruleId) {
        List<MemberPointLogs> checkInRecords = memberPointLogsMapper.getCheckInRecords(mid, ruleId);
        if (checkInRecords == null || checkInRecords.isEmpty()) {
            return 0;
        }
        int consecutiveDays = 1;
        int maxConsecutiveDays = 1;
        for (int i = 1; i < checkInRecords.size(); i++) {
            LocalDateTime prevDate = checkInRecords.get(i-1).getCreatedAt();
            LocalDateTime currentDate = checkInRecords.get(i).getCreatedAt();
            if (prevDate.toLocalDate().plusDays(1).equals(currentDate.toLocalDate())) {
                consecutiveDays++;
            } else {
                consecutiveDays = 1;
            }
            maxConsecutiveDays = Math.max(maxConsecutiveDays, consecutiveDays);
        }
        return maxConsecutiveDays;
    }
    
    @Override
    public List<AchievementStatusDto> getAchievementStatusByRuleId(Integer ruleId, Integer mid, Long seasonId) {
        List<AchievementStatusDto> result = new ArrayList<>();
        
        if (seasonId != null) {
            // 赛季模式
            List<SeasonRuleAchievement> achievements = seasonRuleAchievementMapper.getBySeasonIdAndRuleId(seasonId, ruleId);
            
            for (SeasonRuleAchievement achievement : achievements) {
                AchievementStatusDto dto = new AchievementStatusDto();
                
                // 设置基本信息
                dto.setId(achievement.getId());
                dto.setRuleId(achievement.getRuleId());
                dto.setTitle(achievement.getTitle());
                dto.setImg(achievement.getImg());
                dto.setConditionType(achievement.getConditionType());
                dto.setConditionValue(achievement.getConditionValue());
                dto.setConditionDesc(achievement.getConditionDesc());
                dto.setRewardType(achievement.getRewardType());
                dto.setRewardValue(achievement.getRewardValue());
                
                // 检查是否完成
                SeasonRuleAchievementLog log = seasonRuleAchievementLogMapper.getBySeasonIdAndSraIdAndMid(seasonId, achievement.getId(), mid);
                dto.setIsCompleted(log != null);
                if (log != null) {
                    dto.setCompletedAt(log.getCreatedAt());
                }
                
                // 计算当前进度和剩余进度
                calculateSeasonProgress(dto, achievement, mid, seasonId);
                
                result.add(dto);
            }
        } else {
            // 常规模式
            List<RuleAchievement> achievements = ruleAchievementMapper.getByRuleId(ruleId);
            
            for (RuleAchievement achievement : achievements) {
                AchievementStatusDto dto = new AchievementStatusDto();
                
                // 设置基本信息
                dto.setId(achievement.getId().longValue());
                dto.setRuleId(achievement.getRuleId());
                dto.setTitle(achievement.getTitle());
                dto.setImg(achievement.getImg());
                dto.setConditionType(achievement.getConditionType());
                dto.setConditionValue(achievement.getConditionValue());
                dto.setConditionDesc(achievement.getConditionDesc());
                dto.setRewardType(achievement.getRewardType());
                dto.setRewardValue(achievement.getRewardValue());
                
                // 检查是否完成
                RuleAchievementLog log = ruleAchievementLogMapper.getByAchievementAndMember(achievement.getId(), mid);
                dto.setIsCompleted(log != null);
                if (log != null) {
                    dto.setCompletedAt(log.getCreatedAt());
                }
                
                // 计算当前进度和剩余进度
                calculateRegularProgress(dto, achievement, mid);
                
                result.add(dto);
            }
        }
        
        return result;
    }
    
    /**
     * 计算赛季模式下的进度
     */
    private void calculateSeasonProgress(AchievementStatusDto dto, SeasonRuleAchievement achievement, Integer mid, Long seasonId) {
        String conditionType = achievement.getConditionType();
        Integer conditionValue = achievement.getConditionValue();
        
        switch (conditionType) {
            case "连续":
                Integer consecutiveDays = calculateConsecutiveCheckIns(mid, achievement.getRuleId(), conditionValue, seasonId);
                dto.setCurrentProgress(consecutiveDays != null ? consecutiveDays : 0);
                dto.setProgressDesc("当前连续" + dto.getCurrentProgress() + "天");
                if (!dto.getIsCompleted()) {
                    dto.setRemainingProgress(Math.max(0, conditionValue - dto.getCurrentProgress()));
                    dto.setRemainingDesc("还需连续" + dto.getRemainingProgress() + "天");
                }
                break;
                
            case "累计":
                Integer cumulativeCount = seasonPointLogMapper.getPointLogsCountBySeasonIdMidAndRuleId(seasonId, mid, achievement.getRuleId().longValue());
                dto.setCurrentProgress(cumulativeCount != null ? cumulativeCount : 0);
                dto.setProgressDesc("当前累计" + dto.getCurrentProgress() + "次");
                if (!dto.getIsCompleted()) {
                    dto.setRemainingProgress(Math.max(0, conditionValue - dto.getCurrentProgress()));
                    dto.setRemainingDesc("还需" + dto.getRemainingProgress() + "次");
                }
                break;
                
            case "积分":
                Map<String, Object> pointsData = seasonPointLogMapper.getPointSumBySeasonIdMidAndRuleId(seasonId, mid, achievement.getRuleId().longValue());
                Integer pointsSum = 0;
                if (pointsData != null && pointsData.get("pointSum") != null) {
                    pointsSum = ((Number) pointsData.get("pointSum")).intValue();
                }
                dto.setCurrentProgress(pointsSum);
                dto.setProgressDesc("当前累计" + pointsSum + "积分");
                if (!dto.getIsCompleted()) {
                    dto.setRemainingProgress(Math.max(0, conditionValue - pointsSum));
                    dto.setRemainingDesc("还需" + dto.getRemainingProgress() + "积分");
                }
                break;
                
            case "备注":
                if (achievement.getConditionDesc() != null) {
                    dto.setCurrentProgress(dto.getIsCompleted() ? 1 : 0);
                    dto.setProgressDesc(dto.getIsCompleted() ? "已匹配关键字" : "未匹配关键字");
                    if (!dto.getIsCompleted()) {
                        dto.setRemainingProgress(1);
                        dto.setRemainingDesc("需要匹配关键字: " + achievement.getConditionDesc());
                    }
                } else {
                    dto.setCurrentProgress(0);
                    dto.setProgressDesc("无条件设置");
                    dto.setRemainingProgress(0);
                    dto.setRemainingDesc("无条件设置");
                }
                break;
                
            default:
                dto.setCurrentProgress(0);
                dto.setProgressDesc("未知条件类型");
                dto.setRemainingProgress(0);
                dto.setRemainingDesc("未知条件类型");
                break;
        }
    }
    
    /**
     * 计算常规模式下的进度
     */
    private void calculateRegularProgress(AchievementStatusDto dto, RuleAchievement achievement, Integer mid) {
        String conditionType = achievement.getConditionType();
        Integer conditionValue = achievement.getConditionValue();
        
        switch (conditionType) {
            case "连续":
                Integer consecutiveDays = calculateConsecutiveCheckIns(mid, achievement.getRuleId(), conditionValue, null);
                dto.setCurrentProgress(consecutiveDays != null ? consecutiveDays : 0);
                dto.setProgressDesc("当前连续" + dto.getCurrentProgress() + "天");
                if (!dto.getIsCompleted()) {
                    dto.setRemainingProgress(Math.max(0, conditionValue - dto.getCurrentProgress()));
                    dto.setRemainingDesc("还需连续" + dto.getRemainingProgress() + "天");
                }
                break;
                
            case "累计":
                Integer cumulativeCount = memberPointLogsMapper.getPointLogsCountByMidAndRuleId(mid, achievement.getRuleId());
                dto.setCurrentProgress(cumulativeCount != null ? cumulativeCount : 0);
                dto.setProgressDesc("当前累计" + dto.getCurrentProgress() + "次");
                if (!dto.getIsCompleted()) {
                    dto.setRemainingProgress(Math.max(0, conditionValue - dto.getCurrentProgress()));
                    dto.setRemainingDesc("还需" + dto.getRemainingProgress() + "次");
                }
                break;
                
            case "积分":
                Integer pointsSum = memberPointLogsMapper.getPointSumByMidAndRuleId(mid, achievement.getRuleId());
                dto.setCurrentProgress(pointsSum != null ? pointsSum : 0);
                dto.setProgressDesc("当前累计" + dto.getCurrentProgress() + "积分");
                if (!dto.getIsCompleted()) {
                    dto.setRemainingProgress(Math.max(0, conditionValue - dto.getCurrentProgress()));
                    dto.setRemainingDesc("还需" + dto.getRemainingProgress() + "积分");
                }
                break;
                
            case "备注":
                if (achievement.getConditionDesc() != null) {
                    dto.setCurrentProgress(dto.getIsCompleted() ? 1 : 0);
                    dto.setProgressDesc(dto.getIsCompleted() ? "已匹配关键字" : "未匹配关键字");
                    if (!dto.getIsCompleted()) {
                        dto.setRemainingProgress(1);
                        dto.setRemainingDesc("需要匹配关键字: " + achievement.getConditionDesc());
                    }
                } else {
                    dto.setCurrentProgress(0);
                    dto.setProgressDesc("无条件设置");
                    dto.setRemainingProgress(0);
                    dto.setRemainingDesc("无条件设置");
                }
                break;
                
            default:
                dto.setCurrentProgress(0);
                dto.setProgressDesc("未知条件类型");
                dto.setRemainingProgress(0);
                dto.setRemainingDesc("未知条件类型");
                break;
        }
    }
} 