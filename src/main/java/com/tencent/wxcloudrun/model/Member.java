package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Member implements Serializable {
    private Integer id;
    private String name;
    private Integer gender;
    private Integer pointTotal;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
