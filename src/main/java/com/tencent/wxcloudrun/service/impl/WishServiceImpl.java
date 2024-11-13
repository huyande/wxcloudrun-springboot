package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.WishMapper;
import com.tencent.wxcloudrun.dto.WishRequest;
import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.service.WishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishServiceImpl implements WishService {
    
    private final WishMapper wishMapper;
    
    public WishServiceImpl(WishMapper wishMapper) {
        this.wishMapper = wishMapper;
    }
    
    @Override
    @Transactional
    public Wish createWish(WishRequest request) {
        Wish wish = new Wish();
        BeanUtils.copyProperties(request, wish);
//        wish.setCreatedAt(LocalDateTime.now());
//        wish.setUpdatedAt(LocalDateTime.now());
        wishMapper.insert(wish);
        return wish;
    }
    
    @Override
    @Transactional
    public Wish updateWish(WishRequest request) {
        Wish wish = wishMapper.getById(request.getId());
        if (wish == null) {
            throw new RuntimeException("愿望不存在");
        }
        BeanUtils.copyProperties(request, wish);
        wish.setUpdatedAt(LocalDateTime.now());
        wishMapper.update(wish);
        return wish;
    }
    
    @Override
    @Transactional
    public void deleteWish(Integer id) {
        wishMapper.delete(id);
    }
    
    @Override
    public Wish getWishById(Integer id) {
        return wishMapper.getById(id);
    }
    
    @Override
    public List<Wish> getAllWishes() {
        return wishMapper.findAll();
    }
    
    @Override
    public List<Wish> getWishesByMid(String mid) {
        return wishMapper.findByMid(mid);
    }
}
