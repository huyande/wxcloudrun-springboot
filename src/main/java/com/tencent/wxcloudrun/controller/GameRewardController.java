package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.GameReward;
import com.tencent.wxcloudrun.service.GameRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-reward")
public class GameRewardController {

    private final GameRewardService gameRewardService;

    public GameRewardController(@Autowired GameRewardService gameRewardService) {
        this.gameRewardService = gameRewardService;
    }

    @PostMapping
    public ApiResponse createGameReward(@RequestBody GameReward gameReward,
                                       @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameRewardService.createGameReward(gameReward, seasonId);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse getGameRewardById(@PathVariable Integer id,
                                        @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        GameReward gameReward = gameRewardService.getGameRewardById(id, seasonId);
        return ApiResponse.ok(gameReward);
    }

    @GetMapping("/game/{gid}")
    public ApiResponse getGameRewardsByGid(@PathVariable Integer gid,
                                          @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<GameReward> rewards = gameRewardService.getGameRewardsByGid(gid, seasonId);
        return ApiResponse.ok(rewards);
    }

    @GetMapping("/user/{uid}")
    public ApiResponse getGameRewardsByUid(@PathVariable Integer uid,
                                          @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<GameReward> rewards = gameRewardService.getGameRewardsByUid(uid, seasonId);
        return ApiResponse.ok(rewards);
    }

    @GetMapping("/type/{type}")
    public ApiResponse getGameRewardsByType(@PathVariable String type,
                                           @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<GameReward> rewards = gameRewardService.getGameRewardsByType(type, seasonId);
        return ApiResponse.ok(rewards);
    }

    @GetMapping("/game/{gid}/type/{type}")
    public ApiResponse getGameRewardsByGidAndType(@PathVariable Integer gid, 
                                                 @PathVariable String type,
                                                 @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<GameReward> rewards = gameRewardService.getGameRewardsByGidAndType(gid, type, seasonId);
        return ApiResponse.ok(rewards);
    }

    @GetMapping("/user/{uid}/type/{type}")
    public ApiResponse getGameRewardsByUidAndType(@PathVariable Integer uid, 
                                                 @PathVariable String type,
                                                 @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<GameReward> rewards = gameRewardService.getGameRewardsByUidAndType(uid, type, seasonId);
        return ApiResponse.ok(rewards);
    }

    @PutMapping("/{id}")
    public ApiResponse updateGameReward(@PathVariable Integer id, 
                                       @RequestBody GameReward gameReward,
                                       @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameReward.setId(id);
        gameRewardService.updateGameReward(gameReward, seasonId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteGameReward(@PathVariable Integer id,
                                       @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameRewardService.deleteGameReward(id, seasonId);
        return ApiResponse.ok();
    }
} 