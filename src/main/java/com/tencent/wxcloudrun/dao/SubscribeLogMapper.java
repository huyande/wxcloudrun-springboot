package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SubscribeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SubscribeLogMapper {

    int delete(@Param("id") Integer id);

    void insertOne(SubscribeLog templateSubscribe);
    
    List<SubscribeLog> queryByOpenid(@Param("openid") String openid);
    
    List<SubscribeLog> queryByTemplateId(@Param("templateId") String templateId);
    
    List<SubscribeLog> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    List<SubscribeLog> queryTodaySubscriptions(@Param("openid") String openid, @Param("templateIds") List<String> templateIds);
    
    /**
     * 查询指定时间范围内需要发送的订阅记录
     */
    List<SubscribeLog> querySubscriptionsToSend(@Param("templateId") String templateId, 
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
}
