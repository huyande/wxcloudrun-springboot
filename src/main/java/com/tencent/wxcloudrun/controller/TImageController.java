package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.TImage;
import com.tencent.wxcloudrun.service.TImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
public class TImageController {

    final TImageService tImageService;
    final Logger logger;

    public TImageController(@Autowired TImageService tImageService) {
        this.tImageService = tImageService;
        this.logger = LoggerFactory.getLogger(TImageController.class);
    }

    @GetMapping("/{id}")
    public ApiResponse getImage(@PathVariable Integer id) {
        logger.info("/api/images/{} GET request", id);
        TImage image = tImageService.getImageById(id);
        return ApiResponse.ok(image);
    }

    @GetMapping
    public ApiResponse listImages() {
        logger.info("/api/images GET request");
        return ApiResponse.ok(tImageService.getAllImages());
    }

    @GetMapping("/location/{location}")
    public ApiResponse getImagesByLocation(@PathVariable String location) {
        logger.info("/api/images/location/{} GET request", location);
        return ApiResponse.ok(tImageService.getImagesByLocation(location));
    }

    @PostMapping
    public ApiResponse createImage(@RequestBody TImage image) {
        logger.info("/api/images POST request");
        return ApiResponse.ok(tImageService.createImage(image));
    }

    @PutMapping("/{id}")
    public ApiResponse updateImage(@PathVariable Integer id, @RequestBody TImage image) {
        logger.info("/api/images/{} PUT request", id);
        image.setId(id);
        tImageService.updateImage(image);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteImage(@PathVariable Integer id) {
        logger.info("/api/images/{} DELETE request", id);
        tImageService.deleteImage(id);
        return ApiResponse.ok();
    }
} 