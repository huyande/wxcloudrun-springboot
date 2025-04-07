package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CalculationRequest;
import com.tencent.wxcloudrun.model.Calculation;
import com.tencent.wxcloudrun.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calculation")
public class CalculationController {

    private final CalculationService calculationService;

    public CalculationController(@Autowired CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * 创建计算记录
     * @param request 计算记录请求
     * @return API响应
     */
    @PostMapping
    public ApiResponse createCalculation(@RequestBody CalculationRequest request) {
        Calculation calculation = new Calculation();
        calculation.setMid(request.getMid());
        calculation.setTotalQuestions(request.getTotalQuestions());
        
        Calculation created = calculationService.createCalculation(calculation);
        return ApiResponse.ok(created);
    }

    /**
     * 创建或更新计算记录
     * 如果用户已有记录则更新，否则创建新记录
     * @param request 计算记录请求
     * @return API响应
     */
    @PutMapping("/save")
    public ApiResponse saveCalculation(@RequestBody CalculationRequest request) {
        // 检查用户是否已有记录
        List<Calculation> existingRecords = calculationService.getCalculationsByMid(request.getMid());
        
        Calculation calculation;
        boolean isSuccess;
        
        if (existingRecords != null && !existingRecords.isEmpty()) {
            // 更新已有记录（取第一条记录进行更新）
            calculation = existingRecords.get(0);
            calculation.setTotalQuestions(request.getTotalQuestions());
            isSuccess = calculationService.updateCalculation(calculation);
        } else {
            // 创建新记录
            calculation = new Calculation();
            calculation.setMid(request.getMid());
            calculation.setTotalQuestions(request.getTotalQuestions());
            calculation = calculationService.createCalculation(calculation);
            isSuccess = calculation != null && calculation.getId() != null;
        }
        
        if (isSuccess) {
            return ApiResponse.ok(calculation);
        } else {
            return ApiResponse.error("保存失败");
        }
    }

    /**
     * 根据用户ID获取计算记录
     * @param mid 用户ID
     * @return API响应
     */
    @GetMapping("/user/{mid}")
    public ApiResponse getCalculationByMid(@PathVariable Integer mid) {
        List<Calculation> calculations = calculationService.getCalculationsByMid(mid);
        if (calculations == null || calculations.isEmpty()) {
            return ApiResponse.ok(null); // 没有记录时返回null
        }
        // 每个用户只有一条记录，返回第一条
        return ApiResponse.ok(calculations.get(0));
    }

    /**
     * 更新计算记录
     * @param id 记录ID
     * @param request 计算记录请求
     * @return API响应
     */
    @PutMapping("/{id}")
    public ApiResponse updateCalculation(@PathVariable Integer id, @RequestBody CalculationRequest request) {
        Calculation calculation = calculationService.getCalculationById(id);
        if (calculation == null) {
            return ApiResponse.error("计算记录不存在");
        }
        
        calculation.setMid(request.getMid());
        calculation.setTotalQuestions(request.getTotalQuestions());
        
        boolean success = calculationService.updateCalculation(calculation);
        if (success) {
            return ApiResponse.ok(calculation);
        } else {
            return ApiResponse.error("更新失败");
        }
    }

    /**
     * 删除计算记录
     * @param id 记录ID
     * @return API响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse deleteCalculation(@PathVariable Integer id) {
        boolean success = calculationService.deleteCalculation(id);
        if (success) {
            return ApiResponse.ok();
        } else {
            return ApiResponse.error("删除失败");
        }
    }
} 