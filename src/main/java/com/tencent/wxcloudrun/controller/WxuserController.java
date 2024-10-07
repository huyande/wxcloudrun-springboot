package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.CounterService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * counter控制器
 */
@RestController
@RequestMapping("user")
public class WxuserController {

  final WxuserService wxuserService;
  final Logger logger;

  public WxuserController(@Autowired WxuserService wxuserService) {
    this.wxuserService = wxuserService;
    this.logger = LoggerFactory.getLogger(WxuserController.class);
  }

  @GetMapping("/login")
  public ApiResponse login(@RequestHeader(value = "X-WX-OPENID",required = false)String openid){
    if(StringUtils.isEmpty(openid)){
      return ApiResponse.error("openid null");
    }
    Optional<WxUser> user = wxuserService.getUser(openid);
    if(user.isPresent()){
      return ApiResponse.ok(user);
    }
    return ApiResponse.ok(null);
  }
}