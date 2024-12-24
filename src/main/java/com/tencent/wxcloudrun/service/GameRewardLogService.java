package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.GameRewardLog;

import java.util.List;

public interface GameRewardLogService {
    // 创建游戏中奖记录
    void createGameRewardLog(GameRewardLog gameRewardLog);
    
    // 根据ID获取记录
    GameRewardLog getGameRewardLogById(Integer id);
    
    // 根据成员ID获取记录列表
    List<GameRewardLog> getGameRewardLogsByMid(Integer mid);
    
    // 根据游戏分组获取记录列表
    List<GameRewardLog> getGameRewardLogsByGameGroup(Integer gameGroup);
    
    // 根据成员ID和游戏分组获取记录列表
    List<GameRewardLog> getGameRewardLogsByMidAndGameGroup(Integer mid, Integer gameGroup);
    
    // 根据成员ID和奖励类型获取记录列表
    List<GameRewardLog> getGameRewardLogsByMidAndRewardType(Integer mid, String rewardType);
} 