package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.*;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    final MemberMapper memberMapper;
    final MemberRelasMapper memberRelasMapper;
    final WxuserMapper wxuserMapper;
    final FamilyMapper familyMapper;
    final WishMapper wishMapper;
    final MemberRulesMapper memberRulesMapper;
    final MemberPointLogsMapper memberPointLogsMapper;
    final WishLogMapper wishLogMapper;
    final GameRewardLogMapper gameRewardLogMapper;
    final RuleAchievementMapper ruleAchievementMapper;
    final RuleAchievementLogMapper ruleAchievementLogMapper;

    //构造函数注入
    public MemberServiceImpl(@Autowired MemberMapper memberMapper,
                             @Autowired MemberRelasMapper memberRelasMapper,
                             @Autowired WxuserMapper wxuserMapper,
                             @Autowired FamilyMapper familyMapper,
                             @Autowired WishMapper wishMapper,
                             @Autowired MemberRulesMapper memberRulesMapper,
                             @Autowired MemberPointLogsMapper memberPointLogsMapper,
                             @Autowired WishLogMapper wishLogMapper,
                             @Autowired GameRewardLogMapper gameRewardLogMapper,
                             @Autowired RuleAchievementMapper ruleAchievementMapper,
                             @Autowired RuleAchievementLogMapper ruleAchievementLogMapper) {

        this.familyMapper = familyMapper;
        this.memberMapper = memberMapper;
        this.memberRelasMapper = memberRelasMapper;
        this.wxuserMapper = wxuserMapper;
        this.wishMapper = wishMapper;
        this.memberRulesMapper = memberRulesMapper;
        this.memberPointLogsMapper = memberPointLogsMapper;
        this.wishLogMapper = wishLogMapper;
        this.gameRewardLogMapper = gameRewardLogMapper;
        this.ruleAchievementMapper = ruleAchievementMapper;
        this.ruleAchievementLogMapper = ruleAchievementLogMapper;
    }

    private static final List<Wish> DEFAULT_WISHES = Collections.unmodifiableList(Arrays.asList(
            new Wish("零花钱", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/money.jpg", 10, 1, "元",0),
            new Wish("看电视", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/tv.jpg", 1, 5, "分钟",0),
            new Wish("玩平板", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/pad.jpg", 1, 5, "分钟",0),
            new Wish("玩手机", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/phone.jpg", 1, 5, "分钟",0),
            new Wish( "玩游戏", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/game.jpg", 1, 5, "分钟",0),
            new Wish("自由活动", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/free.jpg", 1, 10, "分钟",0)
    ));

    @Override
    public List<Member> getMembersByFamilyCode(String FamilyCode) {
        return memberMapper.getMembersByFamilyCode(FamilyCode);
    }

    @Override
    public Integer getMemberCountByUid(Integer uId) {
        return memberMapper.getCountMembersByUid(uId);
    }

    @Transient
    @Override
    public Member insert(MemberRequest memberRequest) {
        WxUser wxUser = wxuserMapper.getUserById(memberRequest.getUid());
        Member member = new Member();
        member.setName(memberRequest.getName());
        member.setGender(memberRequest.getGender());
        member.setPointTotal(memberRequest.getPointTotalCount());
        member.setUid(memberRequest.getUid());
        member.setFamilyCode(wxUser.getFamilyCode());
        memberMapper.insertOne(member);
        Family family = familyMapper.getOneByCodeAndUid(wxUser.getFamilyCode(), memberRequest.getUid());
        if(family == null){
            family = new Family();
            family.setCode(wxUser.getFamilyCode());
            family.setUid(memberRequest.getUid());
            familyMapper.insertOne(family);
        }

        if(memberRequest.getRole()!=-1){//如果role不是-1，则更新这个user的role
            //更新这个user的role
            wxuserMapper.updateRoleUserById(memberRequest.getUid(),memberRequest.getRole());
        }
        //插入默认的愿望
        for(Wish wish:DEFAULT_WISHES){
            wish.setMid(member.getId());
            wishMapper.insert(wish);
        }
        return member;
    }

    @Override
    public List<Member> getMembersByUid(Integer id) {
        return memberMapper.getMembersByUid(id);
    }

    @Override
    public Member getMemberById(Integer mid) {
        return memberMapper.getMemberById(mid);
    }

    @Override
    public Member updateMember(Integer id, MemberRequest memberRequest) {
        Member member = memberMapper.getMemberById(id);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        if(memberRequest.getName()!=null){
            member.setName(memberRequest.getName());
        }
        if(memberRequest.getGender()!=null){
            member.setGender(memberRequest.getGender());
        }
        if(memberRequest.getAvatar()!=null){
            member.setAvatar(memberRequest.getAvatar());
        }

//        member.setPointTotal(memberRequest.getPointTotalCount());
        
        memberMapper.updateById(member);
        return memberMapper.getMemberById(id);
    }

    @Transactional
    @Override
    public void clearMemberData(Integer mid) {
        // 验证member是否存在
        Member member = memberMapper.getMemberById(mid);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        List<MemberRules> rules = memberRulesMapper.getRulesByMid(mid);
        for(MemberRules rule : rules){
            //删除成就配置
            ruleAchievementMapper.deleteByRuleId(rule.getId());
        }
        
        // 删除规则数据
        memberRulesMapper.deleteByMid(mid);
        
        // 删除积分记录
        memberPointLogsMapper.deleteByMid(mid);
        
        // 删除愿望记录
        wishLogMapper.deleteByMid(mid);
        
        // 删除愿望
        wishMapper.deleteByMid(mid,1);

        //删除游戏日志
        gameRewardLogMapper.deleteByMid(mid);
        //删除成就记录
        ruleAchievementLogMapper.deleteByMId(mid);

        //删除赛季相关的数据
        memberMapper.deleteAllSeasion(mid);

    }

    @Override
    public void deleteMemberById(Integer mid) {
        Member member = memberMapper.getMemberById(mid);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        List<MemberRules> rules = memberRulesMapper.getRulesByMid(mid);
        for(MemberRules rule : rules){
            //删除成就配置
            ruleAchievementMapper.deleteByRuleId(rule.getId());
        }
        // 删除规则数据
        memberRulesMapper.deleteByMid(mid);

        // 删除积分记录
        memberPointLogsMapper.deleteByMid(mid);

        // 删除愿望记录
        wishLogMapper.deleteByMid(mid);

        // 删除愿望
        wishMapper.deleteByMid(mid,null);

        //删除游戏日志
        gameRewardLogMapper.deleteByMid(mid);
        //删除成就记录
        ruleAchievementLogMapper.deleteByMId(mid);
        memberMapper.deleteById(mid);
    }

    @Override
    @Transactional
    public Member switchMode(Integer mid, String mode) {
        // 验证成员是否存在
        Member member = memberMapper.getMemberById(mid);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        
        // 验证模式参数
        if (!"defaultMode".equals(mode) && !"seasonMode".equals(mode)) {
            throw new IllegalArgumentException("无效的模式类型，必须是 'defaultMode' 或 'seasonMode'");
        }
        
        // 如果切换到常规模式，清除关联的赛季ID
//        if ("NORMAL".equals(mode)) {
//            member.setCurrentSeasonId(null);
//        }
        
        // 设置模式
        member.setMode(mode);
        
        // 更新成员信息
        memberMapper.updateById(member);
        
        // 返回更新后的成员信息
        return memberMapper.getMemberById(mid);
    }
    
    @Override
    @Transactional
    public Member updateCurrentSeasonId(Integer mid, Long seasonId) {
        // 验证成员是否存在
        Member member = memberMapper.getMemberById(mid);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        
        // 设置赛季ID
        member.setCurrentSeasonId(seasonId);
        
        // 更新成员信息
        memberMapper.updateById(member);
        
        // 返回更新后的成员信息
        return memberMapper.getMemberById(mid);
    }

    @Override
    public List<Map<String, Object>> getFamilyMembersRules(Integer mid, String familyCode) {
        // 获取家庭组中的所有成员
        List<Member> allMembers = memberMapper.getMembersByFamilyCode(familyCode);
        
        // 过滤掉当前成员，只保留其他成员
        List<Member> otherMembers = allMembers.stream()
                .filter(member -> !member.getId().equals(mid))
                .collect(Collectors.toList());
        
        if (otherMembers.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 为每个成员获取其活跃规则
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Member member : otherMembers) {
            Map<String, Object> memberWithRules = new HashMap<>();
            memberWithRules.put("member", member);
            
            try {
                // 常规模式处理 - 获取状态为1的规则
                List<MemberRules> rules = memberRulesMapper.getActiveRulesByMid(member.getId());
                memberWithRules.put("rules", rules);
            } catch (Exception e) {
                logger.warn("获取成员 {} 的规则失败: {}", member.getId(), e.getMessage());
                memberWithRules.put("rules", Collections.emptyList());
            }
            
            result.add(memberWithRules);
        }
        
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> batchImportRules(Integer mid, List<Integer> ruleIds) {
        // 检查目标成员是否存在
        Member targetMember = memberMapper.getMemberById(mid);
        if (targetMember == null) {
            throw new IllegalArgumentException("目标成员不存在");
        }

        List<Map<String, Object>> importResults = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (Integer ruleId : ruleIds) {
            Map<String, Object> result = new HashMap<>();
            result.put("originalRuleId", ruleId);
            
            try {
                // 根据规则ID查询规则信息
                MemberRules originalRule = memberRulesMapper.getRuleById(ruleId);
                if (originalRule == null) {
                    result.put("success", false);
                    result.put("message", "源规则不存在");
                    failCount++;
                    importResults.add(result);
                    continue;
                }

                // 检查目标成员是否已存在同名规则
                MemberRules existingRule = memberRulesMapper.getRuleByNameAndMid(originalRule.getName(), mid);
                if (existingRule != null) {
                    result.put("success", false);
                    result.put("message", "目标成员已存在同名规则: " + originalRule.getName());
                    failCount++;
                    importResults.add(result);
                    continue;
                }

                // 创建新的规则
                MemberRules newRule = new MemberRules();
                newRule.setMid(mid);
                newRule.setName(originalRule.getName());
                newRule.setType(originalRule.getType());
                newRule.setIcon(originalRule.getIcon());
                newRule.setIconType(originalRule.getIconType());
                newRule.setWeeks(originalRule.getWeeks());
                newRule.setContent(originalRule.getContent());
                newRule.setQuickScore(originalRule.getQuickScore());
                newRule.setTypeSort(originalRule.getTypeSort());
                newRule.setEnablePomodoro(originalRule.getEnablePomodoro());
                newRule.setPomodoroTime(originalRule.getPomodoroTime());
                newRule.setIsAchievement(originalRule.getIsAchievement());
                newRule.setCompletionConditions(originalRule.getCompletionConditions());
                newRule.setStatus(1); // 设置为启用状态
                
                // 设置排序值（获取当前成员该类型规则的最大排序值+1）
                MemberRules lastRule = memberRulesMapper.getLastSortByTypeAndMid(mid, originalRule.getType());
                if (lastRule != null) {
                    newRule.setSort(lastRule.getSort() + 1);
                } else {
                    newRule.setSort(0);
                }

                // 插入新规则
                memberRulesMapper.insertOne(newRule);
                
                result.put("success", true);
                result.put("newRuleId", newRule.getId());
                result.put("ruleName", newRule.getName());

                // 如果原规则配置了成就，复制成就配置
                if (originalRule.getIsAchievement() != null && originalRule.getIsAchievement() == 1) {
                    try {
                        List<RuleAchievement> achievements = ruleAchievementMapper.getByRuleId(ruleId);
                        
                        int achievementCount = 0;
                        for (RuleAchievement originalAchievement : achievements) {
                            // 创建新的成就配置
                            RuleAchievement newAchievement = new RuleAchievement();
                            newAchievement.setMId(mid);
                            newAchievement.setRuleId(newRule.getId());
                            newAchievement.setTitle(originalAchievement.getTitle());
                            newAchievement.setImg(originalAchievement.getImg());
                            newAchievement.setConditionType(originalAchievement.getConditionType());
                            newAchievement.setConditionValue(originalAchievement.getConditionValue());
                            newAchievement.setConditionDesc(originalAchievement.getConditionDesc());
                            newAchievement.setRewardType(originalAchievement.getRewardType());
                            newAchievement.setRewardValue(originalAchievement.getRewardValue());
                            
                            ruleAchievementMapper.insert(newAchievement);
                            achievementCount++;
                        }
                        
                        result.put("achievementCount", achievementCount);
                        result.put("message", "规则和成就配置导入成功");
                    } catch (Exception e) {
                        logger.warn("成就配置复制失败，但规则创建成功 - 规则ID: {}, 错误: {}", newRule.getId(), e.getMessage());
                        result.put("achievementCount", 0);
                        result.put("message", "规则导入成功，但成就配置复制失败");
                    }
                } else {
                    result.put("achievementCount", 0);
                    result.put("message", "规则导入成功");
                }
                
                successCount++;
                
            } catch (Exception e) {
                logger.error("导入规则失败 - 规则ID: {}, 错误: {}", ruleId, e.getMessage());
                result.put("success", false);
                result.put("message", "导入失败: " + e.getMessage());
                failCount++;
            }
            
            importResults.add(result);
        }

        // 返回导入结果
        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", ruleIds.size());
        response.put("successCount", successCount);
        response.put("failCount", failCount);
        response.put("results", importResults);
        
        return response;
    }

}
