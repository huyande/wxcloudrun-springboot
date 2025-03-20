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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    final AccountMapper accountMapper;
    final AccountLogMapper accountLogMapper;
    final InterestRateMapper interestRateMapper;
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    public AccountDTO addBalance(Integer mid, BigDecimal amount, String category, String remark, String createdAt) {
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
            
            // 设置创建时间，如果没有提供则使用当前时间
            if (createdAt != null && !createdAt.trim().isEmpty()) {
                try {
                    log.setCreatedAt(LocalDateTime.parse(createdAt, dateTimeFormatter));
                } catch (Exception e) {
                    // 如果解析失败，使用当前时间
                    log.setCreatedAt(LocalDateTime.now());
                }
            } else {
                log.setCreatedAt(LocalDateTime.now());
            }
            
            accountLogMapper.insertOne(log);
        }
        return convertToDTO(account);
    }

    @Override
    @Transactional
    public AccountDTO reduceBalance(Integer mid, BigDecimal amount, String category, String remark, String type, String createdAt) {
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
            
            // 设置创建时间，如果没有提供则使用当前时间
            if (createdAt != null && !createdAt.trim().isEmpty()) {
                try {
                    log.setCreatedAt(LocalDateTime.parse(createdAt, dateTimeFormatter));
                } catch (Exception e) {
                    // 如果解析失败，使用当前时间
                    log.setCreatedAt(LocalDateTime.now());
                }
            } else {
                log.setCreatedAt(LocalDateTime.now());
            }
            
            accountLogMapper.insertOne(log);
            return convertToDTO(account);
        } else {
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
                
                // 记录利息收入日志 如果totalInterest大于0，则记录日志  
                if(totalInterest.compareTo(BigDecimal.ZERO) > 0){
                    AccountLog log = new AccountLog();
                    log.setUid(account.getUid());
                    log.setMid(mid);
                    log.setType("增加");
                    log.setAmount(totalInterest);
                    log.setCategory("利息");
                    log.setRemark(String.format("(%d天复利)", daysBetween));
                    log.setCreatedAt(LocalDateTime.now());
                    accountLogMapper.insertOne(log);
                }
            }
        }
    }

    @Override
    @Transactional
    public AccountDTO updateAccountLog(Integer tid, BigDecimal amount, String category, String remark, String type, String createdAt) {
        // 查询原交易记录
        AccountLog oldLog = accountLogMapper.getLogById(tid);
        if (oldLog == null) {
            return null;
        }
        
        // 获取账户信息
        Account account = accountMapper.getAccountByMid(oldLog.getMid());
        if (account == null) {
            return null;
        }
        
        // 计算余额调整值
        BigDecimal balanceAdjustment = BigDecimal.ZERO;
        
        // 原交易是增加类型
        if ("增加".equals(oldLog.getType())) {
            // 从余额中减去原交易金额
            balanceAdjustment = balanceAdjustment.subtract(oldLog.getAmount());
        } else {
            // 原交易是减少类型，向余额中加回原交易金额
            balanceAdjustment = balanceAdjustment.add(oldLog.getAmount());
        }
        
        // 新交易是增加类型
        if ("增加".equals(type)) {
            // 向余额中加入新交易金额
            balanceAdjustment = balanceAdjustment.add(amount);
        } else {
            // 新交易是减少类型，从余额中减去新交易金额
            balanceAdjustment = balanceAdjustment.subtract(amount);
        }
        
        // 检查余额是否足够（如果是减少操作）
        if (account.getBalance().add(balanceAdjustment).compareTo(BigDecimal.ZERO) < 0) {
            return null; // 余额不足
        }
        
        // 更新账户余额
        account.setBalance(account.getBalance().add(balanceAdjustment));
        accountMapper.updateBalance(account);
        
        // 更新交易记录
        oldLog.setAmount(amount);
        oldLog.setCategory(category);
        oldLog.setRemark(remark);
        oldLog.setType(type);
        
        // 设置创建时间，如果没有提供则保持原来的时间
        if (createdAt != null && !createdAt.trim().isEmpty()) {
            try {
                oldLog.setCreatedAt(LocalDateTime.parse(createdAt, dateTimeFormatter));
            } catch (Exception e) {
                // 如果解析失败，保持原来的时间
            }
        }
        
        accountLogMapper.updateLog(oldLog);
        
        return convertToDTO(account);
    }
    
    @Override
    @Transactional
    public AccountDTO deleteAccountLog(Integer tid) {
        // 查询原交易记录
        AccountLog log = accountLogMapper.getLogById(tid);
        if (log == null) {
            return null;
        }
        
        // 获取账户信息
        Account account = accountMapper.getAccountByMid(log.getMid());
        if (account == null) {
            return null;
        }
        
        // 计算余额调整值
        BigDecimal balanceAdjustment = BigDecimal.ZERO;
        
        // 原交易是增加类型
        if ("增加".equals(log.getType())) {
            // 从余额中减去原交易金额
            balanceAdjustment = balanceAdjustment.subtract(log.getAmount());
        } else {
            // 原交易是减少类型，向余额中加回原交易金额
            balanceAdjustment = balanceAdjustment.add(log.getAmount());
        }
        
        // 检查余额是否足够（如果是删除增加类型的交易）
        if (account.getBalance().add(balanceAdjustment).compareTo(BigDecimal.ZERO) < 0) {
            return null; // 余额不足
        }
        
        // 更新账户余额
        account.setBalance(account.getBalance().add(balanceAdjustment));
        accountMapper.updateBalance(account);
        
        // 删除交易记录
        accountLogMapper.deleteById(tid);
        
        return convertToDTO(account);
    }
} 