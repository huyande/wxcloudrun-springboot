package com.tencent.wxcloudrun.controller.drugRecord;

import cn.hutool.core.date.DateUtil;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.drugRecord.WxUsers;
import com.tencent.wxcloudrun.service.drugRecord.WxUsersService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/drugRecord/wxUsers")
public class WxUsersController {
    
    @Autowired
    private WxUsersService wxUsersService;

    @GetMapping("/login")
    public ApiResponse login(@RequestHeader(value = "X-WX-OPENID",required = false)String openid, @RequestParam String version){
        if(StringUtils.isEmpty(openid)){
          return ApiResponse.error("openid null");
        }
        WxUsers wxUsers = wxUsersService.getByOpenid(openid);
        WxCheckConfig wxCheckConfig = wxUsersService.getWxCheckConfig(version);
        HashMap<String,Object> resMap = new HashMap<>();
        resMap.put("checkShow",wxCheckConfig==null?0:wxCheckConfig.getStatus());
        if(wxUsers==null){
            WxUsers user = new WxUsers();
            user.setOpenid(openid);
            wxUsersService.insert(user);
            resMap.put("openid",openid);
        }
        if(wxUsers!=null){
          //判断user的更新时间是否是今天 user.get().getUpdatedAt()
          //获取系统默认时区
          ZoneId zoneId = ZoneId.systemDefault();
          //时区的日期和时间
          ZonedDateTime zonedDateTime = wxUsers.getUpdatedAt().atZone(zoneId);
          //获取时刻
          Date date = Date.from(zonedDateTime.toInstant());
          if(!DateUtil.isSameDay(date, new Date())) {
            // The user's update time is today
            wxUsers.setUpdatedAt(LocalDateTime.now());
            wxUsersService.update(wxUsers);
          }
          resMap.put("openid",wxUsers.getOpenid());
        }
        return ApiResponse.ok(resMap);
    }
} 