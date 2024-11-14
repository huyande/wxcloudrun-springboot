package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.WishLog;
import java.util.List;

public interface WishLogService {
    
    // 根据ID查询
    WishLog getById(Integer id);
    
    // 根据用户ID查询列表
    List<WishLog> getByUid(String uid);
    
    // 根据愿望ID查询列表
    List<WishLog> getByWid(Integer wid);
    
    // 新增
    void create(WishLog wishLog);
    
    // 更新
    void update(WishLog wishLog);
    
    // 删除
    void deleteById(Integer id);

    Integer getSumNumByMid(Integer mid);
}
