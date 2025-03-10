package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.beans.Transient;
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

  private LocalDateTime vipExpiredAt;//vip过期时间

  //不映射数据库字段
  private transient Boolean isVip;

  private transient Integer isRead;

  //vip签到时间
  private LocalDateTime vipSignInAt;
  
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
