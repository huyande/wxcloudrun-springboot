package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.SeasonWishLogMapper;
import com.tencent.wxcloudrun.dao.WishLogMapper;
import com.tencent.wxcloudrun.dto.WishLogRequest;
import com.tencent.wxcloudrun.model.SeasonWishLog;
import com.tencent.wxcloudrun.model.WishLog;
import com.tencent.wxcloudrun.service.WishLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WishLogServiceImpl implements WishLogService {

    private final WishLogMapper wishLogMapper;
    private final SeasonWishLogMapper seasonWishLogMapper;
    // Assuming MemberPointLogsService is also needed for point checks, and it's already in controller
    // If MemberPointLogsService also becomes season-aware, it needs to be handled.
    // For now, assuming its getPointSum method will be adapted or called with seasonId.

    @Autowired
    public WishLogServiceImpl(WishLogMapper wishLogMapper, SeasonWishLogMapper seasonWishLogMapper) {
        this.wishLogMapper = wishLogMapper;
        this.seasonWishLogMapper = seasonWishLogMapper;
    }

    @Override
    public <T> T getById(Integer id, Long seasonId, Class<T> expectedType) {
        if (id == null) return null;
        if (seasonId != null) {
            SeasonWishLog seasonWishLog = seasonWishLogMapper.getByIdAndSeasonId(id.longValue(), seasonId);
            if (seasonWishLog != null) {
                if (expectedType.isAssignableFrom(SeasonWishLog.class)) {
                    return expectedType.cast(seasonWishLog);
                }
                throw new IllegalArgumentException("Invalid expected type for season wish log: " + expectedType.getName());
            }
            return null;
        } else {
            WishLog wishLog = wishLogMapper.getById(id);
            if (wishLog != null) {
                if (expectedType.isAssignableFrom(WishLog.class)) {
                    return expectedType.cast(wishLog);
                }
                throw new IllegalArgumentException("Invalid expected type for regular wish log: " + expectedType.getName());
            }
            return null;
        }
    }

    @Override
    public <T> List<T> getByUid(String uid, Long seasonId, Class<T> expectedType) {
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonWishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of season wish logs: " + expectedType.getName());
            }
            // SeasonWishLogMapper doesn't have getByUid directly. Assuming mid or other criteria.
            // This method might need re-evaluation based on SeasonWishLogMapper capabilities.
            // For now, returning empty list to avoid compilation error.
            // throw new UnsupportedOperationException("getByUid for SeasonWishLog not directly supported by mapper.");
             List<SeasonWishLog> seasonLogs = Collections.emptyList(); // Placeholder
            return seasonLogs.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(WishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of regular wish logs: " + expectedType.getName());
            }
            List<WishLog> logs = wishLogMapper.getByUid(uid);
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }

    @Override
    public <T> List<T> getByWid(Integer wid, Long seasonId, Class<T> expectedType) {
         if (wid == null) return Collections.emptyList();
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonWishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of season wish logs: " + expectedType.getName());
            }
            List<SeasonWishLog> seasonLogs = seasonWishLogMapper.getBySeasonIdAndWid(seasonId, wid.longValue());
            return seasonLogs.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(WishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of regular wish logs: " + expectedType.getName());
            }
            List<WishLog> logs = wishLogMapper.getByWid(wid);
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public <T> T createWishLog(WishLogRequest request, Long seasonId, Class<T> expectedType) {
        // Point checking logic should be here, using MemberPointLogsService with seasonId
        // Integer lastPointSum = memberPointLogsService.getLastPointSum(request.getMid(), seasonId);
        // if(lastPointSum < request.getPoint()){
        //     throw new RuntimeException("积分不足，不能兑换"); // Or a custom exception
        // }

        if (seasonId != null) {
            SeasonWishLog seasonWishLog = new SeasonWishLog();
            BeanUtils.copyProperties(request, seasonWishLog); // Careful with type differences
            seasonWishLog.setSeasonId(seasonId);
            // SeasonWishLog.wid is Long, WishLogRequest.wid is Integer
            if(request.getWid() != null) seasonWishLog.setWid(request.getWid().longValue());
            // SeasonWishLog.id is Long, auto-generated by DB
            seasonWishLog.setCreatedAt(LocalDateTime.now());
            seasonWishLog.setUpdatedAt(LocalDateTime.now());
            // status default? Assuming 0 if not set in request
            if (seasonWishLog.getStatus() == null) seasonWishLog.setStatus(0);


            seasonWishLogMapper.insert(seasonWishLog);
            if (expectedType.isAssignableFrom(SeasonWishLog.class)) {
                return expectedType.cast(seasonWishLog);
            }
            throw new IllegalArgumentException("Invalid expected type for season wish log creation: " + expectedType.getName());
        } else {
            WishLog wishLog = new WishLog();
            BeanUtils.copyProperties(request, wishLog);
            // WishLog.wid is Integer, WishLogRequest.wid is Integer - OK
            // WishLog.id is Integer, auto-generated
            wishLog.setCreatedAt(LocalDateTime.now());
            wishLog.setUpdatedAt(LocalDateTime.now());
            if (wishLog.getStatus() == null) wishLog.setStatus(0);

            wishLogMapper.insert(wishLog);
            if (expectedType.isAssignableFrom(WishLog.class)) {
                return expectedType.cast(wishLog);
            }
            throw new IllegalArgumentException("Invalid expected type for regular wish log creation: " + expectedType.getName());
        }
    }

    @Override
    @Transactional
    public <T> T updateWishLog(Integer id, WishLogRequest request, Long seasonId, Class<T> expectedType) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null for update");

        if (seasonId != null) {
            SeasonWishLog seasonWishLog = seasonWishLogMapper.getByIdAndSeasonId(id.longValue(), seasonId);
            if (seasonWishLog == null) {
                throw new RuntimeException("Season wish log not found with id: " + id + " for seasonId: " + seasonId);
            }
            // Update specific fields from request. status, endTime are typical.
            if (request.getStatus() != null) {
                seasonWishLog.setStatus(request.getStatus());
            }
            if (request.getEndTime() != null) {
                 try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime localDateTime = LocalDateTime.parse(request.getEndTime(), formatter);
                    seasonWishLog.setEndTime(localDateTime);
                } catch (Exception e) {
                    // Log error or throw custom exception for bad date format
                    throw new IllegalArgumentException("Invalid endTime format: " + request.getEndTime(), e);
                }
            }
            // Potentially other fields from WishLogRequest like info, amount, unit, unitType if they are updatable
            if(request.getInfo() != null) seasonWishLog.setInfo(request.getInfo());
            if(request.getAmount() != null) seasonWishLog.setAmount(request.getAmount());
            if(request.getUnit() != null) seasonWishLog.setUnit(request.getUnit());
            if(request.getUnitType() != null) seasonWishLog.setUnitType(request.getUnitType());

            seasonWishLog.setUpdatedAt(LocalDateTime.now());
            seasonWishLogMapper.update(seasonWishLog);
            if (expectedType.isAssignableFrom(SeasonWishLog.class)) {
                return expectedType.cast(seasonWishLog);
            }
            throw new IllegalArgumentException("Invalid expected type for season wish log update: " + expectedType.getName());
        } else {
            WishLog wishLog = wishLogMapper.getById(id);
            if (wishLog == null) {
                throw new RuntimeException("Wish log not found for id: " + id);
            }
             if (request.getStatus() != null) {
                wishLog.setStatus(request.getStatus());
            }
            if (request.getEndTime() != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime localDateTime = LocalDateTime.parse(request.getEndTime(), formatter);
                    wishLog.setEndTime(localDateTime);
                } catch (Exception e) {
                     throw new IllegalArgumentException("Invalid endTime format: " + request.getEndTime(), e);
                }
            }
            if(request.getInfo() != null) wishLog.setInfo(request.getInfo());
            if(request.getAmount() != null) wishLog.setAmount(request.getAmount());
            if(request.getUnit() != null) wishLog.setUnit(request.getUnit());
            if(request.getUnitType() != null) wishLog.setUnitType(request.getUnitType());

            wishLog.setUpdatedAt(LocalDateTime.now());
            wishLogMapper.update(wishLog);
            if (expectedType.isAssignableFrom(WishLog.class)) {
                return expectedType.cast(wishLog);
            }
            throw new IllegalArgumentException("Invalid expected type for regular wish log update: " + expectedType.getName());
        }
    }
    
    @Override
    @Transactional
    public void legacyUpdate(WishLog wishLog, Long seasonId) {
        // This is the original update method. It needs to be adapted for seasonId if used.
        // Or, if updateWishLog handles all cases, this might be removed.
        // For now, assuming it operates on regular WishLog if seasonId is null.
        if (seasonId != null) {
            // Logic for updating SeasonWishLog using a SeasonWishLog object (conversion needed)
            // SeasonWishLog seasonLog = new SeasonWishLog();
            // BeanUtils.copyProperties(wishLog, seasonLog); // Be careful with ID and other fields
            // seasonLog.setSeasonId(seasonId);
            // seasonLog.setUpdatedAt(LocalDateTime.now());
            // if(wishLog.getId() != null) seasonLog.setId(wishLog.getId().longValue());
            // if(wishLog.getWid() != null) seasonLog.setWid(wishLog.getWid().longValue());
            // seasonWishLogMapper.update(seasonLog);
            throw new UnsupportedOperationException("legacyUpdate for season logs needs proper implementation or DTO based update should be used.");
        } else {
            wishLog.setUpdatedAt(LocalDateTime.now()); // original WishLogMapper.update was missing this
            wishLogMapper.update(wishLog);
        }
    }


    @Override
    public <T> List<T> queryTimeTask(Long seasonId, Class<T> expectedType) {
        // Assuming time tasks can also be seasonal
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonWishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of season time tasks: " + expectedType.getName());
            }
            // SeasonWishLogMapper needs a queryTimeTask equivalent
            // List<SeasonWishLog> seasonTasks = seasonWishLogMapper.queryTimeTaskBySeason(seasonId); // Hypothetical
            // For now, returning empty list.
            List<SeasonWishLog> seasonTasks = Collections.emptyList(); // Placeholder
            return seasonTasks.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(WishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of regular time tasks: " + expectedType.getName());
            }
            List<WishLog> tasks = wishLogMapper.queryTimeTask();
            return tasks.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id, Long seasonId) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null for delete");
        if (seasonId != null) {
            seasonWishLogMapper.deleteByIdAndSeasonId(id.longValue(), seasonId);
        } else {
            wishLogMapper.deleteById(id);
        }
    }

    @Override
    public Integer getSumNumByMid(Integer mid, Long seasonId) {
        if (mid == null) return 0;
        if (seasonId != null) {
            // SeasonWishLogMapper needs getSumNumBySeasonIdAndMid or similar.
            // return seasonWishLogMapper.getTotalPointsBySeasonIdAndMid(seasonId, mid); // Example if it sums points
            // For "SumNum" if it means count:
            return seasonWishLogMapper.getCountBySeasonIdAndMidAndStatus(seasonId, mid, null); // null for all statuses
        } else {
            return wishLogMapper.getSumNumByMid(mid); // Assuming this is a count
        }
    }

    @Override
    public List<Map<String, Object>> getByMid(Integer mid, Long seasonId) {
         if (mid == null) return Collections.emptyList();
        if (seasonId != null) {
            // SeasonWishLogMapper has getBySeasonIdAndMid which returns List<SeasonWishLog>
            // The original WishLogService returns List<Map<String,Object>>
            // This requires conversion or the mapper method to be changed.
            // For now, let's assume we adapt.
            // This method needs careful review for what the Map should contain.
            // List<SeasonWishLog> seasonLogs = seasonWishLogMapper.getBySeasonIdAndMid(seasonId, mid);
            // return seasonLogs.stream().map(log -> {
            //     Map<String, Object> map = new HashMap<>();
            //     // Populate map from seasonLog ...
            //     return map;
            // }).collect(Collectors.toList());
            throw new UnsupportedOperationException("getByMid for SeasonWishLog returning List<Map> needs specific mapping logic.");
        } else {
            return wishLogMapper.getByMid(mid);
        }
    }

    @Override
    public <T> List<T> getAllLogByStatus(Integer mid, Integer status, Long seasonId, Class<T> expectedType) {
        if (mid == null || status == null) return Collections.emptyList();
        if (seasonId != null) {
            if (!expectedType.isAssignableFrom(SeasonWishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of season wish logs: " + expectedType.getName());
            }
            List<SeasonWishLog> seasonLogs = seasonWishLogMapper.getBySeasonIdAndMidAndStatus(seasonId, mid, status);
            return seasonLogs.stream().map(expectedType::cast).collect(Collectors.toList());
        } else {
            if (!expectedType.isAssignableFrom(WishLog.class)) {
                throw new IllegalArgumentException("Invalid expected type for list of regular wish logs: " + expectedType.getName());
            }
            List<WishLog> logs = wishLogMapper.getAllLogByMidAndStatus(mid, status);
            return logs.stream().map(expectedType::cast).collect(Collectors.toList());
        }
    }

    @Override
    public Map<String, Object> getByMidWithPage(Integer mid, Integer page, Integer pageSize, Integer status, Long seasonId) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;

        if (seasonId != null) {
            int total = seasonWishLogMapper.getCountBySeasonIdAndMidAndStatus(seasonId, mid, status);
            List<Map<String, Object>> list = seasonWishLogMapper.getBySeasonIdAndMidWithPage(seasonId, mid, status, offset, pageSize);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("pageNum", page);
            result.put("pageSize", pageSize);
            result.put("list", list);
            return result;
        } else {
            int total = wishLogMapper.getCountByMidAndStatus(mid, status);
            List<Map<String, Object>> list = wishLogMapper.getByMidWithPage(mid, status, offset, pageSize);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", total);
            result.put("pageNum", page);
            result.put("pageSize", pageSize);
            result.put("list", list);
            return result;
        }
    }
}
