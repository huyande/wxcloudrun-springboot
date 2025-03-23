package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.TImage;
import java.util.List;
import java.util.Map;

public interface TImageService {
    
    TImage getImageById(Integer id);
    
    List<TImage> getAllImages();
    
    TImage createImage(TImage tImage);
    
    void updateImage(TImage tImage);
    
    void deleteImage(Integer id);
    
    List<TImage> getImagesByLocation(String location);
    
    /**
     * 获取按groupName分组的图片列表
     * @param location 位置
     * @return 分组后的图片列表，格式为[{groupName: xxx, items: []}]
     */
    List<Map<String, Object>> getGroupedImagesByLocation(String location);
} 