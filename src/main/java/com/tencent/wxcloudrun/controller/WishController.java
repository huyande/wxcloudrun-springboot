package com.tencent.wxcloudrun.controller;

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
    public Wish createWish(@RequestBody WishRequest request) {
        return wishService.createWish(request);
    }

    @PutMapping("/update")
    public Wish updateWish(@RequestBody WishRequest request) {
        return wishService.updateWish(request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteWish(@PathVariable Integer id) {
        wishService.deleteWish(id);
    }

    @GetMapping("/{id}")
    public Wish getWish(@PathVariable Integer id) {
        return wishService.getWishById(id);
    }

    @GetMapping("/list")
    public List<Wish> getAllWishes() {
        return wishService.getAllWishes();
    }

    @GetMapping("/member/{mid}")
    public List<Wish> getWishesByMid(@PathVariable String mid) {
        return wishService.getWishesByMid(mid);
    }
}
