package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    void insertOne(Member member);
    List<Member> getMembersByFamilyCode(@Param("familyCode") String FamilyCode);
    List<Member> getMembersByUid(@Param("uId") Integer uId);
    Integer getCountMembersByUid(@Param("uId") Integer uId);

    Member getMemberById(@Param("id") Integer mid);

    void updateById(Member member);

    void deleteById(@Param("id") Integer id);

    void deleteAllSeasion(Integer mid);
}
