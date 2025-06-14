package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.VipConvertLog;
import com.tencent.wxcloudrun.model.WxCheckConfig;
import com.tencent.wxcloudrun.model.WxUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WxuserMapper {

  WxUser getUser(@Param("openid") String openid);
  WxUser getUserById(@Param("id") Integer id);

  void upsertUser(WxUser wxUser);
  void updateRoleUserById(@Param("id") Integer id,@Param("role") Integer role );

  List<WxUser> getUsersByFamilyCode(@Param("familyCode") String familyCode);
  WxUser getUserByFamilyCode(@Param("familyCode") String familyCode);

  WxCheckConfig getWxCheckConfigs(@Param("version") String version);

  void updateUserSettings(@Param("id") Integer id,
                         @Param("soundEnabled") Integer soundEnabled,
                         @Param("animationEnabled") Integer animationEnabled);

  void updateAtUserById(@Param("id")Integer id);

  void updateVipExpiredAt(@Param("id") Integer id, @Param("vipExpiredAt") LocalDateTime vipExpiredAt,@Param("isPay") Integer isPay);

  void insertVipLog(VipConvertLog vipConvertLog);
  VipConvertLog getVipLog(@Param("sourceOpenid") String sourceOpenid, @Param("channel") String channel, @Param("targetOpenid") String targetOpenid);

  Integer getShareVipCount(@Param("openid") String openid);
}
