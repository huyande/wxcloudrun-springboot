package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.FamilyRequest;
import com.tencent.wxcloudrun.dto.VipDto;
import com.tencent.wxcloudrun.model.Family;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.WxUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WxuserService {

  Optional<WxUser> getUser(String openid);

  void upsertUser(WxUser wxUser);

  List<WxUser> getUsersByFamilyCode(String code);

  Optional<WxUser> getUserByFamilyCode(String code);

  /**
   *
   * @param code
   * @param bindUser 需要被绑定的user
   * @param adminUser 绑定人
   * @param members 绑定人的孩子
   */
  void setFailyRela(FamilyRequest familyRequest);

  WxUser getUserById(Integer id);

  Family getFamilyByCodeAndUid(String code, Integer uid);

  void deleteFamilyRelas(FamilyRequest familyRequest);

  WxCheckConfig getWxCheckConfig(String version);

  void updateUserSettings(Integer id, Integer soundEnabled, Integer animationEnabled);

  void updateAtUserById(Integer id);

  double updateVipExpiredAt(Integer id);

  void updateFamilyIsRead(String code, Integer uid, Integer isRead);

  boolean hasBindMid(FamilyRequest familyRequest);

  void updateVipExpiredAtByFamilyCode(String channel, int day,String openid);

  Integer getShareVipCount(String openid);

  void updateVipExpired(Integer id, LocalDateTime newExpiredAt,Integer isPay);

  WxUser updateVipDay(VipDto vipDto);
}
