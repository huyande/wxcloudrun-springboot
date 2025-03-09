package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CategoryStatDTO implements Serializable {
    private String name;  // 分类名称
    private BigDecimal value;  // 分类数量

    public CategoryStatDTO(String name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }
} 