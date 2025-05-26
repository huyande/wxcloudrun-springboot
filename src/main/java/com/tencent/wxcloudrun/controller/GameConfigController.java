package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.GameConfigWithRewardsRequest;
import com.tencent.wxcloudrun.model.GameConfig;
import com.tencent.wxcloudrun.service.GameConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-config")
public class GameConfigController {

    private final GameConfigService gameConfigService;

    public GameConfigController(@Autowired GameConfigService gameConfigService) {
        this.gameConfigService = gameConfigService;
    }

    @PostMapping
    public ApiResponse createGameConfig(@RequestBody GameConfig gameConfig,
                                       @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameConfigService.createGameConfig(gameConfig, seasonId);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse getGameConfigById(@PathVariable Integer id,
                                        @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        GameConfig gameConfig = gameConfigService.getGameConfigById(id, seasonId);
        return ApiResponse.ok(gameConfig);
    }

    @GetMapping("/user")
    public ApiResponse getGameConfigByUidAndType(@RequestParam Integer uid, 
                                                @RequestParam String type,
                                                @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        GameConfig gameConfig = gameConfigService.getGameConfigByUidAndType(uid, type, seasonId);
        return ApiResponse.ok(gameConfig);
    }

    @GetMapping("/user/{uid}")
    public ApiResponse getGameConfigsByUid(@PathVariable Integer uid,
                                          @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        List<GameConfig> configs = gameConfigService.getGameConfigsByUid(uid, seasonId);
        return ApiResponse.ok(configs);
    }

    @PutMapping("/{id}")
    public ApiResponse updateGameConfig(@PathVariable Integer id, 
                                       @RequestBody GameConfig gameConfig,
                                       @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameConfig.setId(id);
        gameConfigService.updateGameConfig(gameConfig, seasonId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteGameConfig(@PathVariable Integer id,
                                       @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameConfigService.deleteGameConfig(id, seasonId);
        return ApiResponse.ok();
    }

    @PostMapping("/with-rewards")
    public ApiResponse saveGameConfigWithRewards(@RequestBody GameConfigWithRewardsRequest request,
                                                 @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        gameConfigService.saveGameConfigWithRewards(request, seasonId);
        return ApiResponse.ok();
    }
} 