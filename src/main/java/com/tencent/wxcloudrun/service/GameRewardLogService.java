package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.GameRewardLog;

import java.util.List;
import java.util.Map;

public interface GameRewardLogService {
    // 创建游戏中奖记录
    void createGameRewardLog(GameRewardLog gameRewardLog, Long seasonId);
    
    // 根据ID获取记录
    GameRewardLog getGameRewardLogById(Integer id, Long seasonId);
    
    // 根据成员ID获取记录列表
    List<GameRewardLog> getGameRewardLogsByMid(Integer mid, Long seasonId);
    
    // 根据游戏分组获取记录列表
    List<GameRewardLog> getGameRewardLogsByGameGroup(Integer gameGroup, Long seasonId);
    
    // 根据成员ID和游戏分组获取记录列表
    List<GameRewardLog> getGameRewardLogsByMidAndGameGroup(Integer mid, Integer gameGroup, Long seasonId);
    
    // 根据成员ID和奖励类型获取记录列表
    List<GameRewardLog> getGameRewardLogsByMidAndRewardType(Integer mid, String rewardType, Long seasonId);

    void updateStatus(Integer id, Long seasonId);

    // 增加分页参数的接口
    Map<String, Object> getGameRewardLogsByMidAndGameGroupWithPage(
            Integer mid, 
            Integer gameGroup, 
            Integer pageNum, 
            Integer pageSize,
            Long seasonId
    );
} 