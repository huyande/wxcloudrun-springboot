package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.WishLogRequest;
import com.tencent.wxcloudrun.model.WishLog;
import java.util.List;
import java.util.Map;

public interface WishLogService {
    
    <T> T getById(Integer id, Long seasonId, Class<T> expectedType);
    
    <T> List<T> getByUid(String uid, Long seasonId, Class<T> expectedType);
    
    <T> List<T> getByWid(Integer wid, Long seasonId, Class<T> expectedType);
    
    <T> T createWishLog(WishLogRequest request, Long seasonId, Class<T> expectedType);
    
    <T> T updateWishLog(Integer id, WishLogRequest request, Long seasonId, Class<T> expectedType);

    void legacyUpdate(WishLog wishLog, Long seasonId);

    <T> List<T> queryTimeTask(Long seasonId, Class<T> expectedType);
    
    void deleteById(Integer id, Long seasonId);

    Integer getSumNumByMid(Integer mid, Long seasonId);

    List<Map<String, Object>> getByMid(Integer mid, Long seasonId);

    <T> List<T> getAllLogByStatus(Integer mid, Integer status, Long seasonId, Class<T> expectedType);
    
    Map<String, Object> getByMidWithPage(Integer mid, Integer page, Integer pageSize, Integer status, Long seasonId);
}
