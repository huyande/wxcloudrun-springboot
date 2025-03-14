package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.AccountLogMapper;
import com.tencent.wxcloudrun.model.AccountLog;
import com.tencent.wxcloudrun.dto.CategoryStatDTO;
import com.tencent.wxcloudrun.service.AccountLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountLogServiceImpl implements AccountLogService {

    final AccountLogMapper accountLogMapper;

    public AccountLogServiceImpl(@Autowired AccountLogMapper accountLogMapper) {
        this.accountLogMapper = accountLogMapper;
    }

    @Override
    @Transactional
    public AccountLog createLog(AccountLog accountLog) {
        accountLogMapper.insertOne(accountLog);
        return accountLog;
    }

    @Override
    public AccountLog getLogById(Integer tid) {
        return accountLogMapper.getLogById(tid);
    }

    @Override
    public List<AccountLog> getLogsByMid(Integer mid, Integer page, Integer pageSize) {
        // 计算偏移量，page从1开始
        int offset = (page - 1) * pageSize;
        return accountLogMapper.getLogsByMid(mid, offset, pageSize);
    }

    @Override
    public List<AccountLog> getLogsByMidAndType(Integer mid, String type, Integer page, Integer pageSize) {
        // 计算偏移量，page从1开始
        int offset = (page - 1) * pageSize;
        return accountLogMapper.getLogsByMidAndType(mid, type, offset, pageSize);
    }

    @Override
    public List<AccountLog> getLogsByMidAndCategory(Integer mid, String category, Integer page, Integer pageSize) {
        // 计算偏移量，page从1开始
        int offset = (page - 1) * pageSize;
        return accountLogMapper.getLogsByMidAndCategory(mid, category, offset, pageSize);
    }

    @Override
    public Integer getLogsCountByMid(Integer mid) {
        return accountLogMapper.getLogsCountByMid(mid);
    }

    @Override
    @Transactional
    public void deleteLog(Integer tid) {
        accountLogMapper.deleteById(tid);
    }

    @Override
    public List<CategoryStatDTO> getCategoryStats(Integer mid) {
        List<Map<String, Object>> stats = accountLogMapper.getCategoryStats(mid);
        return stats.stream()
                .map(map -> new CategoryStatDTO(
                        (String) map.get("name"),
                        (BigDecimal) map.get("value")
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryStatDTO> getTypeStats(Integer mid) {
        List<Map<String, Object>> stats = accountLogMapper.getTypeStats(mid);
        return stats.stream()
                .map(map -> new CategoryStatDTO(
                        (String) map.get("name"),
                        (BigDecimal) map.get("value")
                ))
                .collect(Collectors.toList());
    }
} 