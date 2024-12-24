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
    public ApiResponse createGameConfig(@RequestBody GameConfig gameConfig) {
        gameConfigService.createGameConfig(gameConfig);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse getGameConfigById(@PathVariable Integer id) {
        GameConfig gameConfig = gameConfigService.getGameConfigById(id);
        return ApiResponse.ok(gameConfig);
    }

    @GetMapping("/user")
    public ApiResponse getGameConfigByUidAndType(@RequestParam Integer uid, @RequestParam String type) {
        GameConfig gameConfig = gameConfigService.getGameConfigByUidAndType(uid, type);
        return ApiResponse.ok(gameConfig);
    }

    @GetMapping("/user/{uid}")
    public ApiResponse getGameConfigsByUid(@PathVariable Integer uid) {
        List<GameConfig> configs = gameConfigService.getGameConfigsByUid(uid);
        return ApiResponse.ok(configs);
    }

    @PutMapping("/{id}")
    public ApiResponse updateGameConfig(@PathVariable Integer id, @RequestBody GameConfig gameConfig) {
        gameConfig.setId(id);
        gameConfigService.updateGameConfig(gameConfig);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteGameConfig(@PathVariable Integer id) {
        gameConfigService.deleteGameConfig(id);
        return ApiResponse.ok();
    }

    @PostMapping("/with-rewards")
    public ApiResponse saveGameConfigWithRewards(@RequestBody GameConfigWithRewardsRequest request) {
        gameConfigService.saveGameConfigWithRewards(request);
        return ApiResponse.ok();
    }
} 