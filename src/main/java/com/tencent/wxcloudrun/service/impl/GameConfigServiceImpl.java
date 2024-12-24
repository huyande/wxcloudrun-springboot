package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.GameConfigMapper;
import com.tencent.wxcloudrun.dto.GameConfigWithRewardsRequest;
import com.tencent.wxcloudrun.model.GameConfig;
import com.tencent.wxcloudrun.model.GameReward;
import com.tencent.wxcloudrun.service.GameConfigService;
import com.tencent.wxcloudrun.service.GameRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameConfigServiceImpl implements GameConfigService {

    private final GameConfigMapper gameConfigMapper;
    private final GameRewardService gameRewardService;

    public GameConfigServiceImpl(@Autowired GameConfigMapper gameConfigMapper,
                               @Autowired GameRewardService gameRewardService) {
        this.gameConfigMapper = gameConfigMapper;
        this.gameRewardService = gameRewardService;
    }

    @Override
    public void createGameConfig(GameConfig gameConfig) {
        gameConfigMapper.insertOne(gameConfig);
    }

    @Override
    public GameConfig getGameConfigById(Integer id) {
        return gameConfigMapper.getById(id);
    }

    @Override
    public GameConfig getGameConfigByUidAndType(Integer uid, String type) {
        return gameConfigMapper.getByUidAndType(uid, type);
    }

    @Override
    public List<GameConfig> getGameConfigsByUid(Integer uid) {
        return gameConfigMapper.getByUid(uid);
    }

    @Override
    public void updateGameConfig(GameConfig gameConfig) {
        gameConfigMapper.updateById(gameConfig);
    }

    @Override
    public void deleteGameConfig(Integer id) {
        gameConfigMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void saveGameConfigWithRewards(GameConfigWithRewardsRequest request) {
        // 处理游戏配置
        GameConfig gameConfig = request.getGameConfig();
        if (gameConfig.getId() == null) {
            // 新建配置
            createGameConfig(gameConfig);
        } else {
            // 更新配置
            updateGameConfig(gameConfig);
        }

        //删除奖励配置
        gameRewardService.deleteGameRewardsByGid(gameConfig.getId());

        // 处理奖励配置
        List<GameReward> rewards = request.getRewards();
        for (GameReward reward : rewards) {
            // 设置关联的游戏配置ID
            reward.setGid(gameConfig.getId());
            if (reward.getId() == null) {
                // 新建奖励
                gameRewardService.createGameReward(reward);
            } else {
                // 更新奖励
                gameRewardService.updateGameReward(reward);
            }
        }
    }
} 