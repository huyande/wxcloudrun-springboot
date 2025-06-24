package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.CourseLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CourseLogMapper {
    
    /**
     * 插入新的课时记录
     */
    void insertOne(CourseLog courseLog);
    
    /**
     * 根据课程ID获取课时记录列表
     */
    List<CourseLog> getLogsByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 根据成员ID获取课时记录列表
     */
    List<CourseLog> getLogsByMid(@Param("mid") Integer mid);
    
    /**
     * 根据ID获取课时记录详情
     */
    CourseLog getLogById(@Param("id") Integer id);
    
    /**
     * 根据ID删除课时记录
     */
    void deleteById(@Param("id") Integer id);
    
    /**
     * 获取指定日期范围的课时记录
     */
    List<CourseLog> getLogsByDateRange(@Param("mid") Integer mid, 
                                       @Param("startDate") LocalDate startDate, 
                                       @Param("endDate") LocalDate endDate);
    
    /**
     * 获取课程的课时记录总数
     */
    Integer getLogCountByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 获取成员的课时记录总数
     */
    Integer getLogCountByMid(@Param("mid") Integer mid);
    
    /**
     * 检查某天是否已有课时记录
     */
    CourseLog getLogByDateAndCourse(@Param("courseId") Integer courseId, @Param("lessonDate") LocalDate lessonDate);
    
    /**
     * 获取最近的课时记录
     */
    List<CourseLog> getRecentLogsByMid(@Param("mid") Integer mid, @Param("limit") Integer limit);
} 