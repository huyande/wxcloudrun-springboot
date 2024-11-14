package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WishLogRequest;
import com.tencent.wxcloudrun.model.WishLog;
import com.tencent.wxcloudrun.service.WishLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("wishLog")
public class WishLogController {

    final WishLogService wishLogService;
    final Logger logger;

    public WishLogController(@Autowired WishLogService wishLogService) {
        this.wishLogService = wishLogService;
        this.logger = LoggerFactory.getLogger(WishLogController.class);
    }

    /**
     * 根据ID查询愿望日志
     * @param id 愿望日志ID
     * @return API响应
     */
    @GetMapping("/{id}")
    ApiResponse get(@PathVariable Integer id) {
        try {
            WishLog wishLog = wishLogService.getById(id);
            if (wishLog == null) {
                return ApiResponse.error("愿望日志不存在");
            }
            return ApiResponse.ok(wishLog);
        } catch (Exception e) {
            logger.error("查询愿望日志失败", e);
            return ApiResponse.error("查询失败");
        }
    }

    /**
     * 根据用户ID查询愿望日志列表
     * @param uid 用户ID
     * @return API响应
     */
    @GetMapping("/user/{uid}")
    ApiResponse getByUid(@PathVariable String uid) {
        try {
            List<WishLog> wishLogs = wishLogService.getByUid(uid);
            return ApiResponse.ok(wishLogs);
        } catch (Exception e) {
            logger.error("查询用户愿望日志列表失败", e);
            return ApiResponse.error("查询失败");
        }
    }

    /**
     * 根据愿望ID查询愿望日志列表
     * @param wid 愿望ID
     * @return API响应
     */
    @GetMapping("/wish/{wid}")
    ApiResponse getByWid(@PathVariable Integer wid) {
        try {
            List<WishLog> wishLogs = wishLogService.getByWid(wid);
            return ApiResponse.ok(wishLogs);
        } catch (Exception e) {
            logger.error("查询愿望相关日志列表失败", e);
            return ApiResponse.error("查询失败");
        }
    }

    /**
     * 创建愿望日志
     * @param wishLog 愿望日志对象
     * @return API响应
     */
    @PostMapping
    ApiResponse create(@RequestBody WishLogRequest wishLogRequest) {
        try {
            // 参数校验
            if (wishLogRequest.getUid() == null || wishLogRequest.getWid() == null || wishLogRequest.getNum() == null) {
                return ApiResponse.error("参数不完整");
            }
            WishLog wishLog = new WishLog();
            wishLog.setUid(wishLogRequest.getUid());
            wishLog.setWid(wishLogRequest.getWid());
            wishLog.setNum(wishLogRequest.getNum());
            wishLog.setMid(wishLogRequest.getMid());
            wishLogService.create(wishLog);
            return ApiResponse.ok(wishLog);
        } catch (Exception e) {
            logger.error("创建愿望日志失败", e);
            return ApiResponse.error("创建失败");
        }
    }

    /**
     * 更新愿望日志
     * @param id 愿望日志ID
     * @param wishLog 愿望日志对象
     * @return API响应
     */
    @PutMapping("/{id}")
    ApiResponse update(@PathVariable Integer id, @RequestBody WishLog wishLog) {
        try {
            // 检查是否存在
            WishLog existingLog = wishLogService.getById(id);
            if (existingLog == null) {
                return ApiResponse.error("愿望日志不存在");
            }

            wishLog.setId(id);
            wishLogService.update(wishLog);
            return ApiResponse.ok(wishLog);
        } catch (Exception e) {
            logger.error("更新愿望日志失败", e);
            return ApiResponse.error("更新失败");
        }
    }

    /**
     * 删除愿望日志
     * @param id 愿望日志ID
     * @return API响应
     */
    @DeleteMapping("/{id}")
    ApiResponse delete(@PathVariable Integer id) {
        try {
            // 检查是否存在
            WishLog existingLog = wishLogService.getById(id);
            if (existingLog == null) {
                return ApiResponse.error("愿望日志不存在");
            }

            wishLogService.deleteById(id);
            return ApiResponse.ok();
        } catch (Exception e) {
            logger.error("删除愿望日志失败", e);
            return ApiResponse.error("删除失败");
        }
    }
}
