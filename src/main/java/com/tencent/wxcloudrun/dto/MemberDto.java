package com.tencent.wxcloudrun.dto;

import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.SeasonConfig;
import lombok.Data;

@Data
public class MemberDto extends Member {
    private SeasonConfig seasonConfig; // 赛季配置
}
