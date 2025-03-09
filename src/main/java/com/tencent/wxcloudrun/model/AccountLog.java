package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountLog implements Serializable {
    private Integer tid;
    private Integer uid;
    private Integer mid;
    private String type;  // 增加/减少
    private BigDecimal amount;
    private String category;  // 奖励/惩罚/利息/其他
    private String remark;
    private LocalDateTime createdAt;
} 