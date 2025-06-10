package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.UserDailyTaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserDailyTaskLogMapper {

    /**
     * 插入一条新的用户每日任务记录
     * @param userDailyTaskLog 任务记录实体
     * @return 影响的行数
     */
    int insert(UserDailyTaskLog userDailyTaskLog);

    /**
     * 根据openid和日期查询用户当日的任务，并按类型分组计数
     * @param openid 用户唯一标识
     * @param date 查询日期 (应为当天的日期)
     * @return 返回一个Map列表，每个Map包含 'type' 和 'count'
     *         例如: [{type=TASK_A, count=2}, {type=TASK_B, count=1}]
     */
    List<Map<String, Object>> countTasksByType(@Param("openid") String openid);

    /**
     * 获取用户当前的积分
     * @param openid 用户的OpenID，从请求头获取
     * @return 返回用户的积分
     */
    Integer getPoints(@Param("openid") String openid);

    /**
     * 获取用户当天的任务日志
     * @param openid 用户的OpenID
     * @return 用户当天的任务日志列表
     */
    List<UserDailyTaskLog> findTodayLogsByOpenid(@Param("openid") String openid);

    List<UserDailyTaskLog> findLimitedLogsByOpenid(String openid);

    List<UserDailyTaskLog> findClearLog();

    Integer getClearLogSum(String openid);

    void delClearLogs(String openid);
}
