package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.CourseLogMapper;
import com.tencent.wxcloudrun.dao.CourseMapper;
import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.SeasonPointLogMapper;
import com.tencent.wxcloudrun.dto.CourseLogRequest;
import com.tencent.wxcloudrun.model.Course;
import com.tencent.wxcloudrun.model.CourseLog;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.model.SeasonPointLog;
import com.tencent.wxcloudrun.service.CourseLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class CourseLogServiceImpl implements CourseLogService {

    @Autowired
    private CourseLogMapper courseLogMapper;
    
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private MemberPointLogsMapper memberPointLogsMapper;

    @Autowired
    private SeasonPointLogMapper seasonPointLogMapper;

    @Override
    @Transactional
    public CourseLog addCourseLog(CourseLogRequest courseLogRequest,Long seasonId) {
        // 验证课程是否存在
        Course course = courseMapper.getCourseById(courseLogRequest.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 检查是否重复记录同一天的课时
        CourseLog existingLog = courseLogMapper.getLogByDateAndCourse(
            courseLogRequest.getCourseId(), 
            courseLogRequest.getLessonDate()
        );
        if (existingLog != null) {
            throw new RuntimeException("该日期已有课时记录，请勿重复添加");
        }
        
        CourseLog courseLog = new CourseLog();
        BeanUtils.copyProperties(courseLogRequest, courseLog);
        courseLog.setCreatedAt(LocalDateTime.now());
        
        courseLogMapper.insertOne(courseLog);

        //判断是否开启了增加积分
        if(course.getPointsEnabled()==1){
            LocalDateTime dateTime = LocalDate.now().atStartOfDay();;
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
//            String formattedDateTime = now.format(formatter);
//            LocalDateTime dateTime = LocalDateTime.parse(formattedDateTime, formatter);

            if(seasonId!=null){
                SeasonPointLog seasonPointLog = new SeasonPointLog();
                seasonPointLog.setDay(dateTime);
                seasonPointLog.setSeasonId(seasonId);
                seasonPointLog.setType(4);
                seasonPointLog.setStatus(0);
                seasonPointLog.setMid(courseLogRequest.getMid());
                seasonPointLog.setNum(course.getPointsPerLesson());
                seasonPointLog.setRemark(courseLog.getId().toString());
                seasonPointLogMapper.insert(seasonPointLog);
            }else{
                MemberPointLogs memberPointLogs = new MemberPointLogs();
                memberPointLogs.setDay(dateTime);
                memberPointLogs.setMid(courseLogRequest.getMid());
                memberPointLogs.setNum(course.getPointsPerLesson());
                memberPointLogs.setType(4);
                memberPointLogs.setStatus(0);
                memberPointLogs.setRemark(courseLog.getId().toString());
                memberPointLogsMapper.insertOne(memberPointLogs);
            }
        }
        return courseLogMapper.getLogById(courseLog.getId());
    }

    @Override
    @Transactional
    public void deleteCourseLog(Integer id,Long seasonId) {
        CourseLog courseLog = courseLogMapper.getLogById(id);
        if (courseLog == null) {
            throw new RuntimeException("课时记录不存在");
        }
        // 验证课程是否存在
        Course course = courseMapper.getCourseById(courseLog.getCourseId());
        //判断是否开启了增加积分
        if(course.getPointsEnabled()==1){
            if(seasonId!=null){
                seasonPointLogMapper.deleteBySeasonIdAndReamrk(seasonId,id.toString(),courseLog.getMid(),4);
            }else {
                memberPointLogsMapper.deleteByRemark(id.toString(),courseLog.getMid(),4);
            }
        }
        
        courseLogMapper.deleteById(id);
    }

    @Override
    public CourseLog getCourseLogById(Integer id) {
        return courseLogMapper.getLogById(id);
    }

    @Override
    public List<CourseLog> getCourseLogsByCourseId(Integer courseId) {
        return courseLogMapper.getLogsByCourseId(courseId);
    }

    @Override
    public List<CourseLog> getCourseLogsByMid(Integer mid) {
        return courseLogMapper.getLogsByMid(mid);
    }

    @Override
    public List<CourseLog> getCourseLogsByDateRange(Integer mid, LocalDate startDate, LocalDate endDate) {
        return courseLogMapper.getLogsByDateRange(mid, startDate, endDate);
    }

    @Override
    public List<CourseLog> getRecentCourseLogsByMid(Integer mid, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认获取最近10条记录
        }
        return courseLogMapper.getRecentLogsByMid(mid, limit);
    }

    @Override
    public boolean hasLogOnDate(Integer courseId, LocalDate lessonDate) {
        CourseLog log = courseLogMapper.getLogByDateAndCourse(courseId, lessonDate);
        return log != null;
    }

    @Override
    public boolean checkCourseLogPermission(Integer logId, Integer mid) {
        CourseLog courseLog = courseLogMapper.getLogById(logId);
        return courseLog != null && courseLog.getMid().equals(mid);
    }
} 