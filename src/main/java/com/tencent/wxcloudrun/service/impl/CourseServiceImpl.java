package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.CourseMapper;
import com.tencent.wxcloudrun.dao.CourseLogMapper;
import com.tencent.wxcloudrun.dto.CourseRequest;
import com.tencent.wxcloudrun.dto.CourseStatsDto;
import com.tencent.wxcloudrun.model.Course;
import com.tencent.wxcloudrun.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private CourseLogMapper courseLogMapper;

    @Override
    @Transactional
    public Course createCourse(CourseRequest courseRequest) {
        Course course = new Course();
        BeanUtils.copyProperties(courseRequest, course);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        
        // 设置默认值
        if (course.getPointsEnabled() == null) {
            course.setPointsEnabled(0);
        }
        if (course.getPointsPerLesson() == null) {
            course.setPointsPerLesson(0);
        }
        if (course.getTotalLessons() == null) {
            course.setTotalLessons(0);
        }
        
        courseMapper.insertOne(course);
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Integer id, CourseRequest courseRequest) {
        Course existingCourse = courseMapper.getCourseById(id);
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在");
        }
        
        Course course = new Course();
        BeanUtils.copyProperties(courseRequest, course);
        course.setId(id);
        course.setUpdatedAt(LocalDateTime.now());
        
        courseMapper.updateById(course);
        return courseMapper.getCourseById(id);
    }

    @Override
    @Transactional
    public void deleteCourse(Integer id) {
        Course course = courseMapper.getCourseById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        courseMapper.deleteById(id);
    }

    @Override
    public Course getCourseById(Integer id) {
        return courseMapper.getCourseById(id);
    }

    @Override
    public List<Course> getCoursesByMid(Integer mid) {
        return courseMapper.getCoursesByMid(mid);
    }

    @Override
    public List<Course> searchCourses(Integer mid, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return courseMapper.getCoursesByMid(mid);
        }
        return courseMapper.searchCoursesByKeyword(mid, keyword.trim());
    }

    @Override
    public CourseStatsDto getCourseStats(Integer mid) {
        List<Course> courses = courseMapper.getCoursesWithStatsByMid(mid);
        
        CourseStatsDto stats = new CourseStatsDto();
        stats.setTotalCourses(courses.size());
        
        int totalLessons = 0;
        int completedLessons = 0;
        int earnedPoints = 0;
        
        for (Course course : courses) {
            totalLessons += course.getTotalLessons() != null ? course.getTotalLessons() : 0;
            completedLessons += course.getCompletedLessons() != null ? course.getCompletedLessons() : 0;
            
            if (course.getPointsEnabled() != null && course.getPointsEnabled()==1) {
                int courseCompletedLessons = course.getCompletedLessons() != null ? course.getCompletedLessons() : 0;
                int pointsPerLesson = course.getPointsPerLesson() != null ? course.getPointsPerLesson() : 0;
                earnedPoints += courseCompletedLessons * pointsPerLesson;
            }
        }
        
        stats.setTotalLessons(totalLessons);
        stats.setCompletedLessons(completedLessons);
        stats.setRemainingLessons(Math.max(0, totalLessons - completedLessons));
        stats.setEarnedPoints(earnedPoints);
        
        if (totalLessons > 0) {
            stats.setOverallProgress((double) completedLessons / totalLessons * 100);
        } else {
            stats.setOverallProgress(0.0);
        }
        
        return stats;
    }

    @Override
    public boolean checkCoursePermission(Integer courseId, Integer mid) {
        Course course = courseMapper.getCourseById(courseId);
        return course != null && course.getMid().equals(mid);
    }
} 