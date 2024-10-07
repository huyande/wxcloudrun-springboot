package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.WxUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WxuserMapper {

  WxUser getUser(@Param("openid") String openid);
  WxUser getUserById(@Param("id") Integer id);

  void upsertUser(WxUser wxUser);
  void updateRoleUserById(@Param("id") Integer id,@Param("role") Integer role );

}
