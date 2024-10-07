package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.MemberMapper;
import com.tencent.wxcloudrun.dao.MemberRelasMapper;
import com.tencent.wxcloudrun.dao.WxuserMapper;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberRelas;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    final MemberMapper memberMapper;
    final MemberRelasMapper memberRelasMapper;
    final WxuserMapper wxuserMapper;
    //构造函数注入
    public MemberServiceImpl(@Autowired MemberMapper memberMapper,
                             @Autowired MemberRelasMapper memberRelasMapper,
                             @Autowired WxuserMapper wxuserMapper) {
        this.memberMapper = memberMapper;
        this.memberRelasMapper = memberRelasMapper;
        this.wxuserMapper = wxuserMapper;
    }

    @Override
    public List<Member> getMembersByUid(Integer uId) {
        return memberMapper.getMembersByUid(uId);
    }

    @Override
    public Integer getMemberCountByUid(Integer uId) {
        return memberMapper.getCountMembersByUid(uId);
    }

    @Transient
    @Override
    public Member insert(MemberRequest memberRequest) {
        Member member = new Member();
        member.setName(memberRequest.getName());
        member.setGender(memberRequest.getGender());
        member.setPointTotal(memberRequest.getPointTotalCount());
        memberMapper.insertOne(member);
        MemberRelas memberRelas = new MemberRelas(member.getId(),memberRequest.getUid());
        memberRelasMapper.insertOne(memberRelas);
        if(memberRequest.getRole()!=-1){
            //更新这个user的role
            wxuserMapper.updateRoleUserById(memberRequest.getUid(),memberRequest.getRole());
        }
        return member;
    }

}
