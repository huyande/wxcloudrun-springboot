package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.FamilyMapper;
import com.tencent.wxcloudrun.dao.MemberMapper;
import com.tencent.wxcloudrun.dao.MemberRelasMapper;
import com.tencent.wxcloudrun.dao.WxuserMapper;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Family;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberRelas;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.Collections;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    final MemberMapper memberMapper;
    final MemberRelasMapper memberRelasMapper;
    final WxuserMapper wxuserMapper;
    final FamilyMapper familyMapper;
    //构造函数注入
    public MemberServiceImpl(@Autowired MemberMapper memberMapper,
                             @Autowired MemberRelasMapper memberRelasMapper,
                             @Autowired WxuserMapper wxuserMapper, @Autowired FamilyMapper familyMapper) {
        this.familyMapper = familyMapper;
        this.memberMapper = memberMapper;
        this.memberRelasMapper = memberRelasMapper;
        this.wxuserMapper = wxuserMapper;
    }

    @Override
    public List<Member> getMembersByFamilyCode(String FamilyCode) {
        return memberMapper.getMembersByFamilyCode(FamilyCode);
    }

    @Override
    public Integer getMemberCountByUid(Integer uId) {
        return memberMapper.getCountMembersByUid(uId);
    }

    @Transient
    @Override
    public Member insert(MemberRequest memberRequest) {
        WxUser wxUser = wxuserMapper.getUserById(memberRequest.getUid());
        Member member = new Member();
        member.setName(memberRequest.getName());
        member.setGender(memberRequest.getGender());
        member.setPointTotal(memberRequest.getPointTotalCount());
        member.setUid(memberRequest.getUid());
        member.setFamilyCode(wxUser.getFamilyCode());
        memberMapper.insertOne(member);
        Family family = familyMapper.getOneByCodeAndUid(wxUser.getFamilyCode(), memberRequest.getUid());
        if(family == null){
            family = new Family();
            family.setCode(wxUser.getFamilyCode());
            family.setUid(memberRequest.getUid());
            familyMapper.insertOne(family);
        }

        if(memberRequest.getRole()!=-1){
            //更新这个user的role
            wxuserMapper.updateRoleUserById(memberRequest.getUid(),memberRequest.getRole());
        }
        return member;
    }

    @Override
    public List<Member> getMembersByUid(Integer id) {
        return memberMapper.getMembersByUid(id);
    }

}
