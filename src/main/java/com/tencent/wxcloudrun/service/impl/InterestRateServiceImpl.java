package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.InterestRateMapper;
import com.tencent.wxcloudrun.model.InterestRate;
import com.tencent.wxcloudrun.service.InterestRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InterestRateServiceImpl implements InterestRateService {

    final InterestRateMapper interestRateMapper;

    public InterestRateServiceImpl(@Autowired InterestRateMapper interestRateMapper) {
        this.interestRateMapper = interestRateMapper;
    }

    @Override
    @Transactional
    public InterestRate createInterestRate(Integer mid, BigDecimal annualRate) {
        InterestRate currentInterestRateByMid = interestRateMapper.getCurrentInterestRateByMid(mid);
        if(currentInterestRateByMid!=null){
            currentInterestRateByMid.setAnnualRate(annualRate);
            interestRateMapper.updateById(currentInterestRateByMid);
            return currentInterestRateByMid;
        }else{
            // 检查是否存在相同mid的利率
            InterestRate interestRate = new InterestRate();
            interestRate.setMid(mid);
            interestRate.setAnnualRate(annualRate);
            interestRateMapper.insertOne(interestRate);
            return interestRate;
        }
    }

    @Override
    public InterestRate getInterestRateById(Integer id) {
        return interestRateMapper.getInterestRateById(id);
    }

    @Override
    public InterestRate getCurrentInterestRateByMid(Integer mid) {
        return interestRateMapper.getCurrentInterestRateByMid(mid);
    }

    @Override
    public List<InterestRate> getInterestRateHistoryByMid(Integer mid) {
        return interestRateMapper.getInterestRateHistoryByMid(mid);
    }

    @Override
    @Transactional
    public InterestRate updateInterestRate(Integer id, BigDecimal annualRate) {
        InterestRate interestRate = interestRateMapper.getInterestRateById(id);
        if (interestRate != null) {
            interestRate.setAnnualRate(annualRate);
            interestRateMapper.updateById(interestRate);
        }
        return interestRate;
    }

    @Override
    @Transactional
    public void deleteInterestRate(Integer id) {
        interestRateMapper.deleteById(id);
    }
} 