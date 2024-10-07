package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.WxuserMapper;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.WxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WxuserServiceImpl implements WxuserService {

    final WxuserMapper wxuserMapper;
    //构造函数注入
    public WxuserServiceImpl(@Autowired WxuserMapper wxuserMapper) {
        this.wxuserMapper = wxuserMapper;
    }

    @Override
    public Optional<WxUser> getUser(String openid) {
        return Optional.ofNullable(wxuserMapper.getUser(openid));
    }

    @Override
    public void upsertUser(WxUser wxUser) {
        wxuserMapper.upsertUser(wxUser);
    }
}
