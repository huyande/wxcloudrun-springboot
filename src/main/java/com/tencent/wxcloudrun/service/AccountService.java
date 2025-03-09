package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Account;
import com.tencent.wxcloudrun.dto.AccountDTO;
import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDTO getOrCreateAccount(Integer uid, Integer mid);
    
    AccountDTO getAccountById(Integer id);
    
    AccountDTO getAccountByMid(Integer mid);
    
    List<AccountDTO> getAccountsByUid(Integer uid);
    
    AccountDTO updateAccount(Integer id, BigDecimal targetAmount);
    
    void deleteAccount(Integer id);
    
    AccountDTO addBalance(Integer mid, BigDecimal amount, String category, String remark);
    
    AccountDTO reduceBalance(Integer mid, BigDecimal amount, String category, String remark,String type);
    
    void calculateDailyInterest(Integer mid);
} 