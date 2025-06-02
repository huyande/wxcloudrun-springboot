 package com.tencent.wxcloudrun.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDailyTaskLogDto {

  private String type;
  private Integer points;
  private Integer days;
}