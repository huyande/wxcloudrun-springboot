package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseLogRequest {
    private Integer id;
    private Integer courseId; // 课程ID
    private Integer mid; // 成员id
    private LocalDate lessonDate; // 上课日期
    private String lessonNotes; // 本节课备注
} 