package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CourseLogRequest;
import com.tencent.wxcloudrun.model.CourseLog;
import com.tencent.wxcloudrun.service.CourseLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/course-log")
public class CourseLogController {

    private final Logger logger = LoggerFactory.getLogger(CourseLogController.class);
    
    @Autowired
    private CourseLogService courseLogService;

    /**
     * 添加课时记录
     */
    @PostMapping("/add")
    public ApiResponse addCourseLog(@RequestBody CourseLogRequest courseLogRequest, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            CourseLog courseLog = courseLogService.addCourseLog(courseLogRequest,seasonId);
            return ApiResponse.ok(courseLog);
        } catch (Exception e) {
            logger.error("添加课时记录失败", e);
            return ApiResponse.error("添加课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除课时记录
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteCourseLog(@PathVariable Integer id, @RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        try {
            courseLogService.deleteCourseLog(id,seasonId);
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除课时记录失败", e);
            return ApiResponse.error("删除课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取课时记录详情
     */
    @GetMapping("/detail/{id}")
    public ApiResponse getCourseLogDetail(@PathVariable Integer id) {
        try {
            CourseLog courseLog = courseLogService.getCourseLogById(id);
            if (courseLog == null) {
                return ApiResponse.error("课时记录不存在");
            }
            return ApiResponse.ok(courseLog);
        } catch (Exception e) {
            logger.error("获取课时记录详情失败", e);
            return ApiResponse.error("获取课时记录详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据课程ID获取课时记录列表
     */
    @GetMapping("/list/course/{courseId}")
    public ApiResponse getCourseLogsByCourseId(@PathVariable Integer courseId) {
        try {
            List<CourseLog> logs = courseLogService.getCourseLogsByCourseId(courseId);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取课程课时记录失败", e);
            return ApiResponse.error("获取课程课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 根据成员ID获取课时记录列表
     */
    @GetMapping("/list/member/{mid}")
    public ApiResponse getCourseLogsByMid(@PathVariable Integer mid) {
        try {
            List<CourseLog> logs = courseLogService.getCourseLogsByMid(mid);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取成员课时记录失败", e);
            return ApiResponse.error("获取成员课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定日期范围的课时记录
     */
    @GetMapping("/list/member/{mid}/date-range")
    public ApiResponse getCourseLogsByDateRange(
            @PathVariable Integer mid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<CourseLog> logs = courseLogService.getCourseLogsByDateRange(mid, startDate, endDate);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取日期范围课时记录失败", e);
            return ApiResponse.error("获取日期范围课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近的课时记录
     */
    @GetMapping("/list/member/{mid}/recent")
    public ApiResponse getRecentCourseLogsByMid(
            @PathVariable Integer mid,
            @RequestParam(defaultValue = "10", required = false) Integer limit) {
        try {
            List<CourseLog> logs = courseLogService.getRecentCourseLogsByMid(mid, limit);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("获取最近课时记录失败", e);
            return ApiResponse.error("获取最近课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 检查某天是否已有课时记录
     */
    @GetMapping("/check-date/{courseId}")
    public ApiResponse checkLogOnDate(
            @PathVariable Integer courseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lessonDate) {
        try {
            boolean hasLog = courseLogService.hasLogOnDate(courseId, lessonDate);
            return ApiResponse.ok(hasLog);
        } catch (Exception e) {
            logger.error("检查课时记录失败", e);
            return ApiResponse.error("检查课时记录失败：" + e.getMessage());
        }
    }

    /**
     * 检查课时记录权限
     */
    @GetMapping("/check-permission/{logId}/{mid}")
    public ApiResponse checkCourseLogPermission(@PathVariable Integer logId, @PathVariable Integer mid) {
        try {
            boolean hasPermission = courseLogService.checkCourseLogPermission(logId, mid);
            return ApiResponse.ok(hasPermission);
        } catch (Exception e) {
            logger.error("检查课时记录权限失败", e);
            return ApiResponse.error("检查课时记录权限失败：" + e.getMessage());
        }
    }
} 