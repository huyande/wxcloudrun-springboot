package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WishRequest;
import com.tencent.wxcloudrun.model.SeasonWish;
import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.service.WishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @PostMapping("/create")
    public ApiResponse createWish(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                @RequestBody WishRequest request) {
        if (seasonId != null) {
            return ApiResponse.ok(wishService.createWish(request, seasonId, SeasonWish.class));
        } else {
            return ApiResponse.ok(wishService.createWish(request, null, Wish.class));
        }
    }

    @PutMapping("/update")
    public ApiResponse updateWish(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                @RequestBody WishRequest request) {
        if (seasonId != null) {
            return ApiResponse.ok(wishService.updateWish(request, seasonId, SeasonWish.class));
        } else {
            return ApiResponse.ok(wishService.updateWish(request, null, Wish.class));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteWish(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                @PathVariable Integer id) {
        wishService.deleteWish(id, seasonId);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse getWish(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                             @PathVariable Integer id) {
        if (seasonId != null) {
            return ApiResponse.ok(wishService.getWishById(id, seasonId, SeasonWish.class));
        } else {
            return ApiResponse.ok(wishService.getWishById(id, null, Wish.class));
        }
    }

    @GetMapping("/list")
    public ApiResponse getAllWishes(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId) {
        if (seasonId != null) {
            return ApiResponse.ok(wishService.getAllWishes(seasonId, SeasonWish.class));
        } else {
            return ApiResponse.ok(wishService.getAllWishes(null, Wish.class));
        }
    }

    @GetMapping("/member/{mid}")
    public ApiResponse getWishesByMid(@RequestHeader(value = "X-Season-Id", required = false) Long seasonId,
                                    @PathVariable String mid) {
        if (seasonId != null) {
            return ApiResponse.ok(wishService.getWishesByMid(mid, seasonId, SeasonWish.class));
        } else {
            return ApiResponse.ok(wishService.getWishesByMid(mid, null, Wish.class));
        }
    }
}
