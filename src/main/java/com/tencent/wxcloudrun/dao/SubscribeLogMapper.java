package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SubscribeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubscribeLogMapper {

    int delete(@Param("id") Integer id);

    void insertOne(SubscribeLog templateSubscribe);
    
    List<SubscribeLog> queryByOpenid(@Param("openid") String openid);
    
    List<SubscribeLog> queryByTemplateId(@Param("templateId") String templateId);
    
    List<SubscribeLog> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
