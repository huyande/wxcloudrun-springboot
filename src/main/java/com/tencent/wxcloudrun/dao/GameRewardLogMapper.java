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
    
    // 根据ID和赛季ID查询
    GameRewardLog getByIdAndSeasonId(@Param("id") Integer id, @Param("seasonId") Long seasonId);
    
    // 根据成员ID查询
    List<GameRewardLog> getByMid(@Param("mid") Integer mid);
    
    // 根据成员ID和赛季ID查询
    List<GameRewardLog> getByMidAndSeasonId(@Param("mid") Integer mid, @Param("seasonId") Long seasonId);
    
    // 根据游戏分组查询
    List<GameRewardLog> getByGameGroup(@Param("gameGroup") Integer gameGroup);
    
    // 根据游戏分组和赛季ID查询
    List<GameRewardLog> getByGameGroupAndSeasonId(@Param("gameGroup") Integer gameGroup, @Param("seasonId") Long seasonId);
    
    // 根据成员ID和游戏分组查询
    List<GameRewardLog> getByMidAndGameGroup(@Param("mid") Integer mid, @Param("gameGroup") Integer gameGroup);
    
    // 根据成员ID、游戏分组和赛季ID查询
    List<GameRewardLog> getByMidAndGameGroupAndSeasonId(@Param("mid") Integer mid, @Param("gameGroup") Integer gameGroup, @Param("seasonId") Long seasonId);
    
    // 根据成员ID和奖励类型查询
    List<GameRewardLog> getByMidAndRewardType(@Param("mid") Integer mid, @Param("rewardType") String rewardType);
    
    // 根据成员ID、奖励类型和赛季ID查询
    List<GameRewardLog> getByMidAndRewardTypeAndSeasonId(@Param("mid") Integer mid, @Param("rewardType") String rewardType, @Param("seasonId") Long seasonId);

    // 更新状态
    void updateStatus(@Param("id") Integer id);
    
    // 根据ID和赛季ID更新状态
    void updateStatusByIdAndSeasonId(@Param("id") Integer id, @Param("seasonId") Long seasonId);

    int getByMidAndGameGroupCount(@Param("mid") Integer mid, @Param("gameGroup")Integer gameGroup);
    
    int getByMidAndGameGroupAndSeasonIdCount(@Param("mid") Integer mid, @Param("gameGroup")Integer gameGroup, @Param("seasonId") Long seasonId);

    List<GameRewardLog> getByMidAndGameGroupWithPage(Map<String, Object> params);
    
    List<GameRewardLog> getByMidAndGameGroupAndSeasonIdWithPage(Map<String, Object> params);

    /**
     * 获取指定时间范围内的积分抽奖总次数
     * 
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 抽奖总次数
     */
    Integer getLotteryCountByDateRange(@Param("mid") Integer mid, 
                                      @Param("startDate") String startDate, 
                                      @Param("endDate") String endDate);

    void deleteByMid(@Param("mid") Integer mid);
}