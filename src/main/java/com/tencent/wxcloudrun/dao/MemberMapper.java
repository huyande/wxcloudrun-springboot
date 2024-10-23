package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    void insertOne(Member member);
    List<Member> getMembersByUid(@Param("uId") Integer uId);
    Integer getCountMembersByUid(@Param("uId") Integer uId);
}
