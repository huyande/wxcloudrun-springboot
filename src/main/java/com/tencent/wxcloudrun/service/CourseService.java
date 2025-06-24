package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.CourseRequest;
import com.tencent.wxcloudrun.dto.CourseStatsDto;
import com.tencent.wxcloudrun.model.Course;

import java.util.List;

public interface CourseService {
    
    /**
     * 创建新课程
     */
    Course createCourse(CourseRequest courseRequest);
    
    /**
     * 更新课程信息
     */
    Course updateCourse(Integer id, CourseRequest courseRequest);
    
    /**
     * 删除课程
     */
    void deleteCourse(Integer id);
    
    /**
     * 根据ID获取课程详情
     */
    Course getCourseById(Integer id);
    
    /**
     * 根据成员ID获取课程列表
     */
    List<Course> getCoursesByMid(Integer mid);
    
    /**
     * 搜索课程
     */
    List<Course> searchCourses(Integer mid, String keyword);
    
    /**
     * 获取成员的课程统计信息
     */
    CourseStatsDto getCourseStats(Integer mid);
    
    /**
     * 检查课程权限
     */
    boolean checkCoursePermission(Integer courseId, Integer mid);
} 