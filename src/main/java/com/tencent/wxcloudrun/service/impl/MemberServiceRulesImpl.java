package com.tencent.wxcloudrun.service.impl;

import cn.hutool.core.date.DateTime;
import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.MemberRulesMapper;
import com.tencent.wxcloudrun.dao.SeasonPointLogMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleMapper;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;
import com.tencent.wxcloudrun.model.SeasonRule;
import com.tencent.wxcloudrun.service.MemberRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;

@Service
public class MemberServiceRulesImpl implements MemberRulesService {

    final MemberRulesMapper memberRulesMapper;
    final MemberPointLogsMapper memberPointLogsMapper;
    final SeasonPointLogMapper seasonPointLogMapper;
    final SeasonRuleMapper seasonRuleMapper;
    
    //构造函数注入
    public MemberServiceRulesImpl(@Autowired MemberRulesMapper memberRulesMapper,
                                  @Autowired MemberPointLogsMapper memberPointLogsMapper,
                                  @Autowired SeasonRuleMapper seasonRuleMapper,
                                  @Autowired SeasonPointLogMapper seasonPointLogMapper) {
        this.memberRulesMapper = memberRulesMapper;
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.seasonRuleMapper = seasonRuleMapper;
        this.seasonPointLogMapper = seasonPointLogMapper;
    }

