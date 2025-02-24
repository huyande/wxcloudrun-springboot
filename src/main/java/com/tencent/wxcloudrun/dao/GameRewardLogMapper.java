package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.GameRewardLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GameRewardLogMapper {
    // 创建游戏中奖记录
    void insertOne(GameRewardLog gameRewardLog);
    
    // 根据ID查询
    GameRewardLog getById(@Param("id") Integer id);
    
    // 根据成员ID查询
    List<GameRewardLog> getByMid(@Param("mid") Integer mid);
    
    // 根据游戏分组查询
    List<GameRewardLog> getByGameGroup(@Param("gameGroup") Integer gameGroup);
    
    // 根据成员ID和游戏分组查询
    List<GameRewardLog> getByMidAndGameGroup(@Param("mid") Integer mid, @Param("gameGroup") Integer gameGroup);
    
    // 根据成员ID和奖励类型查询
    List<GameRewardLog> getByMidAndRewardType(@Param("mid") Integer mid, @Param("rewardType") String rewardType);

    // 更新状态
    void updateStatus(@Param("id") Integer id);

    int getByMidAndGameGroupCount(@Param("mid") Integer mid, @Param("gameGroup")Integer gameGroup);

    List<GameRewardLog> getByMidAndGameGroupWithPage(Map<String, Object> params);
}