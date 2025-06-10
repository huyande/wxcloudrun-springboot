package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.TaskConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskConfigMapper {

    /**
     * 获取所有有效的任务配置
     * @return 任务配置列表
     */
    List<TaskConfig> findAllActiveTasks();

    /**
     * 根据任务key查询任务配置
     * @param taskKey 任务唯一标识
     * @return 任务配置
     */
    TaskConfig findByTaskKey(@Param("taskKey") String taskKey);

    /**
     * 插入新的任务配置
     * @param taskConfig 任务配置
     * @return 影响的行数
     */
    int insert(TaskConfig taskConfig);

    /**
     * 更新任务配置
     * @param taskConfig 任务配置
     * @return 影响的行数
     */
    int update(TaskConfig taskConfig);

    /**
     * 删除任务配置 
     * @param id 任务配置ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
} 