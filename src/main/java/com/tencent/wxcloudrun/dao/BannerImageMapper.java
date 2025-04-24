package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.BannerImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BannerImageMapper {
    
    BannerImage getById(Integer id);
    
    List<BannerImage> getAll();

    List<BannerImage> getAllEnabled();
    
    void insert(BannerImage bannerImage);
    
    void update(BannerImage bannerImage);
    
    void deleteById(Integer id);

    void updateStatus(Integer id, Integer status);
} 