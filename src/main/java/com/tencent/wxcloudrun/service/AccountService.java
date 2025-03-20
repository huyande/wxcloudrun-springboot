package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Account;
import com.tencent.wxcloudrun.dto.AccountDTO;
import com.tencent.wxcloudrun.model.AccountLog;
import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDTO getOrCreateAccount(Integer uid, Integer mid);
    
    AccountDTO getAccountById(Integer id);
    
    AccountDTO getAccountByMid(Integer mid);
    
    List<AccountDTO> getAccountsByUid(Integer uid);
    
    AccountDTO updateAccount(Integer id, BigDecimal targetAmount);
    
    void deleteAccount(Integer id);
    
    AccountDTO addBalance(Integer mid, BigDecimal amount, String category, String remark, String createdAt);
    
    AccountDTO reduceBalance(Integer mid, BigDecimal amount, String category, String remark, String type, String createdAt);
    
    void calculateDailyInterest(Integer mid);
    
    /**
     * 修改交易记录并重新计算账户余额
     * @param tid 交易ID
     * @param amount 新的交易金额
     * @param category 新的交易分类
     * @param remark 新的交易备注
     * @param type 新的交易类型（增加/减少）
     * @param createdAt 新的交易时间
     * @return 更新后的账户信息
     */
    AccountDTO updateAccountLog(Integer tid, BigDecimal amount, String category, String remark, String type, String createdAt);
    
    /**
     * 删除交易记录并重新计算账户余额
     * @param tid 交易ID
     * @return 更新后的账户信息
     */
    AccountDTO deleteAccountLog(Integer tid);
} 