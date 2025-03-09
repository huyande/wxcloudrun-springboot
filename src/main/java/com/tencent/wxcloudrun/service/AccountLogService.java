package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.AccountLog;
import com.tencent.wxcloudrun.dto.CategoryStatDTO;
import java.util.List;

public interface AccountLogService {
    AccountLog createLog(AccountLog accountLog);
    
    AccountLog getLogById(Integer tid);
    
    List<AccountLog> getLogsByMid(Integer mid, Integer page, Integer pageSize);
    
    List<AccountLog> getLogsByMidAndType(Integer mid, String type, Integer page, Integer pageSize);
    
    List<AccountLog> getLogsByMidAndCategory(Integer mid, String category, Integer page, Integer pageSize);
    
    Integer getLogsCountByMid(Integer mid);
    
    void deleteLog(Integer tid);

    /**
     * 获取成员交易分类统计数据
     * @param mid 成员ID
     * @return 分类统计数据列表（适用于echarts饼图）
     */
    List<CategoryStatDTO> getCategoryStats(Integer mid);
} 