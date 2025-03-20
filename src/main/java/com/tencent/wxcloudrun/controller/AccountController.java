package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.AccountDTO;
import com.tencent.wxcloudrun.dto.CategoryStatDTO;
import com.tencent.wxcloudrun.model.Account;
import com.tencent.wxcloudrun.model.AccountLog;
import com.tencent.wxcloudrun.model.InterestRate;
import com.tencent.wxcloudrun.service.AccountService;
import com.tencent.wxcloudrun.service.AccountLogService;
import com.tencent.wxcloudrun.service.InterestRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 零钱宝账户管理接口
 */
@RestController
@RequestMapping("account")
public class AccountController {

    final AccountService accountService;
    final AccountLogService accountLogService;
    final InterestRateService interestRateService;
    final Logger logger;

    public AccountController(@Autowired AccountService accountService,
                           @Autowired AccountLogService accountLogService,
                           @Autowired InterestRateService interestRateService) {
        this.accountService = accountService;
        this.accountLogService = accountLogService;
        this.interestRateService = interestRateService;
        this.logger = LoggerFactory.getLogger(AccountController.class);
    }

    /**
     * 获取或创建零钱宝账户（如果不存在）
     * @param request 包含uid(用户ID)和mid(成员ID)的请求体
     * @return 账户信息
     */
    @PostMapping("/create")
    public ApiResponse getOrCreateAccount(@RequestBody Map<String, Integer> request) {
        try {
            Integer uid = request.get("uid");
            Integer mid = request.get("mid");
            
            // 参数校验
            if (uid == null || mid == null) {
                logger.error("创建账户失败：uid或mid为空");
                return ApiResponse.error("uid和mid不能为空");
            }
            
            AccountDTO accountDTO = accountService.getOrCreateAccount(uid, mid);
            return ApiResponse.ok(accountDTO);
        } catch (Exception e) {
            logger.error("创建账户异常", e);
            return ApiResponse.error("创建账户失败：" + e.getMessage());
        }
    }

    /**
     * 根据账户ID查询账户信息
     * @param id 账户ID
     * @return 账户信息
     */
    @GetMapping("/{id}")
    public ApiResponse getAccountById(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ApiResponse.error("无效的账户ID");
            }
            
