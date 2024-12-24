package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.GameReward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameRewardMapper {
    // 创建游戏奖励
    void insertOne(GameReward gameReward);
    
    // 根据ID查询
    GameReward getById(@Param("id") Integer id);
    
    // 根据游戏配置ID查询奖励列表
    List<GameReward> getByGid(@Param("gid") Integer gid);
    
    // 根据用户ID查询
    List<GameReward> getByUid(@Param("uid") Integer uid);
    
    // 根据游戏类型查询
    List<GameReward> getByType(@Param("type") String type);
    
    // 根据游戏配置ID和类型查询
    List<GameReward> getByGidAndType(@Param("gid") Integer gid, @Param("type") String type);
    
    // 根据用户ID和类型查询
    List<GameReward> getByUidAndType(@Param("uid") Integer uid, @Param("type") String type);
    
    // 更新奖励
    void updateById(GameReward gameReward);
    
    // 删除奖励
    void deleteById(@Param("id") Integer id);

    void deleteByGid(@Param("gid")Integer gid);
} 