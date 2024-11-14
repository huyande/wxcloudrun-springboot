package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.WishRequest;
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
    public ApiResponse createWish(@RequestBody WishRequest request) {
        return ApiResponse.ok(wishService.createWish(request));
    }

    @PutMapping("/update")
    public ApiResponse updateWish(@RequestBody WishRequest request) {
        return ApiResponse.ok(wishService.updateWish(request));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteWish(@PathVariable Integer id) {
        wishService.deleteWish(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}")
    public ApiResponse getWish(@PathVariable Integer id) {
        return ApiResponse.ok(wishService.getWishById(id));
    }

    @GetMapping("/list")
    public ApiResponse getAllWishes() {
        return ApiResponse.ok(wishService.getAllWishes());
    }

    @GetMapping("/member/{mid}")
    public ApiResponse getWishesByMid(@PathVariable String mid) {
        return ApiResponse.ok(wishService.getWishesByMid(mid));
    }
}
