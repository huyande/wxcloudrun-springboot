package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberRelas;
import com.tencent.wxcloudrun.model.WxUser;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    List<Member> getMembersByUid(Integer uId);
    Integer getMemberCountByUid(Integer uId);
    Member insert(MemberRequest memberRequest);
}
