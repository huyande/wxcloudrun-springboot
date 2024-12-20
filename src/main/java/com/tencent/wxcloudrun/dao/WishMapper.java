package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Wish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WishMapper {

    // 根据ID查询愿望
    Wish getById(@Param("id") Integer id);
    
    // 查询所有愿望
    List<Wish> findAll();
    
    // 根据mid查询愿望列表
    List<Wish> findByMid(@Param("mid") String mid);
    
    // 创建愿望
    int insert(Wish wish);
    
    // 更新愿望
    int update(Wish wish);
    
    // 删除愿望
    int delete(@Param("id") Integer id);


    void deleteByMid(@Param("mid") Integer mid, @Param("type") Integer type);

}
