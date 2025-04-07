package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Calculation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalculationMapper {
    
    /**
     * 创建计算记录
     * @param calculation 计算记录
     * @return 影响行数
     */
    int insert(Calculation calculation);
    
    /**
     * 根据ID查询计算记录
     * @param id 记录ID
     * @return 计算记录
     */
    Calculation getById(@Param("id") Integer id);
    
    /**
     * 根据用户ID查询计算记录列表
     * @param mid 用户ID
     * @return 计算记录列表
     */
    List<Calculation> getByMid(@Param("mid") Integer mid);
    
    /**
     * 更新计算记录
     * @param calculation 计算记录
     * @return 影响行数
     */
    int update(Calculation calculation);
    
    /**
     * 删除计算记录
     * @param id 记录ID
     * @return 影响行数
     */
    int delete(@Param("id") Integer id);
    
    /**
     * 获取所有计算记录
     * @return 计算记录列表
     */
    List<Calculation> getAll();
} 