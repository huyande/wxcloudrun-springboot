package com.tencent.wxcloudrun.model.drugRecord;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Drugs {
    private Integer id;
    private String openid;
    private String manuName;
    private String img;
    private String code;
    private String spec;
    private String name;
    private String note;// 备注
    private LocalDateTime expireDate;// 有效期
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 