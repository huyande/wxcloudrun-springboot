package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.WishLog;
import java.util.List;
import java.util.Map;

public interface WishLogService {
    
    // 根据ID查询
    WishLog getById(Integer id);
    
    // 根据用户ID查询列表
    List<WishLog> getByUid(String uid);
    
    // 根据愿望ID查询列表
    List<WishLog> getByWid(Integer wid);
    
    // 新增
    WishLog create(WishLog wishLog);
    
    // 更新
    void update(WishLog wishLog);

    /**
     * 查询所有分钟类型的未完成任务
     * @return 任务列表
     */
    List<WishLog> queryTimeTask();
    
    // 删除
    void deleteById(Integer id);

    Integer getSumNumByMid(Integer mid);

    List<Map<String, Object>> getByMid(Integer mid);
}
