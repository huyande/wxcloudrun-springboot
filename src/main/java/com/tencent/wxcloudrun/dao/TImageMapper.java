package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.TImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TImageMapper {
    
    TImage getById(Integer id);
    
    List<TImage> getAll();
    
    void insert(TImage tImage);
    
    void update(TImage tImage);
    
    void deleteById(Integer id);
    
    List<TImage> getByLocation(String location);
} 