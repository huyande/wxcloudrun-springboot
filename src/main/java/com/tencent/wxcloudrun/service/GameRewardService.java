package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.GameReward;

import java.util.List;

public interface GameRewardService {
    // 创建游戏奖励
    void createGameReward(GameReward gameReward, Long seasonId);
    
    // 根据ID获取奖励
    GameReward getGameRewardById(Integer id, Long seasonId);
    
    // 根据游戏配置ID获取奖励列表
    List<GameReward> getGameRewardsByGid(Integer gid, Long seasonId);
    
    // 根据用户ID获取奖励列表
    List<GameReward> getGameRewardsByUid(Integer uid, Long seasonId);
    
    // 根据游戏类型获取奖励列表
    List<GameReward> getGameRewardsByType(String type, Long seasonId);
    
    // 根据游戏配置ID和类型获取奖励列表
    List<GameReward> getGameRewardsByGidAndType(Integer gid, String type, Long seasonId);
    
    // 根据用户ID和类型获取奖励列表
    List<GameReward> getGameRewardsByUidAndType(Integer uid, String type, Long seasonId);
    
    // 更新奖励
    void updateGameReward(GameReward gameReward, Long seasonId);
    
    // 删除奖励
    void deleteGameReward(Integer id, Long seasonId);

    void deleteGameRewardsByGid(Integer gid, Long seasonId);
} 