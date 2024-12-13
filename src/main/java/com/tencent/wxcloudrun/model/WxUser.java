package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WxUser implements Serializable {

  private Integer id;

  private String name;

  private String openid;

  private Integer role; // 0 妈妈 1 爸爸 2 爷爷 3 奶奶 4 外公 5 外婆 6 亲人

  private String familyCode; // 家庭代码，用于标识不同的家庭

  private Integer soundEnabled;
  private Integer animationEnabled;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
