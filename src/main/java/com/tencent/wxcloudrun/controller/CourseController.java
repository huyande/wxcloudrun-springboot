package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CourseRequest;
import com.tencent.wxcloudrun.dto.CourseStatsDto;
import com.tencent.wxcloudrun.model.Course;
import com.tencent.wxcloudrun.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final Logger logger = LoggerFactory.getLogger(CourseController.class);
    
    @Autowired
    private CourseService courseService;

    /**
     * 创建新课程
     */
    @PostMapping("/create")
    public ApiResponse createCourse(@RequestBody CourseRequest courseRequest) {
        try {
            Course course = courseService.createCourse(courseRequest);
            return ApiResponse.ok(course);
        } catch (Exception e) {
            logger.error("创建课程失败", e);
            return ApiResponse.error("创建课程失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/update/{id}")
    public ApiResponse updateCourse(@PathVariable Integer id, @RequestBody CourseRequest courseRequest) {
        try {
            Course course = courseService.updateCourse(id, courseRequest);
            return ApiResponse.ok(course);
        } catch (Exception e) {
            logger.error("更新课程失败", e);
            return ApiResponse.error("更新课程失败：" + e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteCourse(@PathVariable Integer id) {
        try {
            courseService.deleteCourse(id);
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            logger.error("删除课程失败", e);
            return ApiResponse.error("删除课程失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取课程详情
     */
    @GetMapping("/detail/{id}")
    public ApiResponse getCourseDetail(@PathVariable Integer id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course == null) {
                return ApiResponse.error("课程不存在");
            }
            return ApiResponse.ok(course);
        } catch (Exception e) {
            logger.error("获取课程详情失败", e);
            return ApiResponse.error("获取课程详情失败：" + e.getMessage());
        }
    }

    /**
     * 根据成员ID获取课程列表
     */
    @GetMapping("/list/{mid}")
    public ApiResponse getCourseList(@PathVariable Integer mid) {
        try {
            List<Course> courses = courseService.getCoursesByMid(mid);
            return ApiResponse.ok(courses);
        } catch (Exception e) {
            logger.error("获取课程列表失败", e);
            return ApiResponse.error("获取课程列表失败：" + e.getMessage());
        }
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search/{mid}")
    public ApiResponse searchCourses(@PathVariable Integer mid, @RequestParam(required = false) String keyword) {
        try {
            List<Course> courses = courseService.searchCourses(mid, keyword);
            return ApiResponse.ok(courses);
        } catch (Exception e) {
            logger.error("搜索课程失败", e);
            return ApiResponse.error("搜索课程失败：" + e.getMessage());
        }
    }

    /**
     * 获取成员的课程统计信息
     */
    @GetMapping("/stats/{mid}")
    public ApiResponse getCourseStats(@PathVariable Integer mid) {
        try {
            CourseStatsDto stats = courseService.getCourseStats(mid);
            return ApiResponse.ok(stats);
        } catch (Exception e) {
            logger.error("获取课程统计失败", e);
            return ApiResponse.error("获取课程统计失败：" + e.getMessage());
        }
    }

    /**
     * 检查课程权限
     */
    @GetMapping("/check-permission/{courseId}/{mid}")
    public ApiResponse checkCoursePermission(@PathVariable Integer courseId, @PathVariable Integer mid) {
        try {
            boolean hasPermission = courseService.checkCoursePermission(courseId, mid);
            return ApiResponse.ok(hasPermission);
        } catch (Exception e) {
            logger.error("检查课程权限失败", e);
            return ApiResponse.error("检查课程权限失败：" + e.getMessage());
        }
    }
} 