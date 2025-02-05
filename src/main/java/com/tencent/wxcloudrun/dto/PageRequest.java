package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
} 