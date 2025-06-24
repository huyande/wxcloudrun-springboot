package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    
    /**
     * 插入新课程
     */
    void insertOne(Course course);
    
    /**
     * 根据成员ID获取课程列表
     */
    List<Course> getCoursesByMid(@Param("mid") Integer mid);
    
    /**
     * 根据ID获取课程详情
     */
    Course getCourseById(@Param("id") Integer id);
    
    /**
     * 更新课程信息
     */
    void updateById(Course course);
    
    /**
     * 根据ID删除课程
     */
    void deleteById(@Param("id") Integer id);
    
    /**
     * 获取课程的已完成课时数
     */
    Integer getCompletedLessonsByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 获取成员的课程统计信息
     */
    List<Course> getCoursesWithStatsByMid(@Param("mid") Integer mid);
    
    /**
     * 根据关键词搜索课程
     */
    List<Course> searchCoursesByKeyword(@Param("mid") Integer mid, @Param("keyword") String keyword);
} 