package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.GameReward;

import java.util.List;

public interface GameRewardService {
    // 创建游戏奖励
    void createGameReward(GameReward gameReward);
    
    // 根据ID获取奖励
    GameReward getGameRewardById(Integer id);
    
    // 根据游戏配置ID获取奖励列表
    List<GameReward> getGameRewardsByGid(Integer gid);
    
    // 根据用户ID获取奖励列表
    List<GameReward> getGameRewardsByUid(Integer uid);
    
    // 根据游戏类型获取奖励列表
    List<GameReward> getGameRewardsByType(String type);
    
    // 根据游戏配置ID和类型获取奖励列表
    List<GameReward> getGameRewardsByGidAndType(Integer gid, String type);
    
    // 根据用户ID和类型获取奖励列表
    List<GameReward> getGameRewardsByUidAndType(Integer uid, String type);
    
    // 更新奖励
    void updateGameReward(GameReward gameReward);
    
    // 删除奖励
    void deleteGameReward(Integer id);

    void deleteGameRewardsByGid(Integer gid);
} 