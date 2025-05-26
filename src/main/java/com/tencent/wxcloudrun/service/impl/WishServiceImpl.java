package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SeasonWishMapper;
import com.tencent.wxcloudrun.dao.WishMapper;
import com.tencent.wxcloudrun.dto.WishRequest;
import com.tencent.wxcloudrun.model.SeasonWish;
import com.tencent.wxcloudrun.model.Wish;
import com.tencent.wxcloudrun.service.WishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishServiceImpl implements WishService {
    
    private final WishMapper wishMapper;
    private final SeasonWishMapper seasonWishMapper;
    
    public WishServiceImpl(WishMapper wishMapper, SeasonWishMapper seasonWishMapper) {
        this.wishMapper = wishMapper;
        this.seasonWishMapper = seasonWishMapper;
    }
    
    @Override
    @Transactional
    public <T> T createWish(WishRequest request, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            SeasonWish seasonWish = new SeasonWish();
            BeanUtils.copyProperties(request, seasonWish);
            seasonWish.setSeasonId(seasonId);
            // seasonWish.setCreatedAt(LocalDateTime.now()); // Assuming mapper handles this or set by DB default
            // seasonWish.setUpdatedAt(LocalDateTime.now());
            // Assuming request.getMid() is String, SeasonWish.mid is Integer
            if (request.getMid() != null) {
                try {
                    seasonWish.setMid(request.getMid());
                } catch (NumberFormatException e) {
                    // Handle error: mid is not a valid integer for season wish
                    throw new IllegalArgumentException("Invalid mid format for season wish: " + request.getMid());
                }
            }
            
            seasonWishMapper.insert(seasonWish);
            if (expectedType.isAssignableFrom(SeasonWish.class)) {
                return expectedType.cast(seasonWish);
            }
            throw new IllegalArgumentException("Invalid expected type for season wish creation: " + expectedType.getName());
        } else {
            Wish wish = new Wish();
            BeanUtils.copyProperties(request, wish);
            // wish.setMid(request.getMid()); // Assuming Wish.mid is String and matches WishRequest.mid
            // wish.setCreatedAt(LocalDateTime.now());
            // wish.setUpdatedAt(LocalDateTime.now());
            wishMapper.insert(wish);
            if (expectedType.isAssignableFrom(Wish.class)) {
                return expectedType.cast(wish);
            }
            throw new IllegalArgumentException("Invalid expected type for regular wish creation: " + expectedType.getName());
        }
    }
    
    @Override
    @Transactional
    public <T> T updateWish(WishRequest request, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            // WishRequest.id is Integer. SeasonWish.id is Long.
            if (request.getId() == null) {
                 throw new IllegalArgumentException("ID must be provided for update.");
            }
            SeasonWish seasonWish = seasonWishMapper.getById(request.getId().longValue());
            if (seasonWish == null) {
                throw new RuntimeException("赛季愿望不存在, id: " + request.getId());
            }
            if (!seasonWish.getSeasonId().equals(seasonId)) {
                throw new RuntimeException("SeasonId mismatch for update. Expected: " + seasonWish.getSeasonId() + ", got: " + seasonId);
            }
            
            BeanUtils.copyProperties(request, seasonWish);
             // Assuming request.getMid() is String, SeasonWish.mid is Integer
            if (request.getMid() != null) {
                try {
                    seasonWish.setMid(request.getMid());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid mid format for season wish: " + request.getMid());
                }
            } else {
                 seasonWish.setMid(null); // Explicitly set to null if request.mid is null
            }

            seasonWish.setUpdatedAt(LocalDateTime.now());
            seasonWishMapper.update(seasonWish);
            if (expectedType.isAssignableFrom(SeasonWish.class)) {
                return expectedType.cast(seasonWish);
            }
            throw new IllegalArgumentException("Invalid expected type for season wish update: " + expectedType.getName());
        } else {
            if (request.getId() == null) {
                 throw new IllegalArgumentException("ID must be provided for update.");
            }
            Wish wish = wishMapper.getById(request.getId());
            if (wish == null) {
                throw new RuntimeException("愿望不存在, id: " + request.getId());
            }
            BeanUtils.copyProperties(request, wish);
            // wish.setMid(request.getMid()); // Assuming Wish.mid is String
            wish.setUpdatedAt(LocalDateTime.now());
            wishMapper.update(wish);
            if (expectedType.isAssignableFrom(Wish.class)) {
                return expectedType.cast(wish);
            }
            throw new IllegalArgumentException("Invalid expected type for regular wish update: " + expectedType.getName());
        }
    }
    
    @Override
    @Transactional
    public void deleteWish(Integer id, Long seasonId) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for delete operation.");
        }
        if (seasonId != null) {
            SeasonWish seasonWish = seasonWishMapper.getById(id.longValue());
            if (seasonWish != null) { // Check if found
                if (seasonWish.getSeasonId().equals(seasonId)) {
                    seasonWishMapper.delete(id.longValue());
                } else {
                    // Optionally throw an error for seasonId mismatch
                    // System.err.println("SeasonId mismatch on delete: found wish with id " + id + " but seasonId " + seasonWish.getSeasonId() + " does not match requested " + seasonId);
                }
            } else {
                 // Optionally log or handle case where wish is not found
            }
        } else {
            wishMapper.delete(id);
        }
    }
    
    @Override
    public <T> T getWishById(Integer id, Long seasonId, Class<T> expectedType) {
        if (id == null) return null; // Or throw IllegalArgumentException

        if (seasonId != null) {
            SeasonWish seasonWish = seasonWishMapper.getById(id.longValue());
            if (seasonWish != null && seasonWish.getSeasonId().equals(seasonId)) {
                if (expectedType.isAssignableFrom(SeasonWish.class)) {
                    return expectedType.cast(seasonWish);
                }
                throw new IllegalArgumentException("Invalid expected type for season wish: " + expectedType.getName());
            }
            return null;
        } else {
            Wish wish = wishMapper.getById(id);
            if (wish != null) {
                if (expectedType.isAssignableFrom(Wish.class)) {
                    return expectedType.cast(wish);
                }
                throw new IllegalArgumentException("Invalid expected type for regular wish: " + expectedType.getName());
            }
            return null;
        }
    }
    
    @Override
    public <T> List<T> getAllWishes(Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonWish.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of season wishes: " + expectedType.getName());
            }
            List<SeasonWish> seasonWishes = seasonWishMapper.findAllBySeasonId(seasonId);
            return seasonWishes.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(Wish.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of regular wishes: " + expectedType.getName());
            }
            List<Wish> wishes = wishMapper.findAll();
            return wishes.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }
    
    @Override
    public <T> List<T> getWishesByMid(String midString, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonWish.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of season wishes: " + expectedType.getName());
            }
            Integer memberId;
            try {
                memberId = Integer.parseInt(midString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid mid format: " + midString);
            }
            List<SeasonWish> seasonWishes = seasonWishMapper.getBySeasonIdAndMid(seasonId, memberId);
            return seasonWishes.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(Wish.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of regular wishes: " + expectedType.getName());
            }
            // Assuming wishMapper.findByMid takes String mid
            List<Wish> wishes = wishMapper.findByMid(midString);
            return wishes.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }
}
