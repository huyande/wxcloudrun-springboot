package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.TImageMapper;
import com.tencent.wxcloudrun.model.TImage;
import com.tencent.wxcloudrun.service.TImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TImageServiceImpl implements TImageService {

    @Autowired
    private TImageMapper tImageMapper;

    @Override
    public TImage getImageById(Integer id) {
        return tImageMapper.getById(id);
    }

    @Override
    public List<TImage> getAllImages() {
        return tImageMapper.getAll();
    }

    @Override
    public TImage createImage(TImage tImage) {
        LocalDateTime now = LocalDateTime.now();
        tImageMapper.insert(tImage);
        return tImage;
    }

    @Override
    public void updateImage(TImage tImage) {
        tImageMapper.update(tImage);
    }

    @Override
    public void deleteImage(Integer id) {
        tImageMapper.deleteById(id);
    }

    @Override
    public List<TImage> getImagesByLocation(String location) {
        return tImageMapper.getByLocation(location);
    }
    
    @Override
    public List<Map<String, Object>> getGroupedImagesByLocation(String location) {
        // 获取指定位置的所有图片
        List<TImage> images = tImageMapper.getByLocation(location);
        
        // 如果没有图片，返回空列表
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按groupName分组
        Map<String, List<TImage>> groupedImages = images.stream()
                .filter(image -> image.getGroupName() != null) // 过滤掉没有groupName的图片
                .collect(Collectors.groupingBy(TImage::getGroupName));
        
        // 转换为所需的返回格式
        List<Map<String, Object>> result = new ArrayList<>();
        groupedImages.forEach((groupName, groupImages) -> {
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("groupName", groupName);
            groupMap.put("items", groupImages);
            result.add(groupMap);
        });
        
        return result;
    }
} 