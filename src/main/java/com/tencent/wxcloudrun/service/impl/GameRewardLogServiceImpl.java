package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.GameRewardLogMapper;
import com.tencent.wxcloudrun.model.GameRewardLog;
import com.tencent.wxcloudrun.service.GameRewardLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameRewardLogServiceImpl implements GameRewardLogService {

    private final GameRewardLogMapper gameRewardLogMapper;

    public GameRewardLogServiceImpl(@Autowired GameRewardLogMapper gameRewardLogMapper) {
        this.gameRewardLogMapper = gameRewardLogMapper;
    }

    @Override
    public void createGameRewardLog(GameRewardLog gameRewardLog) {
        gameRewardLogMapper.insertOne(gameRewardLog);
    }

    @Override
    public GameRewardLog getGameRewardLogById(Integer id) {
        return gameRewardLogMapper.getById(id);
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByMid(Integer mid) {
        return gameRewardLogMapper.getByMid(mid);
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByGameGroup(Integer gameGroup) {
        return gameRewardLogMapper.getByGameGroup(gameGroup);
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByMidAndGameGroup(Integer mid, Integer gameGroup) {
        return gameRewardLogMapper.getByMidAndGameGroup(mid, gameGroup);
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByMidAndRewardType(Integer mid, String rewardType) {
        return gameRewardLogMapper.getByMidAndRewardType(mid, rewardType);
    }
} 