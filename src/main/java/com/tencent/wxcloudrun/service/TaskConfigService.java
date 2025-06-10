package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.TaskConfig;

import java.util.List;

public interface TaskConfigService {

    /**
     * 获取所有有效的任务配置（含限时任务过期检查）
     * @return 任务配置列表
     */
    List<TaskConfig> getAllActiveTasks();

    /**
     * 根据任务key查询任务配置
     * @param taskKey 任务唯一标识
     * @return 任务配置
     */
    TaskConfig getTaskByKey(String taskKey);

    /**
     * 新增任务配置
     * @param taskConfig 任务配置
     */
    void addTaskConfig(TaskConfig taskConfig);

    /**
     * 更新任务配置
     * @param taskConfig 任务配置
     */
    void updateTaskConfig(TaskConfig taskConfig);

    /**
     * 删除任务配置
     * @param id 任务配置ID
     */
    void deleteTaskConfig(Long id);
} 