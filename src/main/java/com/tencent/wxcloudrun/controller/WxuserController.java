package com.tencent.wxcloudrun.controller;

import cn.hutool.core.date.DateUtil;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.FamilyRequest;
import com.tencent.wxcloudrun.dto.SettingRequest;
import com.tencent.wxcloudrun.model.Family;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.WxUser;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
  public ApiResponse login(@RequestHeader(value = "X-WX-OPENID",required = false)String openid,@RequestParam String version){
    if(StringUtils.isEmpty(openid)){
      return ApiResponse.error("openid null");
    }
    Optional<WxUser> user = wxuserService.getUser(openid);
    WxCheckConfig wxCheckConfig = wxuserService.getWxCheckConfig(version);
    HashMap<String,Object> resMap = new HashMap<>();
    if(wxCheckConfig==null){
      resMap.put("checkShow",0);
    }else{
      resMap.put("checkShow",wxCheckConfig.getStatus());
    }
    if(user.isPresent()){
      //判断user的更新时间是否是今天 user.get().getUpdatedAt()
      //获取系统默认时区
      ZoneId zoneId = ZoneId.systemDefault();
      //时区的日期和时间
      ZonedDateTime zonedDateTime = user.get().getUpdatedAt().atZone(zoneId);
      //获取时刻
      Date date = Date.from(zonedDateTime.toInstant());
      if(!DateUtil.isSameDay(date, new Date())) {
        // The user's update time is today
        wxuserService.updateAtUserById(user.get().getId());
      }
      //如果有user 则判断是否有member
//      Integer memberCount = memberService.getMemberCountByUid(user.get().getId());
      List<Member> members = memberService.getMembersByUid(user.get().getId());
      resMap.put("user",user);
      resMap.put("members",members);
      return ApiResponse.ok(resMap);
    }
    resMap.put("user",null);
    resMap.put("members",null);
    return ApiResponse.ok(resMap);
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

  @PutMapping("/settings/{id}")
  public ApiResponse updateSettings(
          @PathVariable Integer id, @RequestBody SettingRequest settingRequest) {
    
    // 验证参数值是否合法
    try {
        // 使用用户id更新设置
        wxuserService.updateUserSettings(id, settingRequest.getSoundEnabled(), settingRequest.getAnimationEnabled());

        // 重新获取更新后的用户信息
        WxUser updatedUser = wxuserService.getUserById(id);
        return ApiResponse.ok(updatedUser);
    } catch (Exception e) {
        return ApiResponse.error("Update failed");
    }
  }

}