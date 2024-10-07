package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MemberRelas implements Serializable {
    private Integer id;
    private Integer mid ;
    private Integer uid;

    public MemberRelas() {}

    public MemberRelas(Integer mid, Integer uid) {
        this.mid = mid;
        this.uid = uid;
    }
}
