package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WishLogRequest;
import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.model.WishLog;
import com.tencent.wxcloudrun.service.WishLogService;
import com.tencent.wxcloudrun.service.WishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("wishLog")
public class WishLogController {

    final WishLogService wishLogService;
    final WishService wishService;
    final Logger logger;

    public WishLogController(@Autowired WishLogService wishLogService, @Autowired WishService wishService) {
        this.wishLogService = wishLogService;
        this.wishService = wishService;
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
            Wish wish = wishService.getWishById(wishLog.getWid());
            Map<String,Object> res = new HashMap<>();
            res.put("wishLog", wishLog);
            res.put("wish", wish);
            return ApiResponse.ok(res);
        } catch (Exception e) {
            logger.error("查询愿望日志失败", e);
            return ApiResponse.error("查询失败");
        }
    }

    @GetMapping("/getAllLogByStatus/{mid}")
    ApiResponse getAllLogByStatus(@PathVariable Integer mid,@RequestParam Integer status) {
        try {
            List<WishLog> wishLogs = wishLogService.getAllLogByStatus(mid,status);
            return ApiResponse.ok(wishLogs);
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
     * 根据会员ID分页查询愿望日志列表
     * @param mid 会员ID
     * @param page 页码，默认1
     * @param pageSize 每页数量，默认10
     * @param status 状态过滤，可选
     * @return API响应
     */
    @GetMapping("/member/{mid}")
    ApiResponse getByMid(
            @PathVariable Integer mid,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = wishLogService.getByMidWithPage(mid, page, pageSize, status);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("分页查询愿望日志列表失败", e);
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
     * @param
     * @return API响应
     */
    @PostMapping
    ApiResponse create(@RequestBody WishLogRequest wishLogRequest) {
        try {
            // 参数校验
            if (wishLogRequest.getUid() == null || wishLogRequest.getWid() == null || wishLogRequest.getPoint() == null) {
                return ApiResponse.error("参数不完整");
            }
            WishLog wishLog = new WishLog();
            wishLog.setUid(wishLogRequest.getUid());
            wishLog.setWid(wishLogRequest.getWid());
            wishLog.setPoint(wishLogRequest.getPoint());
            wishLog.setMid(wishLogRequest.getMid());
            wishLog.setInfo(wishLogRequest.getInfo());
            wishLog.setAmount(wishLogRequest.getAmount());
            wishLog.setUnitType(wishLogRequest.getUnitType());
            wishLog.setUnit(wishLogRequest.getUnit());
            WishLog log = wishLogService.create(wishLog);
            return ApiResponse.ok(log);
        } catch (Exception e) {
            logger.error("创建愿望日志失败", e);
            return ApiResponse.error("创建失败");
        }
    }

    /**
     * 更新愿望日志
     * @param id 愿望日志ID
     * @param wishLogRequest 愿望日志对象
     * @return API响应
     */
    @PutMapping("/{id}")
    ApiResponse update(@PathVariable Integer id, @RequestBody WishLogRequest wishLogRequest) {
        try {
            // 检查是否存在
            WishLog existingLog = wishLogService.getById(id);
            if (existingLog == null) {
                return ApiResponse.error("愿望日志不存在");
            }

            existingLog.setStatus(wishLogRequest.getStatus());
            if(wishLogRequest.getEndTime() != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDateTime = LocalDateTime.parse(wishLogRequest.getEndTime(), formatter);
                existingLog.setEndTime(localDateTime);
            }
            wishLogService.update(existingLog);
            return ApiResponse.ok(existingLog);
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
