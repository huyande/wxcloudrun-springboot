package com.tencent.wxcloudrun.model;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class InterestRate implements Serializable {
    private Integer id;
    private Integer mid;
    private BigDecimal annualRate;
} 