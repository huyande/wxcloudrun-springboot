package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.WishLogMapper;
import com.tencent.wxcloudrun.model.WishLog;
import com.tencent.wxcloudrun.service.WishLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishLogServiceImpl implements WishLogService {

    @Autowired
    private WishLogMapper wishLogMapper;

    @Override
    public WishLog getById(Integer id) {
        return wishLogMapper.getById(id);
    }

    @Override
    public List<WishLog> getByUid(String uid) {
        return wishLogMapper.getByUid(uid);
    }

    @Override
    public List<WishLog> getByWid(Integer wid) {
        return wishLogMapper.getByWid(wid);
    }

    @Override
    public void create(WishLog wishLog) {
        wishLog.setCreatedAt(LocalDateTime.now());
        wishLog.setUpdatedAt(LocalDateTime.now());
        wishLogMapper.insert(wishLog);
    }

    @Override
    public void update(WishLog wishLog) {
        wishLog.setUpdatedAt(LocalDateTime.now());
        wishLogMapper.update(wishLog);
    }

    @Override
    public void deleteById(Integer id) {
        wishLogMapper.deleteById(id);
    }

    @Override
    public Integer getSumNumByMid(Integer mid) {
        return wishLogMapper.getSumNumByMid(mid);
    }
}
