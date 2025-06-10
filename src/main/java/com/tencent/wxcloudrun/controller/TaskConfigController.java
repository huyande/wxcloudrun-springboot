package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.TaskConfig;
import com.tencent.wxcloudrun.service.TaskConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskConfigController {

    private static final Logger logger = LoggerFactory.getLogger(TaskConfigController.class);

    private final TaskConfigService taskConfigService;

    @Autowired
    public TaskConfigController(TaskConfigService taskConfigService) {
        this.taskConfigService = taskConfigService;
    }

    /**
     * 获取所有可用的任务配置
     * @return 任务配置列表
     */
    @GetMapping("/tasks")
    public ApiResponse getTasks() {
        try {
            List<TaskConfig> tasks = taskConfigService.getAllActiveTasks();
            logger.info("Successfully retrieved {} active tasks", tasks.size());
            return ApiResponse.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving active tasks", e);
            return ApiResponse.error("50001");
        }
    }
} 