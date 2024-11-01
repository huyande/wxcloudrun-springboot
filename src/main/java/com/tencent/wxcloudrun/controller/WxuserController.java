package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.FamilyRequest;
import com.tencent.wxcloudrun.model.Family;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import cn.hutool.core.util.RandomUtil;
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
  public  ApiResponse add(@RequestHeader(value = "X-WX-OPENID")String openid) {
    Optional<WxUser> user = wxuserService.getUser(openid);
    if(!user.isPresent()) {
      WxUser wxUser =  new WxUser();
      wxUser.setOpenid(openid);
      wxUser.setFamilyCode(RandomUtil.randomNumbers(6));
      wxuserService.upsertUser(wxUser);
      return ApiResponse.ok(wxUser);
    }
    return ApiResponse.ok(user.get());
  }


  @GetMapping("/getUsersByUid/{id}")
  public ApiResponse getUser( @PathVariable Integer id) {
    WxUser wxUser = wxuserService.getUserById(id);
    return ApiResponse.ok(wxUser);
  }

  @GetMapping("/getUsersByFamilyCode")
  public ApiResponse getUsersByFamilyCode(@RequestParam String code){
    List<WxUser> users = wxuserService.getUsersByFamilyCode(code);
    return ApiResponse.ok(users);
  }

  /**
   * 判断是否已经绑定过
   * @param code
   * @param openid
   * @return
   */
  @GetMapping("/hasRelaFamily")
  public ApiResponse hasRelaFamily(@RequestParam String code,@RequestHeader(value = "X-WX-OPENID")String openid) {
    Optional<WxUser> user = wxuserService.getUser(openid);
    if (!user.isPresent()) {
      return ApiResponse.ok(false);
    }
    //当自己点时候，直接返回true
    if(user.get().getFamilyCode().equals(code)){
      return ApiResponse.ok(true);
    }
    Family family = wxuserService.getFamilyByCodeAndUid(code, user.get().getId());
    if (family == null) {
      return ApiResponse.ok(false);
    }
    return ApiResponse.ok(true);
  }

  /**
   * 绑定
   * @param code
   * @param openid
   * @return
   */
  @PostMapping("/familyRelas")
  public ApiResponse familyRelas(@RequestBody FamilyRequest familyRequest, @RequestHeader(value = "X-WX-OPENID")String openid) {
    //要绑定的uid
    Optional<WxUser> user = wxuserService.getUser(openid);
    if (!user.isPresent()) {
      return ApiResponse.error("openid null");
    }

    wxuserService.setFailyRela(familyRequest);

    return ApiResponse.ok();
  }

  @DeleteMapping("/familyRelas")
  public ApiResponse deleteFamilyRelas(@RequestBody FamilyRequest familyRequest) {
    wxuserService.deleteFamilyRelas(familyRequest);
    return ApiResponse.ok();
  }

}