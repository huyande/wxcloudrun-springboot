package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.CourseLogRequest;
import com.tencent.wxcloudrun.model.CourseLog;

import java.time.LocalDate;
import java.util.List;

public interface CourseLogService {
    
    /**
     * 添加课时记录
     */
    CourseLog addCourseLog(CourseLogRequest courseLogRequest,Long seasonId);
    
    /**
     * 删除课时记录
     */
    void deleteCourseLog(Integer id,Long seasonId);
    
    /**
     * 根据ID获取课时记录
     */
    CourseLog getCourseLogById(Integer id);
    
    /**
     * 根据课程ID获取课时记录列表
     */
    List<CourseLog> getCourseLogsByCourseId(Integer courseId);
    
    /**
     * 根据成员ID获取课时记录列表
     */
    List<CourseLog> getCourseLogsByMid(Integer mid);
    
    /**
     * 获取指定日期范围的课时记录
     */
    List<CourseLog> getCourseLogsByDateRange(Integer mid, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取最近的课时记录
     */
    List<CourseLog> getRecentCourseLogsByMid(Integer mid, Integer limit);
    
    /**
     * 检查某天是否已有课时记录
     */
    boolean hasLogOnDate(Integer courseId, LocalDate lessonDate);
    
    /**
     * 检查课时记录权限
     */
    boolean checkCourseLogPermission(Integer logId, Integer mid);
} 