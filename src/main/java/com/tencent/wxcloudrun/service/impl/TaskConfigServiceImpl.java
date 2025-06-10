package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.TaskConfigMapper;
import com.tencent.wxcloudrun.model.TaskConfig;
import com.tencent.wxcloudrun.service.TaskConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskConfigServiceImpl implements TaskConfigService {

    private static final Logger logger = LoggerFactory.getLogger(TaskConfigServiceImpl.class);

    private final TaskConfigMapper taskConfigMapper;

    @Autowired
    public TaskConfigServiceImpl(TaskConfigMapper taskConfigMapper) {
        this.taskConfigMapper = taskConfigMapper;
    }

    @Override
    public List<TaskConfig> getAllActiveTasks() {
        try {
            return taskConfigMapper.findAllActiveTasks();
        } catch (Exception e) {
            logger.error("Error getting all active tasks", e);
            throw new RuntimeException("Failed to get active tasks", e);
        }
    }

    @Override
    public TaskConfig getTaskByKey(String taskKey) {
        if (taskKey == null || taskKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Task key cannot be empty");
        }
        
        try {
            return taskConfigMapper.findByTaskKey(taskKey);
        } catch (Exception e) {
            logger.error("Error getting task by key: " + taskKey, e);
            throw new RuntimeException("Failed to get task by key", e);
        }
    }

    @Override
    public void addTaskConfig(TaskConfig taskConfig) {
        if (taskConfig == null) {
            throw new IllegalArgumentException("Task config cannot be null");
        }
        
        try {
            taskConfigMapper.insert(taskConfig);
            logger.info("Successfully added task config: {}", taskConfig.getTaskKey());
        } catch (Exception e) {
            logger.error("Error adding task config", e);
            throw new RuntimeException("Failed to add task config", e);
        }
    }

    @Override
    public void updateTaskConfig(TaskConfig taskConfig) {
        if (taskConfig == null || taskConfig.getId() == null) {
            throw new IllegalArgumentException("Task config and ID cannot be null");
        }
        
        try {
            taskConfigMapper.update(taskConfig);
            logger.info("Successfully updated task config: {}", taskConfig.getTaskKey());
        } catch (Exception e) {
            logger.error("Error updating task config", e);
            throw new RuntimeException("Failed to update task config", e);
        }
    }

    @Override
    public void deleteTaskConfig(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Task config ID cannot be null");
        }
        
        try {
            taskConfigMapper.deleteById(id);
            logger.info("Successfully deleted task config with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting task config with ID: " + id, e);
            throw new RuntimeException("Failed to delete task config", e);
        }
    }
} 