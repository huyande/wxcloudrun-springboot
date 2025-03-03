package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.TImage;
import java.util.List;

public interface TImageService {
    
    TImage getImageById(Integer id);
    
    List<TImage> getAllImages();
    
    TImage createImage(TImage tImage);
    
    void updateImage(TImage tImage);
    
    void deleteImage(Integer id);
    
    List<TImage> getImagesByLocation(String location);
} 