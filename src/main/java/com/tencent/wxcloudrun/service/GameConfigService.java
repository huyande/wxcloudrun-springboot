package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.GameConfigWithRewardsRequest;
import com.tencent.wxcloudrun.model.GameConfig;

import java.util.List;

public interface GameConfigService {
    // 创建游戏配置
    void createGameConfig(GameConfig gameConfig, Long seasonId);
    
    // 根据ID获取配置
    GameConfig getGameConfigById(Integer id, Long seasonId);
    
    // 根据uid和type获取配置
    GameConfig getGameConfigByUidAndType(Integer uid, String type, Long seasonId);
    
    // 获取用户所有配置
    List<GameConfig> getGameConfigsByUid(Integer uid, Long seasonId);
    
    // 更新配置
    void updateGameConfig(GameConfig gameConfig, Long seasonId);
    
    // 删除配置
    void deleteGameConfig(Integer id, Long seasonId);

    // 保存游戏配置和奖项
    void saveGameConfigWithRewards(GameConfigWithRewardsRequest request, Long seasonId);
} 