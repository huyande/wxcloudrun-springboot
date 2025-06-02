 package com.tencent.wxcloudrun.dto;

 import lombok.Data;

 @Data
public class TaskCountDto {

  private String type;
  private Long count;

  public TaskCountDto(String type, Long count) {
     this.type = type;
     this.count = count;
  }
 }
