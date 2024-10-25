package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * counter控制器
 */
@RestController
@RequestMapping("user")
public class WxuserController {

  final WxuserService wxuserService;
  final MemberService memberService;
  final Logger logger;

  public WxuserController(@Autowired WxuserService wxuserService, @Autowired MemberService memberService) {
    this.wxuserService = wxuserService;
    this.memberService = memberService;
    this.logger = LoggerFactory.getLogger(WxuserController.class);
  }

  @GetMapping("/login")
  public ApiResponse login(@RequestHeader(value = "X-WX-OPENID",required = false)String openid){
    if(StringUtils.isEmpty(openid)){
      return ApiResponse.error("openid null");
    }
    Optional<WxUser> user = wxuserService.getUser(openid);
    if(user.isPresent()){
      //如果有user 则判断是否有member
//      Integer memberCount = memberService.getMemberCountByUid(user.get().getId());
      List<Member> members = memberService.getMembersByUid(user.get().getId());
      HashMap<String,Object> resMap = new HashMap<>();
      resMap.put("user",user);
      resMap.put("members",members);
      return ApiResponse.ok(resMap);
    }
    return ApiResponse.ok(null);
  }

  @PostMapping("/create")
  public  ApiResponse add(@RequestHeader(value = "X-WX-OPENID")String openid){
    Optional<WxUser> user = wxuserService.getUser(openid);
    if(!user.isPresent()) {
      WxUser wxUser =  new WxUser();
      wxUser.setOpenid(openid);
      wxuserService.upsertUser(wxUser);
    }
    return ApiResponse.ok();
  }
}