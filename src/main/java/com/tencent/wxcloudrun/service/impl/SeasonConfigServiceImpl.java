package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SeasonConfigMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleAchievementMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleMapper;
import com.tencent.wxcloudrun.dao.SeasonWishMapper;
import com.tencent.wxcloudrun.dao.SeasonPointLogMapper;
import com.tencent.wxcloudrun.dao.SeasonWishLogMapper;
import com.tencent.wxcloudrun.dao.SeasonRuleAchievementLogMapper;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.MemberPointLogsService;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.SeasonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 赛季配置服务实现类
 */
@Service
public class SeasonConfigServiceImpl implements SeasonConfigService {

    private static final List<SeasonWish> DEFAULT_WISHES = Collections.unmodifiableList(Arrays.asList(
            new SeasonWish("零花钱", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/money.jpg", 10, 1, "元",0,null),
            new SeasonWish("看电视", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/tv.jpg", 1, 5, "分钟",0,null),
            new SeasonWish("玩平板", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/pad.jpg", 1, 5, "分钟",0,null),
            new SeasonWish("玩手机", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/phone.jpg", 1, 5, "分钟",0,null),
            new SeasonWish( "玩游戏", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/game.jpg", 1, 5, "分钟",0,null),
            new SeasonWish("自由活动", "cloud://prod-6gb7vj6n92fcca3f.7072-prod-6gb7vj6n92fcca3f-1252169564/config/wall/free.jpg", 1, 10, "分钟",0,null)
    ));

    @Autowired
    private SeasonConfigMapper seasonConfigMapper;
    
    @Autowired
    private MemberService memberService;

    @Autowired
    private SeasonWishMapper seasonWishMapper;

    @Autowired
    private SeasonRuleMapper seasonRuleMapper;

    @Autowired
    private SeasonRuleAchievementMapper seasonRuleAchievementMapper;

    @Autowired
    private SeasonPointLogMapper seasonPointLogMapper;
    
    @Autowired
    private SeasonWishLogMapper seasonWishLogMapper;
    
    @Autowired
    private SeasonRuleAchievementLogMapper seasonRuleAchievementLogMapper;
    
    @Autowired
    private MemberPointLogsService memberPointLogsService;

    @Override
    public SeasonConfig getById(Long id) {
        return seasonConfigMapper.getById(id);
    }

    @Override
    public List<SeasonConfig> getByMid(Integer mId) {
        return seasonConfigMapper.getByMid(mId);
    }

    @Override
    public List<SeasonConfig> getActiveByMid(Integer mId) {
        return seasonConfigMapper.getActiveByMid(mId);
    }
    
    @Override
    public SeasonConfig getActiveSeasonByMember(Integer mId) {
        // 获取用户的活跃赛季列表
        List<SeasonConfig> activeSeasons = seasonConfigMapper.getActiveByMid(mId);
        // 如果存在活跃赛季，返回第一个（理论上只会有一个活跃赛季）
        return activeSeasons != null && !activeSeasons.isEmpty() ? activeSeasons.get(0) : null;
    }

    @Override
    @Transactional
    public SeasonConfig createSeason(SeasonConfig seasonConfig) {
        // 设置初始状态为未开始
        if (seasonConfig.getStatus() == null) {
            seasonConfig.setStatus(0);
        }
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        seasonConfig.setCreateTime(now);
        seasonConfig.setUpdateTime(now);
        
        // 插入数据库
        seasonConfigMapper.insert(seasonConfig);
        
//        // 设置初始的心愿
//        for(SeasonWish wish:DEFAULT_WISHES){
//            wish.setMid(seasonConfig.getMId());
//            wish.setSeasonId(seasonConfig.getId());
//            seasonWishMapper.insert(wish);
//        }
        
        return seasonConfig;
    }

    @Override
    @Transactional
    public SeasonConfig updateSeason(SeasonConfig seasonConfig) {
        // 更新时间
        seasonConfig.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库
        seasonConfigMapper.update(seasonConfig);
        
        // 返回更新后的配置
        return seasonConfigMapper.getById(seasonConfig.getId());
    }

    @Override
    @Transactional
    public SeasonConfig updateStatus(Long id, Integer status) {
        // 更新状态
        seasonConfigMapper.updateStatus(id, status);
        
        // 返回更新后的配置
        return seasonConfigMapper.getById(id);
    }

    @Override
    @Transactional
    public SeasonConfig startSeason(Integer mId, Long id) {
        return this.switchActiveSeason(mId,id);
    }

    @Override
    @Transactional
    public SeasonConfig endSeason(Long id) {
        // 获取当前赛季
        SeasonConfig seasonConfig = seasonConfigMapper.getById(id);
        if (seasonConfig == null) {
            throw new RuntimeException("赛季不存在");
        }
        
        // 检查状态是否为进行中
        if (seasonConfig.getStatus() != 1) {
            throw new RuntimeException("赛季状态不是进行中，无法结束");
        }
        
        // 设置状态为已结束
        return updateStatus(id, 2);
    }

    @Override
    @Transactional
    public SeasonConfig archiveSeason(Long id) {
        // 获取当前赛季
        SeasonConfig seasonConfig = seasonConfigMapper.getById(id);
        if (seasonConfig == null) {
            throw new RuntimeException("赛季不存在");
        }
        
        // 检查状态是否为已结束
        if (seasonConfig.getStatus() != 2) {
            throw new RuntimeException("赛季状态不是已结束，无法归档");
        }
        
        // 设置状态为已归档
        return updateStatus(id, 3);
    }

    @Override
    @Transactional
    public boolean deleteSeason(Long id) {
        // 获取当前赛季
        SeasonConfig seasonConfig = seasonConfigMapper.getById(id);
        if (seasonConfig == null) {
            return false;
        }
        
        // 只有未开始的赛季才能删除
        if (seasonConfig.getStatus() ==1) {
            throw new RuntimeException("赛季正在进行中，无法删除");
        }
        
        // 删除赛季
        int result = seasonConfigMapper.delete(id);

        this.clearSeason(id);

        return result > 0;
    }
    
    @Override
    @Transactional
    public SeasonConfig switchActiveSeason(Integer mId, Long newSeasonId) {
        // 1. 验证新赛季是否存在
        SeasonConfig newSeason = seasonConfigMapper.getById(newSeasonId);
        if (newSeason == null) {
            throw new RuntimeException("新赛季不存在");
        }
        
        // 2. 如果新赛季不属于该成员，抛出异常
        if (!newSeason.getMId().equals(mId)) {
            throw new RuntimeException("该赛季不属于指定成员");
        }
        
        // 3. 检查新赛季状态，只有未开始(0)或已结束(2)的赛季可以被设为活跃 && newSeason.getStatus() != 2
        if (newSeason.getStatus() != 0 ) {
            throw new RuntimeException("只有未开始或已结束的赛季可以被设为活跃");
        }

        Member member = memberService.getMemberById(mId);

        // 4. 获取当前活跃的赛季
//        List<SeasonConfig> activeSeasons = seasonConfigMapper.getActiveByMid(mId);

        SeasonConfig activeSeason = seasonConfigMapper.getById(member.getCurrentSeasonId());


        // 5. 如果存在活跃赛季，将其状态改为已结束(2)
//        if (activeSeasons != null && !activeSeasons.isEmpty()) {
//            for (SeasonConfig activeSeason : activeSeasons) {
//                seasonConfigMapper.updateStatus(activeSeason.getId(), 2);
//            }
//        }
        seasonConfigMapper.updateStatus(activeSeason.getId(), 2);
        
        // 6. 将新赛季设置为活跃状态(1)
        seasonConfigMapper.updateStatus(newSeasonId, 1);
        
        // 7. 更新成员的currentSeasonId
        memberService.updateCurrentSeasonId(mId, newSeasonId);

        //计算上赛季结算到当前赛季的积分
        if(activeSeason.getConversionRate()>0){
            LocalDateTime dateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            //获取上赛季剩余的积分
            HashMap<String, Integer> pointMap = memberPointLogsService.getPointInfoByMid(mId, activeSeason.getId());
            Integer pointSum = pointMap.get("pointSum");
            int settlement = Math.round((float) pointSum / activeSeason.getConversionRate());
            SeasonPointLog seasonLog = new SeasonPointLog();
            seasonLog.setSeasonId(newSeasonId);
            seasonLog.setDay(dateTime);
            seasonLog.setType(3);
            seasonLog.setMid(mId);
            seasonLog.setNum(settlement);
            seasonLog.setRemark(activeSeason.getId().toString());
            seasonPointLogMapper.insert(seasonLog);
        }
        
        // 8. 返回更新后的赛季配置
        return seasonConfigMapper.getById(newSeasonId);
    }


    @Override
    @Transactional
    public boolean clearSeason(Long id) {
        // 删除心愿
        seasonWishMapper.deleteBySeasonId(id);

        // 删除规则
        seasonRuleMapper.deleteBySeasonId(id);

        // 删除成就
        seasonRuleAchievementMapper.deleteBySeasonId(id);

        // 删除积分记录
        seasonPointLogMapper.deleteBySeasonId(id);

        // 删除规则达成记录
        seasonRuleAchievementLogMapper.deleteBySeasonId(id);

        // 删除心愿日志
        seasonWishLogMapper.deleteBySeasonId(id);

        return true;
    }
} 