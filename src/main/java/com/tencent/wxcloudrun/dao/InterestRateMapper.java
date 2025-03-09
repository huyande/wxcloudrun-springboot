package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.InterestRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InterestRateMapper {
    void insertOne(InterestRate interestRate);
    
    InterestRate getInterestRateById(@Param("id") Integer id);
    
    InterestRate getCurrentInterestRateByMid(@Param("mid") Integer mid);
    
    List<InterestRate> getInterestRateHistoryByMid(@Param("mid") Integer mid);
    
    void updateById(InterestRate interestRate);
    
    void deleteById(@Param("id") Integer id);
} 