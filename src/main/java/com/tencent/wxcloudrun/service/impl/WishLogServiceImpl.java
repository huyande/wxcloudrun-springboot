package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.WishLogMapper;
import com.tencent.wxcloudrun.model.WishLog;
import com.tencent.wxcloudrun.service.WishLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public WishLog create(WishLog wishLog) {
        wishLog.setCreatedAt(LocalDateTime.now());
        wishLog.setUpdatedAt(LocalDateTime.now());
        wishLogMapper.insert(wishLog);
        return wishLog;
    }

    @Override
    public void update(WishLog wishLog) {
//        wishLog.setUpdatedAt(LocalDateTime.now());

        wishLogMapper.update(wishLog);
    }

    @Override
    public void deleteById(Integer id) {
        wishLogMapper.deleteById(id);
    }

    @Override
    public List<WishLog> queryTimeTask() {
        return wishLogMapper.queryTimeTask();
    }

    @Override
    public Integer getSumNumByMid(Integer mid) {
        return wishLogMapper.getSumNumByMid(mid);
    }

    @Override
    public List<Map<String, Object>> getByMid(Integer mid) {
        return wishLogMapper.getByMid(mid);
    }

    @Override
    public List<WishLog> getAllLogByStatus(Integer mid, Integer status) {
        return wishLogMapper.getAllLogByMidAndStatus(mid, status);
    }

    @Override
    public Map<String, Object> getByMidWithPage(Integer mid, Integer page, Integer pageSize, Integer status) {
        // 参数校验和默认值设置
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 获取总记录数
        int total = wishLogMapper.getCountByMidAndStatus(mid, status);
        
        // 获取分页数据
        List<Map<String, Object>> list = wishLogMapper.getByMidWithPage(mid, status, offset, pageSize);
        
        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pageNum", page);
        result.put("pageSize", pageSize);
        result.put("list", list);
        
        return result;
    }
}
