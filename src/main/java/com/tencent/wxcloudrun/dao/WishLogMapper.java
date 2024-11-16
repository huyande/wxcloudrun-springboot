package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.WishLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WishLogMapper {
    
    // 根据ID查询
    WishLog getById(@Param("id") Integer id);
    
    // 根据用户ID查询列表
    List<WishLog> getByUid(@Param("uid") String uid);
    
    // 根据愿望ID查询列表
    List<WishLog> getByWid(@Param("wid") Integer wid);
    
    // 新增
    int insert(WishLog wishLog);
    
    // 更新
    void update(WishLog wishLog);
    
    // 删除
    void deleteById(@Param("id") Integer id);

    Integer getSumNumByMid(@Param("mid") Integer mid);

    List<Map<String, Object>> getByMid(@Param("mid")Integer mid);
}
