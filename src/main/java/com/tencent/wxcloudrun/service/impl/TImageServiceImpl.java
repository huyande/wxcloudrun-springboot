package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.TImageMapper;
import com.tencent.wxcloudrun.model.TImage;
import com.tencent.wxcloudrun.service.TImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
} 