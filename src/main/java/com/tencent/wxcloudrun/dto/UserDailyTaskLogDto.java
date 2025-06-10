package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDailyTaskLogDto {

  private String type;
  private Integer points;
  private Integer days;
  
  // 新增字段
  private String status; // 'completed', 'pending', 'approved', 'rejected'
  private String reviewContent; // 用户提交的审核内容
}