            AccountDTO accountDTO = accountService.getAccountById(id);
            if (accountDTO == null) {
                return ApiResponse.error("账户不存在");
            }
            return ApiResponse.ok(accountDTO);
        } catch (Exception e) {
            logger.error("查询账户异常，id: {}", id, e);
            return ApiResponse.error("查询账户失败：" + e.getMessage());
        }
    }

    /**
     * 根据成员ID查询账户信息
     * @param mid 成员ID
     * @return 账户信息
     */
    @GetMapping("/member/{mid}")
    public ApiResponse getAccountByMid(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            AccountDTO accountDTO = accountService.getAccountByMid(mid);
            if (accountDTO == null) {
                return ApiResponse.error("账户不存在");
            }
            return ApiResponse.ok(accountDTO);
        } catch (Exception e) {
            logger.error("查询账户异常，mid: {}", mid, e);
            return ApiResponse.error("查询账户失败：" + e.getMessage());
        }
    }

    /**
     * 查询用户名下的所有账户
     * @param uid 用户ID
     * @return 账户列表
     */
    @GetMapping("/user/{uid}")
    public ApiResponse getAccountsByUid(@PathVariable Integer uid) {
        try {
            if (uid == null || uid <= 0) {
                return ApiResponse.error("无效的用户ID");
            }
            
            List<AccountDTO> accounts = accountService.getAccountsByUid(uid);
            return ApiResponse.ok(accounts);
        } catch (Exception e) {
            logger.error("查询用户账户列表异常，uid: {}", uid, e);
            return ApiResponse.error("查询账户列表失败：" + e.getMessage());
        }
    }

    /**
     * 更新账户储蓄目标金额
     * @param id 账户ID
     * @param request 包含targetAmount(目标金额)的请求体
     * @return 更新后的账户信息
     */
    @PutMapping("/{id}")
    public ApiResponse updateAccount(@PathVariable Integer id, @RequestBody Map<String, BigDecimal> request) {
        try {
            if (id == null || id <= 0) {
                return ApiResponse.error("无效的账户ID");
            }
            
            BigDecimal targetAmount = request.get("targetAmount");
            if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) < 0) {
                return ApiResponse.error("目标金额不能为空或小于0");
            }
            
            AccountDTO accountDTO = accountService.updateAccount(id, targetAmount);
            if (accountDTO == null) {
                return ApiResponse.error("账户不存在");
            }
            return ApiResponse.ok(accountDTO);
        } catch (Exception e) {
            logger.error("更新账户异常，id: {}", id, e);
            return ApiResponse.error("更新账户失败：" + e.getMessage());
        }
    }

    /**
     * 删除账户
     * @param id 账户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse deleteAccount(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ApiResponse.error("无效的账户ID");
            }
            
            accountService.deleteAccount(id);
            return ApiResponse.ok();
        } catch (Exception e) {
            logger.error("删除账户异常，id: {}", id, e);
            return ApiResponse.error("删除账户失败：" + e.getMessage());
        }
    }

    /**
     * 增加账户余额
     * @param mid 成员ID
     * @param request 包含amount(金额)、category(分类)、remark(备注)、createdAt(记录时间)的请求体
     * @return 更新后的账户信息
     */
    @PostMapping("/add/{mid}")
    public ApiResponse addBalance(@PathVariable Integer mid, @RequestBody Map<String, Object> request) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            // 参数校验
            if (!request.containsKey("amount")) {
                return ApiResponse.error("金额不能为空");
            }
            
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("金额必须大于0");
            }
            
            String category = (String) request.get("category");
            String remark = (String) request.get("remark");
            String createdAt = (String) request.get("createdAt");
            
            if (category == null || category.trim().isEmpty()) {
                return ApiResponse.error("交易分类不能为空");
            }
            
            AccountDTO accountDTO = accountService.addBalance(mid, amount, category, remark, createdAt);
            if (accountDTO == null) {
                return ApiResponse.error("账户不存在");
            }
            return ApiResponse.ok(accountDTO);
        } catch (NumberFormatException e) {
            logger.error("金额格式错误，mid: {}", mid, e);
            return ApiResponse.error("金额格式错误");
        } catch (Exception e) {
            logger.error("增加余额异常，mid: {}", mid, e);
            return ApiResponse.error("增加余额失败：" + e.getMessage());
        }
    }

    /**
     * 减少账户余额
     * @param mid 成员ID
     * @param request 包含amount(金额)、category(分类)、remark(备注)、type(类型)、createdAt(记录时间)的请求体
     * @return 更新后的账户信息
     */
    @PostMapping("/reduce/{mid}")
    public ApiResponse reduceBalance(@PathVariable Integer mid, @RequestBody Map<String, Object> request) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            // 参数校验
            if (!request.containsKey("amount")) {
                return ApiResponse.error("金额不能为空");
            }
            
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("金额必须大于0");
            }
            
            String category = (String) request.get("category");
            String remark = (String) request.get("remark");
            String type = (String) request.get("type");
            String createdAt = (String) request.get("createdAt");
            
            if (category == null || category.trim().isEmpty()) {
                return ApiResponse.error("交易分类不能为空");
            }
            
            AccountDTO accountDTO = accountService.reduceBalance(mid, amount, category, remark, type, createdAt);
            if (accountDTO == null) {
                return ApiResponse.error("账户不存在或余额不足");
            }
            return ApiResponse.ok(accountDTO);
        } catch (NumberFormatException e) {
            logger.error("金额格式错误，mid: {}", mid, e);
            return ApiResponse.error("金额格式错误");
        } catch (Exception e) {
            logger.error("减少余额异常，mid: {}", mid, e);
            return ApiResponse.error("减少余额失败：" + e.getMessage());
        }
    }

    /**
     * 计算并发放每日利息
     * @param mid 成员ID
     * @return 操作结果
     */
    @PostMapping("/interest/{mid}")
    public ApiResponse calculateInterest(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            accountService.calculateDailyInterest(mid);
            return ApiResponse.ok();
        } catch (Exception e) {
            logger.error("计算利息异常，mid: {}", mid, e);
            return ApiResponse.error("计算利息失败：" + e.getMessage());
        }
    }

    /**
     * 设置账户年化利率
     * @param mid 成员ID
     * @param request 包含annualRate(年化利率)的请求体
     * @return 创建的利率信息
     */
    @PostMapping("/rate/{mid}")
    public ApiResponse setInterestRate(@PathVariable Integer mid, @RequestBody Map<String, BigDecimal> request) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            BigDecimal annualRate = request.get("annualRate");
            if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) < 0) {
                return ApiResponse.error("年化利率不能为空或小于0");
            }
            
            InterestRate rate = interestRateService.createInterestRate(mid, annualRate);
            return ApiResponse.ok(rate);
        } catch (Exception e) {
            logger.error("设置利率异常，mid: {}", mid, e);
            return ApiResponse.error("设置利率失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前生效的利率信息
     * @param mid 成员ID
     * @return 当前利率信息
     */
    @GetMapping("/rate/{mid}")
    public ApiResponse getCurrentRate(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            InterestRate rate = interestRateService.getCurrentInterestRateByMid(mid);
            if (rate == null) {
                return ApiResponse.error("未设置利率");
            }
            return ApiResponse.ok(rate);
        } catch (Exception e) {
            logger.error("查询当前利率异常，mid: {}", mid, e);
            return ApiResponse.error("查询利率失败：" + e.getMessage());
        }
    }

    /**
     * 查询利率调整历史记录
     * @param mid 成员ID
     * @return 利率历史记录列表
     */
    @GetMapping("/rate/history/{mid}")
    public ApiResponse getRateHistory(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            List<InterestRate> rates = interestRateService.getInterestRateHistoryByMid(mid);
            return ApiResponse.ok(rates);
        } catch (Exception e) {
            logger.error("查询利率历史异常，mid: {}", mid, e);
            return ApiResponse.error("查询利率历史失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询交易记录
     * @param mid 成员ID
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 交易记录列表
     */
    @GetMapping("/logs/{mid}")
    public ApiResponse getLogs(@PathVariable Integer mid,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 20;
            }
            
            List<AccountLog> logs = accountLogService.getLogsByMid(mid, page, pageSize);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("查询交易记录异常，mid: {}", mid, e);
            return ApiResponse.error("查询交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 按交易类型分页查询交易记录
     * @param mid 成员ID
     * @param type 交易类型（增加/支出）
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 交易记录列表
     */
    @GetMapping("/logs/{mid}/type")
    public ApiResponse getLogsByType(@PathVariable Integer mid,
                                   @RequestParam String type,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            if (type == null || (!type.equals("增加") && !type.equals("支出"))) {
                return ApiResponse.error("无效的交易类型");
            }
            
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 20;
            }
            
            List<AccountLog> logs = accountLogService.getLogsByMidAndType(mid, type, page, pageSize);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("按类型查询交易记录异常，mid: {}, type: {}", mid, type, e);
            return ApiResponse.error("查询交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 按交易分类分页查询交易记录
     * @param mid 成员ID
     * @param category 交易分类（奖励/惩罚/利息/其他）
     * @param page 页码（从1开始）
     * @param pageSize 每页记录数
     * @return 交易记录列表
     */
    @GetMapping("/logs/{mid}/category")
    public ApiResponse getLogsByCategory(@PathVariable Integer mid,
                                       @RequestParam String category,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            if (category == null || category.trim().isEmpty()) {
                return ApiResponse.error("交易分类不能为空");
            }
            
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1 || pageSize > 100) {
                pageSize = 20;
            }
            
            List<AccountLog> logs = accountLogService.getLogsByMidAndCategory(mid, category, page, pageSize);
            return ApiResponse.ok(logs);
        } catch (Exception e) {
            logger.error("按分类查询交易记录异常，mid: {}, category: {}", mid, category, e);
            return ApiResponse.error("查询交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取交易记录总数
     * @param mid 成员ID
     * @return 交易记录总数
     */
    @GetMapping("/logs/count/{mid}")
    public ApiResponse getLogsCount(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            Integer count = accountLogService.getLogsCountByMid(mid);
            return ApiResponse.ok(count);
        } catch (Exception e) {
            logger.error("查询交易记录总数异常，mid: {}", mid, e);
            return ApiResponse.error("查询交易记录总数失败：" + e.getMessage());
        }
    }

    /**
     * 获取交易分类统计数据（用于饼图展示）
     * @param mid 成员ID
     * @return 分类统计数据
     */
    @GetMapping("/stats/category/{mid}")
    public ApiResponse getCategoryStats(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }
            
            List<CategoryStatDTO> stats = accountLogService.getCategoryStats(mid);
            return ApiResponse.ok(stats);
        } catch (Exception e) {
            logger.error("查询交易分类统计异常，mid: {}", mid, e);
            return ApiResponse.error("查询交易分类统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取交易分类统计数据（用于饼图展示）
     * @param mid 成员ID
     * @return 分类统计数据
     */
    @GetMapping("/stats/type/{mid}")
    public ApiResponse getTypeStats(@PathVariable Integer mid) {
        try {
            if (mid == null || mid <= 0) {
                return ApiResponse.error("无效的成员ID");
            }

            List<CategoryStatDTO> stats = accountLogService.getTypeStats(mid);
            return ApiResponse.ok(stats);
        } catch (Exception e) {
            logger.error("查询交易分类统计异常，mid: {}", mid, e);
            return ApiResponse.error("查询交易分类统计失败：" + e.getMessage());
        }
    }

    /**
     * 修改交易记录
     * @param tid 交易ID
     * @param request 包含amount(金额)、category(分类)、remark(备注)、type(类型)、createdAt(记录时间)的请求体
     * @return 更新后的账户信息
     */
    @PutMapping("/log/{tid}")
    public ApiResponse updateAccountLog(@PathVariable Integer tid, @RequestBody Map<String, Object> request) {
        try {
            if (tid == null || tid <= 0) {
                return ApiResponse.error("无效的交易ID");
            }
            
            // 参数校验
            if (!request.containsKey("amount")) {
                return ApiResponse.error("金额不能为空");
            }
            
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ApiResponse.error("金额必须大于0");
            }
            
            String category = (String) request.get("category");
            if (category == null || category.trim().isEmpty()) {
                return ApiResponse.error("交易分类不能为空");
            }
            
            String remark = (String) request.get("remark");
            String type = (String) request.get("type");
            String createdAt = (String) request.get("createdAt");
            
            if (type == null || (!type.equals("增加") && !type.equals("支出"))) {
                return ApiResponse.error("无效的交易类型");
            }
            
            AccountDTO accountDTO = accountService.updateAccountLog(tid, amount, category, remark, type, createdAt);
            if (accountDTO == null) {
                return ApiResponse.error("交易记录不存在或余额不足");
            }
            return ApiResponse.ok(accountDTO);
        } catch (NumberFormatException e) {
            logger.error("金额格式错误，tid: {}", tid, e);
            return ApiResponse.error("金额格式错误");
        } catch (Exception e) {
            logger.error("修改交易记录异常，tid: {}", tid, e);
            return ApiResponse.error("修改交易记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除交易记录
     * @param tid 交易ID
     * @return 更新后的账户信息
     */
    @DeleteMapping("/log/{tid}")
    public ApiResponse deleteAccountLog(@PathVariable Integer tid) {
        try {
            if (tid == null || tid <= 0) {
                return ApiResponse.error("无效的交易ID");
            }
            
            AccountDTO accountDTO = accountService.deleteAccountLog(tid);
            if (accountDTO == null) {
                return ApiResponse.error("交易记录不存在或余额不足");
            }
            return ApiResponse.ok(accountDTO);
        } catch (Exception e) {
            logger.error("删除交易记录异常，tid: {}", tid, e);
            return ApiResponse.error("删除交易记录失败：" + e.getMessage());
        }
    }
} 