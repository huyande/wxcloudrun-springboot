package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.FamilyMapper;
import com.tencent.wxcloudrun.dao.MemberMapper;
import com.tencent.wxcloudrun.dao.MemberRelasMapper;
import com.tencent.wxcloudrun.dao.WxuserMapper;
import com.tencent.wxcloudrun.dto.FamilyRequest;
import com.tencent.wxcloudrun.model.Family;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberRelas;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WxuserServiceImpl implements WxuserService {

    final WxuserMapper wxuserMapper;
    final FamilyMapper familyMapper;
    final MemberMapper memberMapper;
    final MemberRelasMapper memberRelasMapper;
    //构造函数注入
    public WxuserServiceImpl(@Autowired WxuserMapper wxuserMapper, @Autowired FamilyMapper familyMapper, @Autowired MemberMapper memberMapper, @Autowired MemberRelasMapper memberRelasMapper) {
        this.memberMapper = memberMapper;
        this.wxuserMapper = wxuserMapper;
        this.familyMapper = familyMapper;
        this.memberRelasMapper = memberRelasMapper;
    }

    @Override
    public Optional<WxUser> getUser(String openid) {
        return Optional.ofNullable(wxuserMapper.getUser(openid));
    }

    @Override
    public void upsertUser(WxUser wxUser) {
        wxuserMapper.upsertUser(wxUser);
    }

    @Override
    public List<WxUser> getUsersByFamilyCode(String code) {
        return wxuserMapper.getUsersByFamilyCode(code);
    }

    @Override
    public Optional<WxUser> getUserByFamilyCode(String code) {
        return Optional.ofNullable(wxuserMapper.getUserByFamilyCode(code));
    }

    @Override
    public void setFailyRela(FamilyRequest familyRequest) {
        Family _family = familyMapper.getOneByCodeAndUid(familyRequest.getCode(), familyRequest.getUid());
        if(_family ==null){//说明没有被绑定过
            Family family = new Family();
            family.setCode(familyRequest.getCode());
            family.setUid(familyRequest.getUid());
            familyMapper.insertOne(family);
            wxuserMapper.updateRoleUserById(familyRequest.getUid(),familyRequest.getRole());
        }
    }

    @Override
    public WxUser getUserById(Integer id) {
        return wxuserMapper.getUserById(id);
    }

    @Override
    public Family getFamilyByCodeAndUid(String code, Integer uid) {
        return familyMapper.getOneByCodeAndUid(code, uid);
    }

    @Override
    public void deleteFamilyRelas(FamilyRequest familyRequest) {
        familyMapper.deleteByCodeAndUid(familyRequest.getCode(), familyRequest.getUid()); //删除家庭()
    }
}
