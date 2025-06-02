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
}
