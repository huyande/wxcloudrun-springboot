package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class CourseStatsDto {
    private Integer totalCourses; // 总课程数
    private Integer totalLessons; // 总课时数
    private Integer completedLessons; // 已完成课时数
    private Integer remainingLessons; // 剩余课时数
    private Double overallProgress; // 总体进度
    private Integer earnedPoints; // 已获得积分
} 