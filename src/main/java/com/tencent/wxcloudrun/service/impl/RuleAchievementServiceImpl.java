package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dao.RuleAchievementLogMapper;
import com.tencent.wxcloudrun.dao.RuleAchievementMapper;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.model.RuleAchievement;
import com.tencent.wxcloudrun.model.RuleAchievementLog;
import com.tencent.wxcloudrun.service.RuleAchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleAchievementServiceImpl implements RuleAchievementService {

    @Autowired
    private RuleAchievementMapper ruleAchievementDao;
    
    @Autowired
    private RuleAchievementLogMapper ruleAchievementLogDao;
    
    @Autowired
    private MemberPointLogsMapper memberPointLogsMapper;

    @Autowired
    private MemberRulesMapper memberRulesMapper;

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
    
    @Override
    public List<RuleAchievement> checkAchievementRules(Integer ruleId, Integer mid) {
        MemberRules memberRule = memberRulesMapper.getRuleById(ruleId);
        if(memberRule!=null && memberRule.getIsAchievement() !=null &&memberRule.getIsAchievement()==0){
            return null;
        }
        List<RuleAchievement> result = new ArrayList<>();
        
        // 获取该规则下的所有有效成就配置
        List<RuleAchievement> achievements = ruleAchievementDao.getByRuleId(ruleId);
        
        for (RuleAchievement achievement : achievements) {
            // 检查是否已经获得过该成就
            if (ruleAchievementLogDao.getByAchievementAndMember(achievement.getId(), mid) != null) {
                continue;
            }
            
            boolean isAchieved = false;
            Integer currentValue = 0;
            
            // 根据成就类型获取当前值并判断是否满足条件
            switch (achievement.getConditionType()) {
                case "连续":
                    // 计算连续打卡天数
                    currentValue = calculateConsecutiveCheckIns(mid, ruleId);
                    // 连续打卡天数达到要求
                    if (currentValue >= achievement.getConditionValue()) {
                        isAchieved = true;
                    }
                    break;
                case "累计":
                    // 累计打卡次数
                    currentValue = memberPointLogsMapper.getPointLogsCountByMidAndRuleId(mid, ruleId);
                    // 累计打卡次数达到要求
                    if (currentValue != null && currentValue >= achievement.getConditionValue()) {
                        isAchieved = true;
                    }
                    break;
                case "积分":
                    // 当前积分总数
                    currentValue = memberPointLogsMapper.getPointSumByMidAndRuleId(mid, ruleId);
                    // 积分达到要求
                    if (currentValue != null && currentValue >= achievement.getConditionValue()) {
                        isAchieved = true;
                    }
                    break;
            }
            
            if (isAchieved) {
//                Map<String, Object> achievementInfo = new HashMap<>();
//                achievementInfo.put("id", achievement.getId());
//                achievementInfo.put("title", achievement.getTitle());
//                achievementInfo.put("img", achievement.getImg());
//                achievementInfo.put("rewardType", achievement.getRewardType());
//                achievementInfo.put("rewardValue", achievement.getRewardValue());
//                achievementInfo.put("currentValue", currentValue);
                result.add(achievement);
                RuleAchievementLog ruleAchievementLog = new RuleAchievementLog();
                ruleAchievementLog.setMid(mid);
                ruleAchievementLog.setRaId(achievement.getId());
                ruleAchievementLog.setRewardValue(achievement.getRewardValue());
                //保存日志
                ruleAchievementLogDao.insert(ruleAchievementLog);
            }
        }
        
        return result;
    }
    
    /**
     * 计算连续打卡天数
     * @param mid 成员ID
     * @param ruleId 规则ID
     * @return 当前连续打卡天数
     */
    private Integer calculateConsecutiveCheckIns(Integer mid, Integer ruleId) {
        // 获取该规则下的所有打卡记录
        List<MemberPointLogs> checkInRecords = memberPointLogsMapper.getCheckInRecords(mid, ruleId);
        if (checkInRecords == null || checkInRecords.isEmpty()) {
            return 0;
        }
        
        // 按时间排序记录，计算连续打卡天数
        int consecutiveDays = 1; // 至少有一条记录，所以初始值为1
        int maxConsecutiveDays = 1;
        
        for (int i = 1; i < checkInRecords.size(); i++) {
            LocalDateTime prevDate = checkInRecords.get(i-1).getCreatedAt();
            LocalDateTime currentDate = checkInRecords.get(i).getCreatedAt();
            
            // 如果日期相差一天，则连续打卡天数加1
            if (prevDate.toLocalDate().plusDays(1).equals(currentDate.toLocalDate())) {
                consecutiveDays++;
            } else {
                // 如果不连续，重置计数器
                consecutiveDays = 1;
            }
            
            // 更新最大连续天数
            maxConsecutiveDays = Math.max(maxConsecutiveDays, consecutiveDays);
            
//            // 连续打卡天数最大为31
//            if (maxConsecutiveDays >= 31) {
//                return 31;
//            }
        }
        
        return maxConsecutiveDays;
    }
} 