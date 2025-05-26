package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.GameRewardLogMapper;
import com.tencent.wxcloudrun.model.GameRewardLog;
import com.tencent.wxcloudrun.service.GameRewardLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class GameRewardLogServiceImpl implements GameRewardLogService {

    private final GameRewardLogMapper gameRewardLogMapper;

    public GameRewardLogServiceImpl(@Autowired GameRewardLogMapper gameRewardLogMapper) {
        this.gameRewardLogMapper = gameRewardLogMapper;
    }

    @Override
    public void createGameRewardLog(GameRewardLog gameRewardLog, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameRewardLog.setSeasonId(seasonId);
        }
        gameRewardLogMapper.insertOne(gameRewardLog);
    }

    @Override
    public GameRewardLog getGameRewardLogById(Integer id, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardLogMapper.getByIdAndSeasonId(id, seasonId);
        } else {
            // 常规模式
            return gameRewardLogMapper.getById(id);
        }
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByMid(Integer mid, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardLogMapper.getByMidAndSeasonId(mid, seasonId);
        } else {
            // 常规模式
            return gameRewardLogMapper.getByMid(mid);
        }
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByGameGroup(Integer gameGroup, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardLogMapper.getByGameGroupAndSeasonId(gameGroup, seasonId);
        } else {
            // 常规模式
            return gameRewardLogMapper.getByGameGroup(gameGroup);
        }
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByMidAndGameGroup(Integer mid, Integer gameGroup, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardLogMapper.getByMidAndGameGroupAndSeasonId(mid, gameGroup, seasonId);
        } else {
            // 常规模式
            return gameRewardLogMapper.getByMidAndGameGroup(mid, gameGroup);
        }
    }

    @Override
    public List<GameRewardLog> getGameRewardLogsByMidAndRewardType(Integer mid, String rewardType, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            return gameRewardLogMapper.getByMidAndRewardTypeAndSeasonId(mid, rewardType, seasonId);
        } else {
            // 常规模式
            return gameRewardLogMapper.getByMidAndRewardType(mid, rewardType);
        }
    }

    @Override
    public void updateStatus(Integer id, Long seasonId) {
        if (seasonId != null) {
            // 赛季模式
            gameRewardLogMapper.updateStatusByIdAndSeasonId(id, seasonId);
        } else {
            // 常规模式
            gameRewardLogMapper.updateStatus(id);
        }
    }

    @Override
    public Map<String, Object> getGameRewardLogsByMidAndGameGroupWithPage(
            Integer mid, 
            Integer gameGroup, 
            Integer pageNum, 
            Integer pageSize,
            Long seasonId) {
        
        // 获取总记录数
        int total;
        if (seasonId != null) {
            // 赛季模式
            total = gameRewardLogMapper.getByMidAndGameGroupAndSeasonIdCount(mid, gameGroup, seasonId);
        } else {
            // 常规模式
            total = gameRewardLogMapper.getByMidAndGameGroupCount(mid, gameGroup);
        }
        
        // 计算总页数
        int pages = (total + pageSize - 1) / pageSize;
        
        // 确保页码有效
        if (pageNum < 1) pageNum = 1;
        if (pageNum > pages) pageNum = pages;
        
        // 计算offset
        int offset = (pageNum - 1) * pageSize;
        
        // 获取分页数据
        Map<String, Object> params = new HashMap<>();
        params.put("mid", mid);
        params.put("gameGroup", gameGroup);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        params.put("seasonId", seasonId);
        
        List<GameRewardLog> list;
        if (seasonId != null) {
            // 赛季模式
            list = gameRewardLogMapper.getByMidAndGameGroupAndSeasonIdWithPage(params);
        } else {
            // 常规模式
            list = gameRewardLogMapper.getByMidAndGameGroupWithPage(params);
        }
        
        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pages", pages);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("list", list);
        
        return result;
    }
} 