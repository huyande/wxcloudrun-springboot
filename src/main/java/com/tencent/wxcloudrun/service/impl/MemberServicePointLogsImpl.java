package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.MemberPointLogsMapper;
import com.tencent.wxcloudrun.dao.WishLogMapper;
import com.tencent.wxcloudrun.dto.MemberPointLogsRequest;
import com.tencent.wxcloudrun.model.MemberPointLogs;
import com.tencent.wxcloudrun.service.MemberPointLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class MemberServicePointLogsImpl implements MemberPointLogsService {

    //积分日志
    final MemberPointLogsMapper memberPointLogsMapper;
    //构造函数注入
    public MemberServicePointLogsImpl(@Autowired MemberPointLogsMapper memberPointLogsMapper) {
        this.memberPointLogsMapper = memberPointLogsMapper;
    }

    @Override
    public List<MemberPointLogs> getLogsByMidAndDate(Integer mid, LocalDateTime startAt,LocalDateTime endAt) {
        return memberPointLogsMapper.getLogsByMidAndDate(mid, startAt,endAt);
    }

    @Override
    public MemberPointLogs insert(MemberPointLogsRequest memberPointLogsRequest) {
        MemberPointLogs log = memberPointLogsMapper.getLogsByDayMidAndRuleId(memberPointLogsRequest.getDay(), memberPointLogsRequest.getMid(), memberPointLogsRequest.getRuleId());
        if(log!=null){
            if(memberPointLogsRequest.getNum()==0){
                memberPointLogsMapper.delete(log.getId());
                return null;
            }
            //更新
            memberPointLogsMapper.updateById(log.getId(), memberPointLogsRequest.getNum(), memberPointLogsRequest.getUid());
            return log;
        }else{
            MemberPointLogs memberPointLogs = new MemberPointLogs();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(memberPointLogsRequest.getDay(), formatter);

            memberPointLogs.setDay(dateTime);
            memberPointLogs.setMid(memberPointLogsRequest.getMid());
            memberPointLogs.setUid(memberPointLogsRequest.getUid());
            memberPointLogs.setRuleId(memberPointLogsRequest.getRuleId());
            memberPointLogs.setNum(memberPointLogsRequest.getNum());
            memberPointLogs.setType(memberPointLogsRequest.getType());
            memberPointLogsMapper.insertOne(memberPointLogs);
            return memberPointLogs;
        }

    }

    @Override
    public Integer getPointSumByMid(Integer mid) {
        return memberPointLogsMapper.getPointSumByMid(mid);
    }

    @Override
    public Integer getPointDaysByMid(Integer mid) {
        return memberPointLogsMapper.getPointDaysByMid(mid);
    }

    @Override
    public List<Map<String, Object>> getPointLogsByMidAndMonth(Integer mid) {
        List<Map<String, Object>> result = memberPointLogsMapper.getPointLogsByMidAndMonth(mid);
        return result;
    }

    @Override
    public Integer getAllCountLogsByDayMid(Integer mid) {
        return memberPointLogsMapper.getAllCountLogsByDayMid(mid);
    }

    @Override
    public Map<String, Object> getPointLogsByMid(Integer mid, Integer page) {
        Map<String, Object> map = new HashMap<>();
        int pageSize = 10;
        if (page == null) {
            page = 1;
        }
        Integer offset = (page - 1) * pageSize;
        List<Map<String, Object>> logs = memberPointLogsMapper.getPointLogsByMid(mid, pageSize, offset);
        Integer count = memberPointLogsMapper.getPointLogsByMidCount(mid);
        map.put("total", count);
        map.put("rows", logs);
        return map;
    }

}
