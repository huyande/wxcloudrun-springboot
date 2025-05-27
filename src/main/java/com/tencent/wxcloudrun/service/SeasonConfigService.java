package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.SeasonConfig;

import java.util.List;

/**
 * 赛季配置服务接口
 */
public interface SeasonConfigService {

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
     * 获取进行中的赛季
     * @param mId 成员ID
     * @return 进行中的赛季列表
     */
    List<SeasonConfig> getActiveByMid(Integer mId);

    /**
     * 获取某成员当前活跃的赛季
     * @param mId 成员ID
     * @return 当前活跃的赛季配置，如果没有则返回null
     */
    SeasonConfig getActiveSeasonByMember(Integer mId);

    /**
     * 创建赛季
     * @param seasonConfig 赛季配置
     * @return 创建后的赛季配置
     */
    SeasonConfig createSeason(SeasonConfig seasonConfig);

    /**
     * 更新赛季
     * @param seasonConfig 赛季配置
     * @return 更新后的赛季配置
     */
    SeasonConfig updateSeason(SeasonConfig seasonConfig);

    /**
     * 更新赛季状态
     * @param id 赛季ID
     * @param status 状态
     * @return 更新后的赛季配置
     */
    SeasonConfig updateStatus(Long id, Integer status);

    /**
     * 启动赛季
     * @param id 赛季ID
     * @return 更新后的赛季配置
     */
    SeasonConfig startSeason(Integer mId, Long id);

    /**
     * 结束赛季
     * @param id 赛季ID
     * @return 更新后的赛季配置
     */
    SeasonConfig endSeason(Long id);

    /**
     * 归档赛季
     * @param id 赛季ID
     * @return 更新后的赛季配置
     */
    SeasonConfig archiveSeason(Long id);

    /**
     * 删除赛季
     * @param id 赛季ID
     * @return 是否成功
     */
    boolean deleteSeason(Long id);

    /**
     * 切换活跃赛季
     * 将先把当前活跃的赛季(status=1)改为已结束(status=2)，
     * 然后将指定ID的赛季设为活跃(status=1)，
     * 并更新成员的currentSeasonId
     * 
     * @param mId 成员ID
     * @param newSeasonId 新赛季ID
     * @return 已激活的赛季配置
     */
    SeasonConfig switchActiveSeason(Integer mId, Long newSeasonId);


    /**
     * 清空赛季数据
     * @param mId 成员ID
     * @param id 赛季ID
     * @return 是否成功
     */
    boolean clearSeason(Long id);

    void clearMemberSeason(Integer id);
} 