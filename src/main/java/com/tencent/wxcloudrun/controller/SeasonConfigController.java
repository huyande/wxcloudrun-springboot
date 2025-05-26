package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.SeasonConfig;
import com.tencent.wxcloudrun.service.SeasonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 赛季配置控制器
 */
@RestController
@RequestMapping("/api/season/config")
public class SeasonConfigController {

    private final SeasonConfigService seasonConfigService;

    @Autowired
    public SeasonConfigController(SeasonConfigService seasonConfigService) {
        this.seasonConfigService = seasonConfigService;
    }

    /**
     * 创建赛季配置
     * @param seasonConfig 赛季配置信息
     * @return API响应
     */
    @PostMapping
    public ApiResponse createSeason(@RequestBody SeasonConfig seasonConfig) {
        SeasonConfig createdSeason = seasonConfigService.createSeason(seasonConfig);
        return ApiResponse.ok(createdSeason);
    }

    /**
     * 更新赛季配置
     * @param configId 赛季配置ID
     * @param seasonConfig 赛季配置信息
     * @return API响应
     */
    @PutMapping("/{configId}")
    public ApiResponse updateSeason(
            @PathVariable("configId") Long configId,
            @RequestBody SeasonConfig seasonConfig) {
        
        // 确保ID一致
        seasonConfig.setId(configId);
        
        SeasonConfig updatedSeason = seasonConfigService.updateSeason(seasonConfig);
        return ApiResponse.ok(updatedSeason);
    }

    /**
     * 获取赛季配置详情
     * @param configId 赛季配置ID
     * @return API响应
     */
    @GetMapping("/{configId}")
    public ApiResponse getSeason(@PathVariable("configId") Long configId) {
        SeasonConfig season = seasonConfigService.getById(configId);
        if (season == null) {
            return ApiResponse.error("赛季不存在");
        }
        return ApiResponse.ok(season);
    }

    /**
     * 获取某成员的所有赛季配置
     * @param mid 成员ID
     * @return API响应
     */
    @GetMapping("/member/{mid}")
    public ApiResponse getSeasonsByMid(@PathVariable("mid") Integer mid) {
        List<SeasonConfig> seasons = seasonConfigService.getByMid(mid);
        return ApiResponse.ok(seasons);
    }

    /**
     * 获取某成员的所有活跃赛季配置
     * @param mid 成员ID
     * @return API响应
     */
    @GetMapping("/member/{mid}/active")
    public ApiResponse getActiveSeasonsByMid(@PathVariable("mid") Integer mid) {
        List<SeasonConfig> activeSeasons = seasonConfigService.getActiveByMid(mid);
        return ApiResponse.ok(activeSeasons);
    }

    /**
     * 启动赛季
     * @param configId 赛季配置ID
     * @return API响应
     */
    @PostMapping("/{mid}/{configId}/start")
    public ApiResponse startSeason(@PathVariable("mid") Integer mid,@PathVariable("configId") Long configId) {
        try {
            SeasonConfig startedSeason = seasonConfigService.startSeason(mid,configId);
            return ApiResponse.ok(startedSeason);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 结束赛季
     * @param configId 赛季配置ID
     * @return API响应
     */
    @PostMapping("/{configId}/end")
    public ApiResponse endSeason(@PathVariable("configId") Long configId) {
        try {
            SeasonConfig endedSeason = seasonConfigService.endSeason(configId);
            return ApiResponse.ok(endedSeason);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 归档赛季
     * @param configId 赛季配置ID
     * @return API响应
     */
    @PostMapping("/{configId}/archive")
    public ApiResponse archiveSeason(@PathVariable("configId") Long configId) {
        try {
            SeasonConfig archivedSeason = seasonConfigService.archiveSeason(configId);
            return ApiResponse.ok(archivedSeason);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除赛季
     * @param configId 赛季配置ID
     * @return API响应
     */
    @DeleteMapping("/del/{configId}")
    public ApiResponse deleteSeason(@PathVariable("configId") Long configId) {
        try {
            boolean success = seasonConfigService.deleteSeason(configId);
            if (success) {
                return ApiResponse.ok();
            } else {
                return ApiResponse.error("赛季不存在或删除失败");
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新赛季状态
     * @param configId 赛季配置ID
     * @param status 状态
     * @return API响应
     */
    @PutMapping("/{configId}/status")
    public ApiResponse updateSeasonStatus(
            @PathVariable("configId") Long configId,
            @RequestParam("status") Integer status) {
        
        try {
            SeasonConfig updatedSeason = seasonConfigService.updateStatus(configId, status);
            return ApiResponse.ok(updatedSeason);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
} 