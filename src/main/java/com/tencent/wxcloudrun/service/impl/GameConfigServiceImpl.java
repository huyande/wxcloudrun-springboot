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
    public void createGameConfig(GameConfig gameConfig, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameConfig.setSeasonId(seasonId);
        }
        gameConfigMapper.insertOne(gameConfig);
    }

    @Override
    public GameConfig getGameConfigById(Integer id, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameConfigMapper.getByIdAndSeasonId(id, seasonId);
        } else {
            // 常规模式
            return gameConfigMapper.getById(id);
        }
    }

    @Override
    public GameConfig getGameConfigByUidAndType(Integer uid, String type, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameConfigMapper.getByUidAndTypeAndSeasonId(uid, type, seasonId);
        } else {
            // 常规模式
            return gameConfigMapper.getByUidAndType(uid, type);
        }
    }

    @Override
    public List<GameConfig> getGameConfigsByUid(Integer uid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameConfigMapper.getByUidAndSeasonId(uid, seasonId);
        } else {
            // 常规模式
            return gameConfigMapper.getByUid(uid);
        }
    }

    @Override
    public void updateGameConfig(GameConfig gameConfig, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameConfig.setSeasonId(seasonId);
            gameConfigMapper.updateByIdAndSeasonId(gameConfig);
        } else {
            // 常规模式
            gameConfigMapper.updateById(gameConfig);
        }
    }

    @Override
    public void deleteGameConfig(Integer id, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameConfigMapper.deleteByIdAndSeasonId(id, seasonId);
        } else {
            // 常规模式
            gameConfigMapper.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void saveGameConfigWithRewards(GameConfigWithRewardsRequest request, Long seasonId) {
        // 处理游戏配置
        GameConfig gameConfig = request.getGameConfig();
        if (gameConfig.getId() == null) {
            // 新建配置
            createGameConfig(gameConfig, seasonId);
        } else {
            // 更新配置
            updateGameConfig(gameConfig, seasonId);
        }

        //删除奖励配置
        gameRewardService.deleteGameRewardsByGid(gameConfig.getId(), seasonId);

        // 处理奖励配置
        List<GameReward> rewards = request.getRewards();
        for (GameReward reward : rewards) {
            // 设置关联的游戏配置ID
            reward.setGid(gameConfig.getId());
            if (reward.getId() == null) {
                // 新建奖励
                gameRewardService.createGameReward(reward, seasonId);
            } else {
                // 更新奖励
                gameRewardService.updateGameReward(reward, seasonId);
            }
        }
    }
} 