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
    
    // 根据uid和type查询
    GameConfig getByUidAndType(@Param("uid") Integer uid, @Param("type") String type);
    
    // 查询用户所有配置
    List<GameConfig> getByUid(@Param("uid") Integer uid);
    
    // 更新配置
    void updateById(GameConfig gameConfig);
    
    // 删除配置
    void deleteById(@Param("id") Integer id);
} 