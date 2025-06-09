package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CanWishExchangeDto;
import com.tencent.wxcloudrun.dto.WishLogRequest;
import com.tencent.wxcloudrun.model.SeasonConfig;
import com.tencent.wxcloudrun.model.SeasonWishLog;
import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.model.WishLog;
import com.tencent.wxcloudrun.service.MemberPointLogsService;
import com.tencent.wxcloudrun.service.SeasonConfigService;
import com.tencent.wxcloudrun.service.WishLogService;
import com.tencent.wxcloudrun.service.WishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wishLog")
public class WishLogController {

    final WishLogService wishLogService;
    final WishService wishService;
    final Logger logger;
    final MemberPointLogsService memberPointLogsService;
    final SeasonConfigService seasonConfigService;

    public WishLogController(@Autowired WishLogService wishLogService,
                             @Autowired WishService wishService,
                             @Autowired MemberPointLogsService memberPointLogsService,
                             @Autowired SeasonConfigService seasonConfigService) {
        this.wishLogService = wishLogService;
        this.wishService = wishService;
        this.memberPointLogsService = memberPointLogsService;
        this.seasonConfigService = seasonConfigService;
        this.logger = LoggerFactory.getLogger(WishLogController.class);
    }

    /**
     * 根据ID查询愿望日志
     * @param seasonId 季节ID
     * @param id 愿望日志ID
     * @return API响应
     */
    @GetMapping("/{id}")
    ApiResponse get(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId, 
                    @PathVariable Integer id) {
        try {
            Object wishLogData;
            Class<?> expectedWishLogType;
            Class<?> expectedWishType;

            if (seasonId != null) {
                expectedWishLogType = SeasonWishLog.class;
                expectedWishType = com.tencent.wxcloudrun.model.SeasonWish.class;
                wishLogData = wishLogService.getById(id, seasonId, SeasonWishLog.class);
            } else {
                expectedWishLogType = WishLog.class;
                expectedWishType = Wish.class;
                wishLogData = wishLogService.getById(id, null, WishLog.class);
            }

            if (wishLogData == null) {
                return ApiResponse.error("愿望日志不存在");
            }

            Map<String,Object> res = new HashMap<>();
            res.put("wishLog", wishLogData);

            Integer widToFetch = null;
            if (wishLogData instanceof WishLog) {
                widToFetch = ((WishLog) wishLogData).getWid();
            } else if (wishLogData instanceof SeasonWishLog) {
                Long seasonWishLogWid = ((SeasonWishLog) wishLogData).getWid(); 
                if (seasonWishLogWid != null) {
                    if (seasonWishLogWid > Integer.MAX_VALUE || seasonWishLogWid < Integer.MIN_VALUE) {
                        logger.warn("SeasonWishLog WID " + seasonWishLogWid + " is too large for Integer, cannot fetch associated SeasonWish via current wishService.getWishById signature");
                    } else {
                        widToFetch = seasonWishLogWid.intValue();
                    }
                }            
            }

            if (widToFetch != null) {
                Object wishData = wishService.getWishById(widToFetch, seasonId, expectedWishType);
                res.put("wish", wishData);
            } else {
                res.put("wish", null);
            }
            
            return ApiResponse.ok(res);
        } catch (Exception e) {
            logger.error("查询愿望日志失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/getAllLogByStatus/{mid}")
    ApiResponse getAllLogByStatus(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                  @PathVariable Integer mid, @RequestParam Integer status) {
        try {
            if (seasonId != null) {
                return ApiResponse.ok(wishLogService.getAllLogByStatus(mid, status, seasonId, SeasonWishLog.class));
            } else {
                return ApiResponse.ok(wishLogService.getAllLogByStatus(mid, status, null, WishLog.class));
            }
        } catch (Exception e) {
            logger.error("查询愿望日志失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询愿望日志列表
     * @param uid 用户ID
     * @return API响应
     */
    @GetMapping("/user/{uid}")
    ApiResponse getByUid(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                         @PathVariable String uid) {
        try {
            if (seasonId != null) {
                return ApiResponse.ok(wishLogService.getByUid(uid, seasonId, SeasonWishLog.class));
            } else {
                return ApiResponse.ok(wishLogService.getByUid(uid, null, WishLog.class));
            }
        } catch (Exception e) {
            logger.error("查询用户愿望日志列表失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据会员ID分页查询愿望日志列表
     * @param mid 会员ID
     * @param page 页码，默认1
     * @param pageSize 每页数量，默认10
     * @param status 状态过滤，可选
     * @return API响应
     */
    @GetMapping("/member/{mid}")
    ApiResponse getByMid(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
            @PathVariable Integer mid,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> result = wishLogService.getByMidWithPage(mid, page, pageSize, status, seasonId);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            logger.error("分页查询愿望日志列表失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 根据愿望ID查询愿望日志列表
     * @param wid 愿望ID
     * @return API响应
     */
    @GetMapping("/wish/{wid}")
    ApiResponse getByWid(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                         @PathVariable Integer wid) {
        try {
            if (seasonId != null) {
                return ApiResponse.ok(wishLogService.getByWid(wid, seasonId, SeasonWishLog.class));
            } else {
                return ApiResponse.ok(wishLogService.getByWid(wid, null, WishLog.class));
            }
        } catch (Exception e) {
            logger.error("查询愿望相关日志列表失败", e);
            return ApiResponse.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 创建愿望日志
     * @param
     * @return API响应
     */
    @PostMapping
    ApiResponse create(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                       @RequestBody WishLogRequest wishLogRequest) {
        try {
            if (wishLogRequest.getUid() == null || wishLogRequest.getWid() == null || wishLogRequest.getPoint() == null || wishLogRequest.getMid() == null) {
                return ApiResponse.error("参数不完整 (uid, wid, mid, point are required)");
            }

            if (seasonId != null) {
                SeasonConfig seasonConfig = seasonConfigService.getById(seasonId);
                if(seasonConfig.getEndTime()!=null && seasonConfig.getEndTime().isBefore(LocalDateTime.now())){
                    return ApiResponse.error("赛季已结束,不能兑换");
                }
                if(seasonConfig.getStartTime()!=null && seasonConfig.getStartTime().isAfter(LocalDateTime.now())){
                    return ApiResponse.error("赛季未开始,不能兑换");
                }
                CanWishExchangeDto canWishExchangeDto = wishLogService.canSeasonWishExchange(wishLogRequest, seasonId);
                if(canWishExchangeDto.isIs()){
                    SeasonWishLog log = wishLogService.createWishLog(wishLogRequest, seasonId, SeasonWishLog.class);
                    return ApiResponse.ok(log);
                }else{
                    return ApiResponse.error(canWishExchangeDto.getMsg());
                }
            } else {
                CanWishExchangeDto canWishExchangeDto = wishLogService.canWishExchange(wishLogRequest);
                if(canWishExchangeDto.isIs()){
                    WishLog log = wishLogService.createWishLog(wishLogRequest, null, WishLog.class);
                    return ApiResponse.ok(log);
                }else{
                    return ApiResponse.error(canWishExchangeDto.getMsg());
                }

            }
        } catch (Exception e) {
            logger.error("创建愿望日志失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新愿望日志
     * @param id 愿望日志ID
     * @param wishLogRequest 愿望日志对象
     * @return API响应
     */
    @PutMapping("/{id}")
    ApiResponse update(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                       @PathVariable Integer id, @RequestBody WishLogRequest wishLogRequest) {
        try {
            if (seasonId != null) {
                SeasonWishLog updatedLog = wishLogService.updateWishLog(id, wishLogRequest, seasonId, SeasonWishLog.class);
                return ApiResponse.ok(updatedLog);
            } else {
                WishLog updatedLog = wishLogService.updateWishLog(id, wishLogRequest, null, WishLog.class);
                return ApiResponse.ok(updatedLog);
            }
        } catch (Exception e) {
            logger.error("更新愿望日志失败", e);
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除愿望日志
     * @param id 愿望日志ID
     * @return API响应
     */
    @DeleteMapping("/{id}")
    ApiResponse delete(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                       @PathVariable Integer id) {
        try {
            wishLogService.deleteById(id, seasonId);
            return ApiResponse.ok();
        } catch (Exception e) {
            logger.error("删除愿望日志失败", e);
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

}
