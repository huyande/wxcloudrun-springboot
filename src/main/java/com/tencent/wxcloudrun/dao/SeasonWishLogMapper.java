package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonWishLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SeasonWishLogMapper {
    
    /**
     * 根据ID获取赛季愿望日志
     * @param id 日志ID
     * @return 赛季愿望日志
     */
    SeasonWishLog getById(Long id);
    
    /**
     * 根据ID和赛季ID获取赛季愿望日志
     * @param id 日志ID
     * @param seasonId 赛季ID
     * @return 赛季愿望日志
     */
    SeasonWishLog getByIdAndSeasonId(@Param("id") Long id, @Param("seasonId") Long seasonId);
    
    /**
     * 获取某赛季某成员的所有愿望日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 日志列表
     */
    List<SeasonWishLog> getBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某愿望的所有日志
     * @param seasonId 赛季ID
     * @param wid 愿望ID
     * @return 日志列表
     */
    List<SeasonWishLog> getBySeasonIdAndWid(@Param("seasonId") Long seasonId, @Param("wid") Long wid);
    
    /**
     * 获取某赛季某成员某愿望的所有日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param wid 愿望ID
     * @return 日志列表
     */
    List<SeasonWishLog> getBySeasonIdAndMidAndWid(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("wid") Long wid);
    
    /**
     * 获取某赛季某成员某状态的愿望日志数量
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param status 状态
     * @return 日志数量
     */
    int getCountBySeasonIdAndMidAndStatus(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("status") Integer status);
    
    /**
     * 分页获取某赛季某成员的愿望日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param status 状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 日志列表
     */
    List<Map<String, Object>> getBySeasonIdAndMidWithPage(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("status") Integer status,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);
    
    /**
     * 创建愿望日志
     * @param seasonWishLog 愿望日志
     * @return 影响的行数
     */
    int insert(SeasonWishLog seasonWishLog);
    
    /**
     * 更新愿望日志
     * @param seasonWishLog 愿望日志
     * @return 影响的行数
     */
    int update(SeasonWishLog seasonWishLog);
    
    /**
     * 更新愿望日志状态
     * @param id 日志ID
     * @param status 状态
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除愿望日志
     * @param id 日志ID
     * @return 影响的行数
     */
    int delete(Long id);
    
    /**
     * 根据ID和赛季ID删除愿望日志
     * @param id 日志ID
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteByIdAndSeasonId(@Param("id") Long id, @Param("seasonId") Long seasonId);
    
    /**
     * 获取某赛季某成员的愿望消耗积分总和
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 消耗积分总和
     */
    int getTotalPointsBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员某状态的所有愿望日志
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param status 状态
     * @return 日志列表
     */
    List<SeasonWishLog> getBySeasonIdAndMidAndStatus(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("status") Integer status);
    
    // 赛季统计：心愿兑换排行
    List<Map<String, Object>> getTopWishesByDateRange(@Param("seasonId") Long seasonId, @Param("mid") Integer mid, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("limit") Integer limit);
    
    /**
     * 根据赛季ID删除所有愿望日志
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteBySeasonId(Long seasonId);

    Integer getSumNumBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);

    int countUserExchanges(@Param("id")Long id, @Param("startTime")LocalDateTime startTime, @Param("seasonId")Long seasonId);

    // ========== 周报功能新增查询方法 ==========

    /**
     * 获取赛季模式下指定时间范围内的心愿消费积分总数
     * 
     * @param seasonId 赛季ID
     * @param mid 会员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 心愿消费积分总数
     */
    Integer getConsumedPointsByDateRange(@Param("seasonId") Long seasonId,
                                        @Param("mid") Integer mid,
                                        @Param("startDate") String startDate, 
                                        @Param("endDate") String endDate);
}