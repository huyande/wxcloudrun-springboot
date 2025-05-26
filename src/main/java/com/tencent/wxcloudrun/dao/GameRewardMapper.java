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
    
    // 根据ID和赛季ID查询
    GameReward getByIdAndSeasonId(@Param("id") Integer id, @Param("seasonId") Long seasonId);
    
    // 根据游戏配置ID查询奖励列表
    List<GameReward> getByGid(@Param("gid") Integer gid);
    
    // 根据游戏配置ID和赛季ID查询奖励列表
    List<GameReward> getByGidAndSeasonId(@Param("gid") Integer gid, @Param("seasonId") Long seasonId);
    
    // 根据用户ID查询
    List<GameReward> getByUid(@Param("uid") Integer uid);
    
    // 根据用户ID和赛季ID查询
    List<GameReward> getByUidAndSeasonId(@Param("uid") Integer uid, @Param("seasonId") Long seasonId);
    
    // 根据游戏类型查询
    List<GameReward> getByType(@Param("type") String type);
    
    // 根据游戏类型和赛季ID查询
    List<GameReward> getByTypeAndSeasonId(@Param("type") String type, @Param("seasonId") Long seasonId);
    
    // 根据游戏配置ID和类型查询
    List<GameReward> getByGidAndType(@Param("gid") Integer gid, @Param("type") String type);
    
    // 根据游戏配置ID、类型和赛季ID查询
    List<GameReward> getByGidAndTypeAndSeasonId(@Param("gid") Integer gid, @Param("type") String type, @Param("seasonId") Long seasonId);
    
    // 根据用户ID和类型查询
    List<GameReward> getByUidAndType(@Param("uid") Integer uid, @Param("type") String type);
    
    // 根据用户ID、类型和赛季ID查询
    List<GameReward> getByUidAndTypeAndSeasonId(@Param("uid") Integer uid, @Param("type") String type, @Param("seasonId") Long seasonId);
    
    // 更新奖励
    void updateById(GameReward gameReward);
    
    // 根据ID和赛季ID更新奖励
    void updateByIdAndSeasonId(GameReward gameReward);
    
    // 删除奖励
    void deleteById(@Param("id") Integer id);
    
    // 根据ID和赛季ID删除奖励
    void deleteByIdAndSeasonId(@Param("id") Integer id, @Param("seasonId") Long seasonId);

    void deleteByGid(@Param("gid")Integer gid);
    
    void deleteByGidAndSeasonId(@Param("gid")Integer gid, @Param("seasonId") Long seasonId);
} 