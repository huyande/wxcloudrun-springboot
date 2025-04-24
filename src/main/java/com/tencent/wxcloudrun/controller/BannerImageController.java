package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.BannerImage;
import com.tencent.wxcloudrun.service.BannerImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/banners")
public class BannerImageController {

    final BannerImageService bannerImageService;
    final Logger logger;

    public BannerImageController(@Autowired BannerImageService bannerImageService) {
        this.bannerImageService = bannerImageService;
        this.logger = LoggerFactory.getLogger(BannerImageController.class);
    }

    @GetMapping("/{id}")
    public ApiResponse getBanner(@PathVariable Integer id) {
        logger.info("/api/banners/{} GET request", id);
        BannerImage banner = bannerImageService.getBannerImageById(id);
        return ApiResponse.ok(banner);
    }

    @GetMapping
    public ApiResponse listBanners() {
        logger.info("/api/banners GET request");
        return ApiResponse.ok(bannerImageService.getAllBannerImages());
    }
    
    @GetMapping("/enabled")
    public ApiResponse listEnabledBanners() {
        logger.info("/api/banners/enabled GET request");
        return ApiResponse.ok(bannerImageService.getAllEnabledBannerImages());
    }

    @PostMapping
    public ApiResponse createBanner(@RequestBody BannerImage bannerImage) {
        logger.info("/api/banners POST request");
        return ApiResponse.ok(bannerImageService.createBannerImage(bannerImage));
    }

    @PutMapping("/{id}")
    public ApiResponse updateBanner(@PathVariable Integer id, @RequestBody BannerImage bannerImage) {
        logger.info("/api/banners/{} PUT request", id);
        bannerImage.setId(id);
        bannerImageService.updateBannerImage(bannerImage);
        return ApiResponse.ok();
    }
    
    @PutMapping("/{id}/status/{status}")
    public ApiResponse updateBannerStatus(@PathVariable Integer id, @PathVariable Integer status) {
        logger.info("/api/banners/{}/status/{} PUT request", id, status);
        bannerImageService.updateBannerImageStatus(id, status);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteBanner(@PathVariable Integer id) {
        logger.info("/api/banners/{} DELETE request", id);
        bannerImageService.deleteBannerImage(id);
        return ApiResponse.ok();
    }
} 