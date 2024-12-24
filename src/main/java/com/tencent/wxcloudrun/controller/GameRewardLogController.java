package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.GameRewardLog;
import com.tencent.wxcloudrun.service.GameRewardLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-reward-log")
public class GameRewardLogController {

    private final GameRewardLogService gameRewardLogService;

    public GameRewardLogController(@Autowired GameRewardLogService gameRewardLogService) {
        this.gameRewardLogService = gameRewardLogService;
    }

    @PostMapping
    public ApiResponse createGameRewardLog(@RequestBody GameRewardLog gameRewardLog) {
        gameRewardLogService.createGameRewardLog(gameRewardLog);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse getGameRewardLogById(@PathVariable Integer id) {
        GameRewardLog log = gameRewardLogService.getGameRewardLogById(id);
        return ApiResponse.ok(log);
    }

    @GetMapping("/member/{mid}")
    public ApiResponse getGameRewardLogsByMid(@PathVariable Integer mid) {
        List<GameRewardLog> logs = gameRewardLogService.getGameRewardLogsByMid(mid);
        return ApiResponse.ok(logs);
    }

    @GetMapping("/group/{gameGroup}")
    public ApiResponse getGameRewardLogsByGameGroup(@PathVariable Integer gameGroup) {
        List<GameRewardLog> logs = gameRewardLogService.getGameRewardLogsByGameGroup(gameGroup);
        return ApiResponse.ok(logs);
    }

    @GetMapping("/member/{mid}/group/{gameGroup}")
    public ApiResponse getGameRewardLogsByMidAndGameGroup(
            @PathVariable Integer mid,
            @PathVariable Integer gameGroup) {
        List<GameRewardLog> logs = gameRewardLogService.getGameRewardLogsByMidAndGameGroup(mid, gameGroup);
        return ApiResponse.ok(logs);
    }

    @GetMapping("/member/{mid}/type/{rewardType}")
    public ApiResponse getGameRewardLogsByMidAndRewardType(
            @PathVariable Integer mid,
            @PathVariable String rewardType) {
        List<GameRewardLog> logs = gameRewardLogService.getGameRewardLogsByMidAndRewardType(mid, rewardType);
        return ApiResponse.ok(logs);
    }
} 