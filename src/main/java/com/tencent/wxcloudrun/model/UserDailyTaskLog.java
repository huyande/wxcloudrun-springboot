package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserDailyTaskLog {

  private Long id;

  private String openid;

  private String type;

  private Integer points;

  private Date createdAt;
  
  // 新增字段
  private String status; // 'completed', 'pending', 'approved', 'rejected'
  private String reviewContent; // 用户提交的审核内容
  private String remark; // 管理员审核备注
}
