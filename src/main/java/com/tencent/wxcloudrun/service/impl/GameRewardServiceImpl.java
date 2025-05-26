package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.GameRewardMapper;
import com.tencent.wxcloudrun.model.GameReward;
import com.tencent.wxcloudrun.service.GameRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRewardServiceImpl implements GameRewardService {

    private final GameRewardMapper gameRewardMapper;

    public GameRewardServiceImpl(@Autowired GameRewardMapper gameRewardMapper) {
        this.gameRewardMapper = gameRewardMapper;
    }

    @Override
    public void createGameReward(GameReward gameReward, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameReward.setSeasonId(seasonId);
        }
        gameRewardMapper.insertOne(gameReward);
    }

    @Override
    public GameReward getGameRewardById(Integer id, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardMapper.getByIdAndSeasonId(id, seasonId);
        } else {
            // 常规模式
            return gameRewardMapper.getById(id);
        }
    }

    @Override
    public List<GameReward> getGameRewardsByGid(Integer gid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardMapper.getByGidAndSeasonId(gid, seasonId);
        } else {
            // 常规模式
            return gameRewardMapper.getByGid(gid);
        }
    }

    @Override
    public List<GameReward> getGameRewardsByUid(Integer uid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardMapper.getByUidAndSeasonId(uid, seasonId);
        } else {
            // 常规模式
            return gameRewardMapper.getByUid(uid);
        }
    }

    @Override
    public List<GameReward> getGameRewardsByType(String type, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardMapper.getByTypeAndSeasonId(type, seasonId);
        } else {
            // 常规模式
            return gameRewardMapper.getByType(type);
        }
    }

    @Override
    public List<GameReward> getGameRewardsByGidAndType(Integer gid, String type, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardMapper.getByGidAndTypeAndSeasonId(gid, type, seasonId);
        } else {
            // 常规模式
            return gameRewardMapper.getByGidAndType(gid, type);
        }
    }

    @Override
    public List<GameReward> getGameRewardsByUidAndType(Integer uid, String type, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardMapper.getByUidAndTypeAndSeasonId(uid, type, seasonId);
        } else {
            // 常规模式
            return gameRewardMapper.getByUidAndType(uid, type);
        }
    }

    @Override
    public void updateGameReward(GameReward gameReward, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameReward.setSeasonId(seasonId);
            gameRewardMapper.updateByIdAndSeasonId(gameReward);
        } else {
            // 常规模式
            gameRewardMapper.updateById(gameReward);
        }
    }

    @Override
    public void deleteGameReward(Integer id, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameRewardMapper.deleteByIdAndSeasonId(id, seasonId);
        } else {
            // 常规模式
            gameRewardMapper.deleteById(id);
        }
    }

    @Override
    public void deleteGameRewardsByGid(Integer gid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameRewardMapper.deleteByGidAndSeasonId(gid, seasonId);
        } else {
            // 常规模式
            gameRewardMapper.deleteByGid(gid);
        }
    }
} 