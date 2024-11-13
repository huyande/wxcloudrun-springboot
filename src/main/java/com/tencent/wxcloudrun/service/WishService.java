package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.dto.WishRequest;
import java.util.List;

public interface WishService {
    // 创建愿望
    Wish createWish(WishRequest request);
    
    // 更新愿望
    Wish updateWish(WishRequest request);
    
    // 删除愿望
    void deleteWish(Integer id);
    
    // 获取单个愿望
    Wish getWishById(Integer id);
    
    // 获取所有愿望
    List<Wish> getAllWishes();
    
    // 获取指定成员的愿望
    List<Wish> getWishesByMid(String mid);
}
