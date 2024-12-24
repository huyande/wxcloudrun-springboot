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
    public void createGameReward(GameReward gameReward) {
        gameRewardMapper.insertOne(gameReward);
    }

    @Override
    public GameReward getGameRewardById(Integer id) {
        return gameRewardMapper.getById(id);
    }

    @Override
    public List<GameReward> getGameRewardsByGid(Integer gid) {
        return gameRewardMapper.getByGid(gid);
    }

    @Override
    public List<GameReward> getGameRewardsByUid(Integer uid) {
        return gameRewardMapper.getByUid(uid);
    }

    @Override
    public List<GameReward> getGameRewardsByType(String type) {
        return gameRewardMapper.getByType(type);
    }

    @Override
    public List<GameReward> getGameRewardsByGidAndType(Integer gid, String type) {
        return gameRewardMapper.getByGidAndType(gid, type);
    }

    @Override
    public List<GameReward> getGameRewardsByUidAndType(Integer uid, String type) {
        return gameRewardMapper.getByUidAndType(uid, type);
    }

    @Override
    public void updateGameReward(GameReward gameReward) {
        gameRewardMapper.updateById(gameReward);
    }

    @Override
    public void deleteGameReward(Integer id) {
        gameRewardMapper.deleteById(id);
    }

    @Override
    public void deleteGameRewardsByGid(Integer gid) {
        gameRewardMapper.deleteByGid(gid);
    }
} 