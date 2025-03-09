package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Account implements Serializable {
    private Integer id;
    private Integer uid;
    private Integer mid;
    private BigDecimal balance; // 余额 
    private BigDecimal totalEarnings; // 总收益
    private LocalDateTime lastInterestTime; // 上次利息时间
    private BigDecimal targetAmount; // 目标金额
    private BigDecimal yesterdayInterest; // 昨日利息
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
} 