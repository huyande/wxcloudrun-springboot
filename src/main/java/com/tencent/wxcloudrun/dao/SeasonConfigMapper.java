package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SeasonConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeasonConfigMapper {
    
    /**
     * 根据ID获取赛季配置
     * @param id 赛季ID
     * @return 赛季配置
     */
    SeasonConfig getById(Long id);
    
    /**
     * 获取某成员的所有赛季
     * @param mId 成员ID
     * @return 赛季列表
     */
    List<SeasonConfig> getByMid(Integer mId);
    
    /**
     * 获取所有赛季
     * @return 赛季列表
     */
    List<SeasonConfig> getAll();
    
    /**
     * 获取进行中的赛季
     * @param mId 成员ID
     * @return 进行中的赛季列表
     */
    List<SeasonConfig> getActiveByMid(Integer mId);
    
    /**
     * 创建赛季
     * @param seasonConfig 赛季配置
     * @return 影响的行数
     */
    int insert(SeasonConfig seasonConfig);
    
    /**
     * 更新赛季
     * @param seasonConfig 赛季配置
     * @return 影响的行数
     */
    int update(SeasonConfig seasonConfig);
    
    /**
     * 更新赛季状态
     * @param id 赛季ID
     * @param status 状态
     * @return 影响的行数
     */
    int updateStatus(@Param("id") Long id, @Param("startTime") String startTime, @Param("status") Integer status);
    
    /**
     * 删除赛季
     * @param id 赛季ID
     * @return 影响的行数
     */
    int delete(Long id);
} 