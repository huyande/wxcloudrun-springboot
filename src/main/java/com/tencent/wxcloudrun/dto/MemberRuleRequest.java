package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class MemberRuleRequest {
    private Integer mid;//要绑定的member Id
    private String name;//名字
    private String type;//类型
    private String icon;   //图标
    private Integer iconType;//图标类型
    private String weeks;//星期
    private String content;//内容
    private Integer sort;
    private Integer quickScore;
    private Integer typeSort;
}
