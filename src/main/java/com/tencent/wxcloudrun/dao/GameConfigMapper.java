package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.GameConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameConfigMapper {
    // 创建游戏配置
    void insertOne(GameConfig gameConfig);
    
    // 根据ID查询
    GameConfig getById(@Param("id") Integer id);
    
    // 根据ID和赛季ID查询
    GameConfig getByIdAndSeasonId(@Param("id") Integer id, @Param("seasonId") Long seasonId);
    
    // 根据uid和type查询
    GameConfig getByUidAndType(@Param("uid") Integer uid, @Param("type") String type);
    
    // 根据uid、type和赛季ID查询
    GameConfig getByUidAndTypeAndSeasonId(@Param("uid") Integer uid, @Param("type") String type, @Param("seasonId") Long seasonId);
    
    // 查询用户所有配置
    List<GameConfig> getByUid(@Param("uid") Integer uid);
    
    // 根据用户ID和赛季ID查询所有配置
    List<GameConfig> getByUidAndSeasonId(@Param("uid") Integer uid, @Param("seasonId") Long seasonId);
    
    // 更新配置
    void updateById(GameConfig gameConfig);
    
    // 根据ID和赛季ID更新配置
    void updateByIdAndSeasonId(GameConfig gameConfig);
    
    // 删除配置
    void deleteById(@Param("id") Integer id);
    
    // 根据ID和赛季ID删除配置
    void deleteByIdAndSeasonId(@Param("id") Integer id, @Param("seasonId") Long seasonId);
} 