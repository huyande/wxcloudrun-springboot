package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TImage implements Serializable {
    private Integer id;
    private String imageName;
    private String imageUrl;
    private String location;
    private String groupID; // 分组ID
    private String groupName; // 分组名称
}