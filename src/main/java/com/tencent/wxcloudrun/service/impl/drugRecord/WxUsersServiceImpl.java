package com.tencent.wxcloudrun.service.impl.drugRecord;

import com.tencent.wxcloudrun.dao.drugRecord.WxUsersMapper;
import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.drugRecord.WxUsers;
import com.tencent.wxcloudrun.service.drugRecord.WxUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WxUsersServiceImpl implements WxUsersService {
    
    @Autowired
    private WxUsersMapper wxUsersMapper;
    
    @Override
    public WxUsers getById(Integer id) {
        return wxUsersMapper.getById(id);
    }
    
    @Override
    public WxUsers getByOpenid(String openid) {
        return wxUsersMapper.getByOpenid(openid);
    }
    
    @Override
    public List<WxUsers> getAll() {
        return wxUsersMapper.getAll();
    }
    
    @Override
    public int insert(WxUsers wxUsers) {
        return wxUsersMapper.insert(wxUsers);
    }
    
    @Override
    public int update(WxUsers wxUsers) {
        return wxUsersMapper.update(wxUsers);
    }
    
    @Override
    public int deleteById(Integer id) {
        return wxUsersMapper.deleteById(id);
    }

    @Override
    public WxCheckConfig getWxCheckConfig(String version) {
        return wxUsersMapper.getWxCheckConfig(version);
    }
} 