package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.BannerImage;
import java.util.List;

public interface BannerImageService {
    
    BannerImage getBannerImageById(Integer id);
    
    List<BannerImage> getAllBannerImages();
    
    List<BannerImage> getAllEnabledBannerImages();
    
    BannerImage createBannerImage(BannerImage bannerImage);
    
    void updateBannerImage(BannerImage bannerImage);
    
    void deleteBannerImage(Integer id);
    
    void updateBannerImageStatus(Integer id, Integer status);
} 