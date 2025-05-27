package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.*;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

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

}
