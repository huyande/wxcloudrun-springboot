package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseRequest {
    private Integer id;
    private Integer mid; // 成员id
    private String name; // 课程名称
    private Integer totalLessons; // 总课时数
    private LocalDate startDate; // 开始日期
    private String weekdays; // 上课周期（1,2,3,4,5,6,7 对应周一到周日，逗号分隔）
    private String teacher; // 授课老师
    private String location; // 上课地点
    private Integer pointsEnabled; // 是否启用积分奖励
    private Integer pointsPerLesson; // 每次完成课时获得的积分（1-100分）
    private String notes; // 备注信息
} 