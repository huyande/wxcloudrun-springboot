package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AccountMapper {
    void insertOne(Account account);
    
    Account getAccountById(@Param("id") Integer id);
    
    Account getAccountByMid(@Param("mid") Integer mid);
    
    List<Account> getAccountsByUid(@Param("uid") Integer uid);
    
    void updateById(Account account);
    
    void deleteById(@Param("id") Integer id);
    
    void updateBalance(Account account);
} 