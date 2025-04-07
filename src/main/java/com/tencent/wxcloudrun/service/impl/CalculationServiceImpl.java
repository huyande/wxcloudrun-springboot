package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.CalculationMapper;
import com.tencent.wxcloudrun.model.Calculation;
import com.tencent.wxcloudrun.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculationServiceImpl implements CalculationService {

    private final CalculationMapper calculationMapper;

    public CalculationServiceImpl(@Autowired CalculationMapper calculationMapper) {
        this.calculationMapper = calculationMapper;
    }

    @Override
    public Calculation createCalculation(Calculation calculation) {
        calculationMapper.insert(calculation);
        return calculation;
    }

    @Override
    public Calculation getCalculationById(Integer id) {
        return calculationMapper.getById(id);
    }

    @Override
    public List<Calculation> getCalculationsByMid(Integer mid) {
        return calculationMapper.getByMid(mid);
    }

    @Override
    public List<Calculation> getAllCalculations() {
        return calculationMapper.getAll();
    }

    @Override
    public boolean updateCalculation(Calculation calculation) {
        return calculationMapper.update(calculation) > 0;
    }

    @Override
    public boolean deleteCalculation(Integer id) {
        return calculationMapper.delete(id) > 0;
    }
} 