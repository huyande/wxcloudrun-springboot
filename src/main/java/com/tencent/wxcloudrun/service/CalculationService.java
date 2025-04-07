package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Calculation;

import java.util.List;

/**
 * Calculation 业务接口
 */
public interface CalculationService {

    /**
     * 创建计算记录
     * @param calculation 计算记录
     * @return 创建的计算记录
     */
    Calculation createCalculation(Calculation calculation);

    /**
     * 根据ID查询计算记录
     * @param id 记录ID
     * @return 计算记录
     */
    Calculation getCalculationById(Integer id);

    /**
     * 根据用户ID查询计算记录列表
     * @param mid 用户ID
     * @return 计算记录列表
     */
    List<Calculation> getCalculationsByMid(Integer mid);

    /**
     * 获取所有计算记录
     * @return 计算记录列表
     */
    List<Calculation> getAllCalculations();

    /**
     * 更新计算记录
     * @param calculation 计算记录
     * @return 是否更新成功
     */
    boolean updateCalculation(Calculation calculation);

    /**
     * 删除计算记录
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean deleteCalculation(Integer id);
} 