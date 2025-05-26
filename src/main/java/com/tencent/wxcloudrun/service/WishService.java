package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.dto.WishRequest;
import java.util.List;

public interface WishService {
    // 创建愿望
    <T> T createWish(WishRequest request, Long seasonId, Class<T> expectedType);
    
    // 更新愿望
    <T> T updateWish(WishRequest request, Long seasonId, Class<T> expectedType);
    
    // 删除愿望
    void deleteWish(Integer id, Long seasonId);
    
    // 获取单个愿望
    <T> T getWishById(Integer id, Long seasonId, Class<T> expectedType);
    
    // 获取所有愿望
    <T> List<T> getAllWishes(Long seasonId, Class<T> expectedType);
    
    // 获取指定成员的愿望
    <T> List<T> getWishesByMid(String mid, Long seasonId, Class<T> expectedType);

}
