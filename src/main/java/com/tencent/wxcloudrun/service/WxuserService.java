package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.WxUser;

import java.util.Optional;

public interface WxuserService {

  Optional<WxUser> getUser(String openid);

  void upsertUser(WxUser wxUser);

}
