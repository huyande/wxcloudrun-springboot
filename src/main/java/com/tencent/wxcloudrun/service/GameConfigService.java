package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.GameConfigWithRewardsRequest;
import com.tencent.wxcloudrun.model.GameConfig;

import java.util.List;

public interface GameConfigService {
    // 创建游戏配置
    void createGameConfig(GameConfig gameConfig);
    
    // 根据ID获取配置
    GameConfig getGameConfigById(Integer id);
    
    // 根据uid和type获取配置
    GameConfig getGameConfigByUidAndType(Integer uid, String type);
    
    // 获取用户所有配置
    List<GameConfig> getGameConfigsByUid(Integer uid);
    
    // 更新配置
    void updateGameConfig(GameConfig gameConfig);
    
    // 删除配置
    void deleteGameConfig(Integer id);

    // 保存游戏配置和奖项
    void saveGameConfigWithRewards(GameConfigWithRewardsRequest request);
} 