package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.InterestRate;
import java.math.BigDecimal;
import java.util.List;

public interface InterestRateService {
    InterestRate createInterestRate(Integer mid, BigDecimal annualRate);
    
    InterestRate getInterestRateById(Integer id);
    
    InterestRate getCurrentInterestRateByMid(Integer mid);
    
    List<InterestRate> getInterestRateHistoryByMid(Integer mid);
    
    InterestRate updateInterestRate(Integer id, BigDecimal annualRate);
    
    void deleteInterestRate(Integer id);
} 