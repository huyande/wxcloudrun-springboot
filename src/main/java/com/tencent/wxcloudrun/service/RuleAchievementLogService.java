package com.tencent.wxcloudrun.service;

import java.util.List;
import java.util.Map;

public interface RuleAchievementLogService {
    /**
     * 根据ID获取成就日志
     * @param id 日志ID
     * @param seasonId 赛季ID（可选）
     * @param expectedType 期望返回的类型
     * @return 成就日志
     */
    <T> T getById(Integer id, Long seasonId, Class<T> expectedType);

    /**
     * 获取成员的成就日志列表
     * @param mid 成员ID
     * @param seasonId 赛季ID（可选）
     * @param expectedType 期望返回的类型
     * @return 成就日志列表
     */
    <T> List<T> getByMemberId(Integer mid, Long seasonId, Class<T> expectedType);

    /**
     * 获取某个成就的所有日志
     * @param raId 成就ID
     * @param seasonId 赛季ID（可选）
     * @param expectedType 期望返回的类型
     * @return 成就日志列表
     */
    <T> List<T> getByAchievementId(Integer raId, Long seasonId, Class<T> expectedType);

    /**
     * 获取成员的成就列表（包含成就详情）
     * @param mid 成员ID
     * @param seasonId 赛季ID（可选）
     * @return 成就详情列表
     */
    List<Map<String, Object>> getMemberAchievements(Integer mid, Long seasonId);

    /**
     * 创建成就日志
     * @param log 日志对象
     * @param seasonId 赛季ID（可选）
     * @return 创建结果
     */
    Object insert(Object log, Long seasonId);

    /**
     * 删除成就日志
     * @param id 日志ID
     * @param seasonId 赛季ID（可选）
     * @return 是否成功
     */
    boolean deleteById(Integer id, Long seasonId);

    /**
     * 删除成员某个成就的日志
     * @param raId 成就ID
     * @param mid 成员ID
     * @param seasonId 赛季ID（可选）
     * @return 是否成功
     */
    boolean deleteByAchievementAndMember(Integer raId, Integer mid, Long seasonId);
} 