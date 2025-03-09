package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.AccountMapper;
import com.tencent.wxcloudrun.dao.AccountLogMapper;
import com.tencent.wxcloudrun.dao.InterestRateMapper;
import com.tencent.wxcloudrun.dto.AccountDTO;
import com.tencent.wxcloudrun.model.Account;
import com.tencent.wxcloudrun.model.AccountLog;
import com.tencent.wxcloudrun.model.InterestRate;
import com.tencent.wxcloudrun.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    final AccountMapper accountMapper;
    final AccountLogMapper accountLogMapper;
    final InterestRateMapper interestRateMapper;

    public AccountServiceImpl(@Autowired AccountMapper accountMapper,
                            @Autowired AccountLogMapper accountLogMapper,
                            @Autowired InterestRateMapper interestRateMapper) {
        this.accountMapper = accountMapper;
        this.accountLogMapper = accountLogMapper;
        this.interestRateMapper = interestRateMapper;
    }

    private AccountDTO convertToDTO(Account account) {
        if (account == null) {
            return null;
        }
        InterestRate currentRate = interestRateMapper.getCurrentInterestRateByMid(account.getMid());
        return new AccountDTO(account, currentRate);
    }

    @Override
    @Transactional
    public AccountDTO getOrCreateAccount(Integer uid, Integer mid) {
        // 先查询是否已存在账户
        Account account = accountMapper.getAccountByMid(mid);
        InterestRate currentRate;
        
        // 如果账户不存在，创建新账户和默认利率
        if (account == null) {
            account = new Account();
            account.setUid(uid);
            account.setMid(mid);
            account.setBalance(BigDecimal.ZERO);
            account.setTotalEarnings(BigDecimal.ZERO);
            account.setLastInterestTime(LocalDateTime.now());
            accountMapper.insertOne(account);

            // 创建默认利率记录（0%）
            InterestRate interestRate = new InterestRate();
            interestRate.setMid(mid);
            interestRate.setAnnualRate(BigDecimal.ZERO);
            interestRateMapper.insertOne(interestRate);
            currentRate = interestRate;
        } else {
            currentRate = interestRateMapper.getCurrentInterestRateByMid(mid);
            //判断account的lastInterestTime是否小于当前时间，如果小于当前时间，则计算利息
            if (account.getLastInterestTime().isBefore(LocalDateTime.now())) {
                calculateDailyInterest(mid);
            }   
        }
        
        return new AccountDTO(account, currentRate);
    }

    @Override
    public AccountDTO getAccountById(Integer id) {
        return convertToDTO(accountMapper.getAccountById(id));
    }

    @Override
    public AccountDTO getAccountByMid(Integer mid) {
        return convertToDTO(accountMapper.getAccountByMid(mid));
    }

    @Override
    public List<AccountDTO> getAccountsByUid(Integer uid) {
        List<Account> accounts = accountMapper.getAccountsByUid(uid);
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountDTO updateAccount(Integer id, BigDecimal targetAmount) {
        Account account = accountMapper.getAccountById(id);
        if (account != null) {
            account.setTargetAmount(targetAmount);
            accountMapper.updateById(account);
        }
        return convertToDTO(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Integer id) {
        accountMapper.deleteById(id);
    }

    @Override
    @Transactional
    public AccountDTO addBalance(Integer mid, BigDecimal amount, String category, String remark) {
        Account account = accountMapper.getAccountByMid(mid);
        if (account != null) {
            account.setBalance(account.getBalance().add(amount));
            accountMapper.updateBalance(account);
            
            // 记录交易日志
            AccountLog log = new AccountLog();
            log.setUid(account.getUid());
            log.setMid(mid);
            log.setType("增加");
            log.setAmount(amount);
            log.setCategory(category);
            log.setRemark(remark);
            accountLogMapper.insertOne(log);
        }
        return convertToDTO(account);
    }

    @Override
    @Transactional
    public AccountDTO reduceBalance(Integer mid, BigDecimal amount, String category, String remark,String type) {
        Account account = accountMapper.getAccountByMid(mid);
        if (account != null && account.getBalance().compareTo(amount) >= 0) {
            account.setBalance(account.getBalance().subtract(amount));
            accountMapper.updateBalance(account);
            
            // 记录交易日志
            AccountLog log = new AccountLog();
            log.setUid(account.getUid());
            log.setMid(mid);
            log.setType(type);
            log.setAmount(amount);
            log.setCategory(category);
            log.setRemark(remark);
            accountLogMapper.insertOne(log);
            return convertToDTO(account);
        }else{
            return null;
        }
    }

    @Override
    @Transactional
    public void calculateDailyInterest(Integer mid) {
        Account account = accountMapper.getAccountByMid(mid);
        InterestRate rate = interestRateMapper.getCurrentInterestRateByMid(mid);
        
        if (account != null && rate != null) {
            LocalDateTime now = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(account.getLastInterestTime(), now);
            
            if (daysBetween > 0) {
                // 先计算日利率 = 年化利率/36500，保留8位小数
                BigDecimal dailyRate = rate.getAnnualRate()
                    .divide(new BigDecimal("36500"), 8, RoundingMode.HALF_UP);
                
                BigDecimal currentBalance = account.getBalance();
                BigDecimal totalInterest = BigDecimal.ZERO;
                BigDecimal yesterdayInterest = BigDecimal.ZERO;

                if (daysBetween == 1) {
                    // 只有一天的情况
                    yesterdayInterest = currentBalance
                        .multiply(dailyRate)
                        .setScale(2, RoundingMode.HALF_UP);
                    totalInterest = yesterdayInterest;
                } else {
                    // 多天需要计算复利
                    for (int i = 0; i < daysBetween; i++) {
                        // 用当前余额计算当天利息
                        BigDecimal dailyInterest = currentBalance
                            .multiply(dailyRate)
                            .setScale(2, RoundingMode.HALF_UP);
                        
                        // 更新当前余额（加上今天的利息）
                        currentBalance = currentBalance.add(dailyInterest);
                        
                        // 累加总利息
                        totalInterest = totalInterest.add(dailyInterest);
                        
                        // 如果是最后一天，记录为昨日利息
                        if (i == daysBetween - 1) {
                            yesterdayInterest = dailyInterest;
                        }
                    }
                }
                
                // 更新账户信息
                account.setBalance(account.getBalance().add(totalInterest));
                account.setTotalEarnings(account.getTotalEarnings().add(totalInterest));
                account.setLastInterestTime(now);
                account.setYesterdayInterest(yesterdayInterest);
                accountMapper.updateById(account);
                
                // 记录利息收入日志
                if(totalInterest != BigDecimal.ZERO){
                    AccountLog log = new AccountLog();
                    log.setUid(account.getUid());
                    log.setMid(mid);
                    log.setType("增加");
                    log.setAmount(totalInterest);
                    log.setCategory("利息");
                    log.setRemark(String.format("利息收入(%d天复利)", daysBetween));
                    accountLogMapper.insertOne(log);
                }
            }
        }
    }
} 