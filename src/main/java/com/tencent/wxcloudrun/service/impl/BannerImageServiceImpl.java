package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.BannerImageMapper;
import com.tencent.wxcloudrun.model.BannerImage;
import com.tencent.wxcloudrun.service.BannerImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BannerImageServiceImpl implements BannerImageService {

    @Autowired
    private BannerImageMapper bannerImageMapper;

    @Override
    public BannerImage getBannerImageById(Integer id) {
        return bannerImageMapper.getById(id);
    }

    @Override
    public List<BannerImage> getAllBannerImages() {
        return bannerImageMapper.getAll();
    }

    @Override
    public List<BannerImage> getAllEnabledBannerImages() {
        return bannerImageMapper.getAllEnabled();
    }

    @Override
    public BannerImage createBannerImage(BannerImage bannerImage) {
        // 设置默认值
        if (bannerImage.getStatus() == null) {
            bannerImage.setStatus(1); // 默认启用
        }
        
        if (bannerImage.getSort() == null) {
            bannerImage.setSort(0); // 默认排序值
        }
        
        bannerImage.setCreatedAt(LocalDateTime.now());
        bannerImage.setUpdatedAt(LocalDateTime.now());
        
        bannerImageMapper.insert(bannerImage);
        return bannerImage;
    }

    @Override
    public void updateBannerImage(BannerImage bannerImage) {
        bannerImage.setUpdatedAt(LocalDateTime.now());
        bannerImageMapper.update(bannerImage);
    }

    @Override
    public void deleteBannerImage(Integer id) {
        bannerImageMapper.deleteById(id);
    }

    @Override
    public void updateBannerImageStatus(Integer id, Integer status) {
        bannerImageMapper.updateStatus(id, status);
    }
} 