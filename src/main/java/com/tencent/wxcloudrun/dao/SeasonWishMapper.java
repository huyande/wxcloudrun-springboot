package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonWish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeasonWishMapper {
    
    /**
     * 根据ID获取赛季愿望
     * @param id 愿望ID
     * @return 赛季愿望
     */
    SeasonWish getById(Long id);
    
    /**
     * 获取某赛季某成员的所有愿望
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 愿望列表
     */
    List<SeasonWish> getBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员的所有待实现愿望
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 愿望列表
     */
    List<SeasonWish> getPendingBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员的所有已实现愿望
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 愿望列表
     */
    List<SeasonWish> getFulfilledBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 按类型获取愿望
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @param type 类型
     * @return 愿望列表
     */
    List<SeasonWish> getBySeasonIdAndMidAndType(
            @Param("seasonId") Long seasonId,
            @Param("mid") Integer mid,
            @Param("type") Integer type);
    
    /**
     * 创建愿望
     * @param seasonWish 愿望
     * @return 影响的行数
     */
    int insert(SeasonWish seasonWish);
    
    /**
     * 更新愿望
     * @param seasonWish 愿望
     * @return 影响的行数
     */
    int update(SeasonWish seasonWish);
    
    /**
     * 更新愿望状态
     * @param id 愿望ID
     * @param status 状态
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除愿望
     * @param id 愿望ID
     * @return 影响的行数
     */
    int delete(Long id);
    
    /**
     * 获取某赛季某成员的愿望统计
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 愿望统计
     */
    int getWishCountBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);
    
    /**
     * 获取某赛季某成员的已实现愿望数量
     * @param seasonId 赛季ID
     * @param mid 成员ID
     * @return 已实现愿望数量
     */
    int getFulfilledWishCountBySeasonIdAndMid(@Param("seasonId") Long seasonId, @Param("mid") Integer mid);

    /**
     * 获取某赛季的所有愿望
     * @param seasonId 赛季ID
     * @return 愿望列表
     */
    List<SeasonWish> findAllBySeasonId(@Param("seasonId") Long seasonId);

    /**
     * 根据赛季ID删除所有愿望
     * @param seasonId 赛季ID
     * @return 影响的行数
     */
    int deleteBySeasonId(Long seasonId);
} 