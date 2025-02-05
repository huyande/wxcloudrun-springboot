package com.tencent.wxcloudrun.service.drugRecord;

import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.drugRecord.WxUsers;

import java.util.List;

public interface WxUsersService {
    WxUsers getById(Integer id);
    
    WxUsers getByOpenid(String openid);
    
    List<WxUsers> getAll();
    
    int insert(WxUsers wxUsers);
    
    int update(WxUsers wxUsers);
    
    int deleteById(Integer id);

    WxCheckConfig getWxCheckConfig(String version);
} 