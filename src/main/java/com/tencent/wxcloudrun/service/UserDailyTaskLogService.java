package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.TaskCountDto;
import com.tencent.wxcloudrun.dto.UserDailyTaskLogDto;
import com.tencent.wxcloudrun.model.UserDailyTaskLog;

import java.util.List;

public interface UserDailyTaskLogService {

    /**
     * 新增一条用户每日任务记录
     * @param openid 用户的OpenID，从请求头获取
     * @param taskLogDto 任务记录数据传输对象
     */
    void addTaskLog(String openid, UserDailyTaskLogDto taskLogDto,String famliyCode);

    /**
     * 获取用户当日各项任务的完成计数
     * @param openid 用户的OpenID，从请求头获取
     * @return 返回一个列表，包含每种任务类型及其当日完成次数
     */
    List<TaskCountDto> getDailyTaskCounts(String openid);

    /**
     * 获取用户当前的积分
     * @param openid 用户的OpenID，从请求头获取
     * @return 返回用户的积分
     */
    Integer getPoints(String openid);

    /**
     * 获取用户当天的任务日志
     * @param openid 用户的OpenID，从请求头获取
     * @return 返回用户当天的任务日志列表
     */
    List<UserDailyTaskLog> getTodayTaskLogs(String openid);

    List<UserDailyTaskLog> getLimitedLogs(String openid);

    void clearTaskLogs();

    /**
     * 获取待审核的任务日志
     * @param reviewContent 审核内容
     * @return 待审核的任务日志列表
     */
    List<UserDailyTaskLog> getPendingReviewLogs(String reviewContent);

    /**
     * 更新任务日志状态
     * @param id 日志ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateLogStatus(Long id, String status);

    void addShareJifen(String ownerOpenid, String fromOpenid);
}
