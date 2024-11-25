package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;

import java.util.List;

public interface MemberService {
    List<Member> getMembersByFamilyCode(String FamilyCode);
    Integer getMemberCountByUid(Integer uId);
    Member insert(MemberRequest memberRequest);

    List<Member> getMembersByUid(Integer id);

    Member getMemberById(Integer mid);
}
