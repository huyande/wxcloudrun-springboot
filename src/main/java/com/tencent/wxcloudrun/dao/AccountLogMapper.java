package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.AccountLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountLogMapper {
    void insertOne(AccountLog accountLog);
    
    AccountLog getLogById(@Param("tid") Integer tid);
    
    List<AccountLog> getLogsByMid(@Param("mid") Integer mid, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    List<AccountLog> getLogsByMidAndType(@Param("mid") Integer mid, @Param("type") String type, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    List<AccountLog> getLogsByMidAndCategory(@Param("mid") Integer mid, @Param("category") String category, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    Integer getLogsCountByMid(@Param("mid") Integer mid);
    
    void deleteById(@Param("tid") Integer tid);

    /**
     * 按分类统计交易记录数量
     * @param mid 成员ID
     * @return 分类及其对应的数量
     */
    List<Map<String, Object>> getCategoryStats(@Param("mid") Integer mid);

    List<Map<String, Object>> getTypeStats(@Param("mid") Integer mid);
} 