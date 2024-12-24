package com.tencent.wxcloudrun.dto;

import com.tencent.wxcloudrun.model.GameConfig;
import com.tencent.wxcloudrun.model.GameReward;
import lombok.Data;

import java.util.List;

@Data
public class GameConfigWithRewardsRequest {
    private GameConfig gameConfig;
    private List<GameReward> rewards;
} 