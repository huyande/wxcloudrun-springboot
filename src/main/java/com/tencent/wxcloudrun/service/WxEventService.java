package com.tencent.wxcloudrun.service;

import java.util.Map;

public interface WxEventService {
    String eventHandel(Map<String, Object> jsonMap);
}
