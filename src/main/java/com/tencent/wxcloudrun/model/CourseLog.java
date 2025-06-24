package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CourseLog implements Serializable {
    private Integer id;
    private Integer courseId; // 课程ID
    private Integer mid; // 成员id
    private LocalDate lessonDate; // 上课日期
    private String lessonNotes; // 本节课备注
    private LocalDateTime createdAt; // 记录创建时间
    
    // 关联课程信息
    private transient String courseName; // 课程名称
} 