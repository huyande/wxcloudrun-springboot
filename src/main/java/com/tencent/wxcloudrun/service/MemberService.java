package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;

import java.util.List;

public interface MemberService {
    List<Member> getMembersByUid(Integer uId);
    Integer getMemberCountByUid(Integer uId);
    Member insert(MemberRequest memberRequest);
}