    /**
     * 获取指定mid的规则列表（支持常规与赛季模式）
     */
    @Override
    public <T> List<T> getRulesByMid(Integer mId, String day, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            List<SeasonRule> rules = seasonRuleMapper.getRulesBySeasonIdAndMidAndDay(seasonId, mId, day);
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return (List<T>) rules;
        } else {
            DateTime parse = DateUtil.parse(day);
            String dayCh = DateUtil.dayOfWeekEnum(parse).toChinese();
            List<MemberRules> rules = memberRulesMapper.getRulesByMid(mId);
            List<MemberRules> result = new ArrayList<>();
            for(MemberRules rule : rules){
                if(rule.getWeeks().contains(dayCh)){
                    result.add(rule);
                }
            }
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return (List<T>) result;
        }
    }

    /**
     * 新增规则（支持常规与赛季模式）
     */
    @Override
    public <T> T insert(MemberRuleRequest memberRuleRequest, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            SeasonRule seasonRule = new SeasonRule();
            seasonRule.setSeasonId(seasonId);
            seasonRule.setName(memberRuleRequest.getName());
            seasonRule.setType(memberRuleRequest.getType());
            seasonRule.setMId(memberRuleRequest.getMid());
            seasonRule.setIcon(memberRuleRequest.getIcon());
            seasonRule.setIconType(memberRuleRequest.getIconType());
            seasonRule.setWeeks(memberRuleRequest.getWeeks());
            seasonRule.setContent(memberRuleRequest.getContent());
            seasonRule.setSort(memberRuleRequest.getSort());
            seasonRule.setQuickScore(memberRuleRequest.getQuickScore());
            seasonRule.setTypeSort(memberRuleRequest.getTypeSort());
            seasonRule.setEnablePomodoro(memberRuleRequest.getEnablePomodoro());
            seasonRule.setPomodoroTime(memberRuleRequest.getPomodoroTime());
            seasonRule.setStatus(1); // 默认激活
            if(memberRuleRequest.getIsAchievement() != null) {
                // 如果SeasonRule有对应字段，需要进行设置
                seasonRule.setIsAchievement(memberRuleRequest.getIsAchievement());
            }
            seasonRule.setCreateTime(LocalDateTime.now());
            seasonRule.setUpdateTime(LocalDateTime.now());
            
            seasonRuleMapper.insert(seasonRule);
            
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return expectedType.cast(seasonRule);
        } else {
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
            memberRules.setEnablePomodoro(memberRuleRequest.getEnablePomodoro());
            memberRules.setPomodoroTime(memberRuleRequest.getPomodoroTime());
            if(memberRuleRequest.getIsAchievement()!=null){
                memberRules.setIsAchievement(memberRuleRequest.getIsAchievement());
            }
            memberRulesMapper.insertOne(memberRules);
            
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return expectedType.cast(memberRules);
        }
    }

    /**
     * 根据名称和mid获取规则（支持常规与赛季模式）
     */
    @Override
    public <T> T getRuleByNameAndMid(String name, Integer mid, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            SeasonRule rule = seasonRuleMapper.getRuleByNameAndMid(name, mid, seasonId);
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return expectedType.cast(rule);
        } else {
            MemberRules rule = memberRulesMapper.getRuleByNameAndMid(name, mid);
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return expectedType.cast(rule);
        }
    }

    /**
     * 根据ID获取规则（支持常规与赛季模式）
     */
    @Override
    public <T> T getRuleById(Integer id, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            SeasonRule rule = seasonRuleMapper.getById(Long.valueOf(id));
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return expectedType.cast(rule);
        } else {
            MemberRules rule = memberRulesMapper.getRuleById(id);
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return expectedType.cast(rule);
        }
    }

    /**
     * 删除规则（支持常规与赛季模式）
     */
    @Override
    public void delete(Integer id, Long seasonId) {
        if (seasonId != null) {
            seasonRuleMapper.delete(Long.valueOf(id));
            // 注意：对应的积分日志处理逻辑也应该添加，类似于常规分支
            seasonPointLogMapper.delete(Long.valueOf(id));
        } else {
            memberRulesMapper.delete(id);
            memberPointLogsMapper.delete(Long.valueOf(id));
        }
    }

    /**
     * 根据ID更新规则（支持常规与赛季模式）
     */
    @Override
    public void updateRuleById(Integer id, MemberRuleRequest memberRuleRequest, Long seasonId) {
        if (seasonId != null) {
            SeasonRule seasonRule = new SeasonRule();
            seasonRule.setId(Long.valueOf(id));
            seasonRule.setSeasonId(seasonId);
            seasonRule.setName(memberRuleRequest.getName());
            seasonRule.setType(memberRuleRequest.getType());
            seasonRule.setMId(memberRuleRequest.getMid());
            seasonRule.setIcon(memberRuleRequest.getIcon());
            seasonRule.setIconType(memberRuleRequest.getIconType());
            seasonRule.setWeeks(memberRuleRequest.getWeeks());
            seasonRule.setContent(memberRuleRequest.getContent());
            seasonRule.setSort(memberRuleRequest.getSort());
            seasonRule.setQuickScore(memberRuleRequest.getQuickScore());
            seasonRule.setStatus(1); // 默认激活
            seasonRule.setTypeSort(memberRuleRequest.getTypeSort());
            seasonRule.setEnablePomodoro(memberRuleRequest.getEnablePomodoro());
            seasonRule.setPomodoroTime(memberRuleRequest.getPomodoroTime());
            seasonRule.setUpdateTime(LocalDateTime.now());
            if(memberRuleRequest.getIsAchievement() != null) {
                // 如果SeasonRule有对应字段，需要进行设置
                seasonRule.setIsAchievement(memberRuleRequest.getIsAchievement());
            }
            
            seasonRuleMapper.update(seasonRule);
        } else {
            MemberRules memberRules = new MemberRules();
            memberRules.setId(id);
            memberRules.setName(memberRuleRequest.getName());
            memberRules.setType(memberRuleRequest.getType());
            memberRules.setMid(memberRuleRequest.getMid());
            memberRules.setIcon(memberRuleRequest.getIcon());
            memberRules.setIconType(memberRuleRequest.getIconType());
            memberRules.setWeeks(memberRuleRequest.getWeeks());
            memberRules.setContent(memberRuleRequest.getContent());
            memberRules.setSort(memberRuleRequest.getSort());
            memberRules.setQuickScore(memberRuleRequest.getQuickScore());
            memberRules.setStatus(1);
            memberRules.setTypeSort(memberRuleRequest.getTypeSort());
            memberRules.setEnablePomodoro(memberRuleRequest.getEnablePomodoro());
            memberRules.setPomodoroTime(memberRuleRequest.getPomodoroTime());
            if(memberRuleRequest.getIsAchievement()!=null){
                memberRules.setIsAchievement(memberRuleRequest.getIsAchievement());
            }
            memberRulesMapper.updateRuleById(memberRules);
        }
    }

    /**
     * 批量新增规则（支持常规与赛季模式）
     */
    @Override
    public <T> List<T> insertBatch(List<MemberRuleRequest> memberRuleRequests, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            List<SeasonRule> list = new ArrayList<>();
            
            if (memberRuleRequests.isEmpty()) {
                return (List<T>) list;
            }
            
            // 假设所有请求的mid都相同，取第一个检查
            Integer mid = memberRuleRequests.get(0).getMid();
            
            // 检查当前规则数量
            int currentCount = countActiveRulesByMid(mid, seasonId);
            
            for(MemberRuleRequest memberRuleRequest : memberRuleRequests){
                // 如果已有规则数量加上当前待处理数量超过50个，则不再处理
                if (currentCount >= 50) {
                    throw new RuntimeException("规则数量已达上限(50个)，无法创建新规则");
                }
                
                // 这里应该检查重名规则，类似常规分支
                // 由于没有直接的方法，可以通过获取所有规则后过滤实现
                List<SeasonRule> existingRules = seasonRuleMapper.getBySeasonIdAndMid(seasonId, mid);
                boolean ruleExists = false;
                for (SeasonRule rule : existingRules) {
                    if (rule.getName().equals(memberRuleRequest.getName())) {
                        ruleExists = true;
                        break;
                    }
                }
                
                if (!ruleExists) {
                    // 设置类型排序
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
                    
                    SeasonRule seasonRule = insert(memberRuleRequest, seasonId, SeasonRule.class);
                    list.add(seasonRule);
                    currentCount++; // 更新当前规则计数
                }
            }
            
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return (List<T>) list;
        } else {
            List<MemberRules> list = new ArrayList<>();
            
            if (memberRuleRequests.isEmpty()) {
                return (List<T>) list;
            }
            
            // 假设所有请求的mid都相同，取第一个检查
            Integer mid = memberRuleRequests.get(0).getMid();
            
            // 检查当前规则数量
            int currentCount = countActiveRulesByMid(mid, null);
            
            for(MemberRuleRequest memberRuleRequest : memberRuleRequests){
                // 如果已有规则数量加上当前待处理数量超过50个，则不再处理
                if (currentCount >= 50) {
                    throw new RuntimeException("规则数量已达上限(50个)，无法创建新规则");
                }
                
                List<MemberRules> rules = memberRulesMapper.getRuleByNameAndMids(memberRuleRequest.getName(), memberRuleRequest.getMid());
                if(rules.isEmpty()){
                    // 设置类型排序
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
                    
                    MemberRules memberRules = insert(memberRuleRequest, null, MemberRules.class);
                    list.add(memberRules);
                    currentCount++; // 更新当前规则计数
                }
            }
            
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return (List<T>) list;
        }
    }

    /**
     * 更新规则（支持常规与赛季模式）
     */
    @Override
    public void updateRule(MemberRules rule, Long seasonId) {
        if (seasonId != null) {
            SeasonRule seasonRule = new SeasonRule();
            seasonRule.setId(Long.valueOf(rule.getId()));
            seasonRule.setSeasonId(seasonId);
            seasonRule.setName(rule.getName());
            seasonRule.setType(rule.getType());
            seasonRule.setMId(rule.getMid());
            seasonRule.setIcon(rule.getIcon());
            seasonRule.setIconType(rule.getIconType());
            seasonRule.setWeeks(rule.getWeeks());
            seasonRule.setContent(rule.getContent());
            seasonRule.setSort(rule.getSort());
            seasonRule.setQuickScore(rule.getQuickScore());
            seasonRule.setStatus(rule.getStatus());
            seasonRule.setTypeSort(rule.getTypeSort());
            seasonRule.setEnablePomodoro(rule.getEnablePomodoro());
            seasonRule.setPomodoroTime(rule.getPomodoroTime());
            if (rule.getIsAchievement() != null) {
                // 如果SeasonRule有对应字段，需要进行设置
            }
            seasonRule.setUpdateTime(LocalDateTime.now());
            
            seasonRuleMapper.update(seasonRule);
        } else {
            memberRulesMapper.updateRuleById(rule);
        }
    }

    /**
     * 获取指定mid的所有活动规则（status=1，支持常规与赛季模式）
     */
    @Override
    public <T> List<T> getActiveRulesByMid(Integer mId, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            List<SeasonRule> rules = seasonRuleMapper.getActiveRulesBySeasonIdAndMid(seasonId, mId);
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return (List<T>) rules;
        } else {
            List<MemberRules> rules = memberRulesMapper.getActiveRulesByMid(mId);
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return (List<T>) rules;
        }
    }

    /**
     * 获取指定mid的活跃规则数量（支持常规与赛季模式）
     */
    @Override
    public int countActiveRulesByMid(Integer mId, Long seasonId) {
        if (seasonId != null) {
            return seasonRuleMapper.countActiveRulesBySeasonIdAndMid(seasonId, mId);
        } else {
            return memberRulesMapper.countActiveRulesByMid(mId);
        }
    }

    /**
     * 检查是否可以创建新规则（支持常规与赛季模式）
     */
    @Override
    public boolean canCreateRule(Integer mId, Long seasonId) {
        int count = countActiveRulesByMid(mId, seasonId);
        return count <= 50; // 如果当前规则数量小于50，则可以创建
    }

    /**
     * 交换两个规则的排序值（支持常规与赛季模式）
     */
    @Override
    public void swapRuleSort(Integer currentId, Integer targetId, Long seasonId) {
        if (seasonId != null) {
            SeasonRule currentRule = seasonRuleMapper.getById(Long.valueOf(currentId));
            SeasonRule targetRule = seasonRuleMapper.getById(Long.valueOf(targetId));
            
            if (currentRule == null || targetRule == null) {
                throw new RuntimeException("规则不存在");
            }
            
            // 交换排序值
            Integer tempSort = currentRule.getSort();
            currentRule.setSort(targetRule.getSort());
            targetRule.setSort(tempSort);
            
            // 更新规则
            seasonRuleMapper.update(currentRule);
            seasonRuleMapper.update(targetRule);
        } else {
            MemberRules currentRule = memberRulesMapper.getRuleById(currentId);
            MemberRules targetRule = memberRulesMapper.getRuleById(targetId);
            
            if (currentRule == null || targetRule == null) {
                throw new RuntimeException("规则不存在");
            }
            
            memberRulesMapper.swapRuleSort(currentId, targetId, currentRule.getSort(), targetRule.getSort());
        }
    }

    /**
     * 获取指定类型的最后一个规则（按排序，支持常规与赛季模式）
     */
    @Override
    public <T> T getLastSortByTypeAndMid(Integer mid, String type, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            SeasonRule rule = seasonRuleMapper.getLastSortByTypeAndMid(mid, seasonId, type);
            if (!expectedType.isAssignableFrom(SeasonRule.class)) {
                throw new IllegalArgumentException("expectedType必须为SeasonRule.class");
            }
            return expectedType.cast(rule);
        } else {
            MemberRules rule = memberRulesMapper.getLastSortByTypeAndMid(mid, type);
            if (!expectedType.isAssignableFrom(MemberRules.class)) {
                throw new IllegalArgumentException("expectedType必须为MemberRules.class");
            }
            return expectedType.cast(rule);
        }
    }

    /**
     * 批量更新规则分类名称（支持常规与赛季模式）
     */
    @Override
    public int updateRuleType(Integer mid, String oldType, String newType, Long seasonId) {
        if (mid == null || oldType == null || newType == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        if (seasonId != null) {
            return seasonRuleMapper.updateRuleType(mid, seasonId, oldType, newType);
        } else {
            return memberRulesMapper.updateRuleTypeByMid(mid, oldType, newType);
        }
    }

    /**
     * 获取会员的所有规则分类（支持常规与赛季模式）
     */
    @Override
    public List<Map<String, Integer>> getRuleTypes(Integer mid, Long seasonId) {
        if (mid == null) {
            throw new IllegalArgumentException("会员ID不能为空");
        }
        
        if (seasonId != null) {
            return seasonRuleMapper.getRuleTypes(mid, seasonId);
        } else {
            return memberRulesMapper.getRuleTypesByMid(mid);
        }
    }
}
