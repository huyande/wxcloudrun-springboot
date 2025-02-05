package com.tencent.wxcloudrun.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> list;
    private Integer total;
    private Integer pageNum;
    private Integer pageSize;
    
    public static <T> PageResponse<T> of(List<T> list, Integer total, Integer pageNum, Integer pageSize) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(total);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        return response;
    }
} 