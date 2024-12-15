package com.tencent.wxcloudrun.controller;

import cn.hutool.json.JSONObject;
import com.tencent.wxcloudrun.service.WxEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import com.tencent.wxcloudrun.model.SubscribeLog;

@RestController
@RequestMapping("/event")
public class WxEventController {

    final Logger logger;
    final WxEventService wxEventService;

    public WxEventController(@Autowired WxEventService wxEventService) {
        this.wxEventService = wxEventService;
        this.logger = LoggerFactory.getLogger(WxEventController.class);;
    }

    @PostMapping("/handel")
    public String handel(@RequestBody Map<String, Object> jsonMap) {
//        logger.info("事件响应：{}",jsonMap);
//        String msgType = (String) jsonMap.get("MsgType");
//        if(msgType.equals("event")) {
//            wxEventService.eventHandel(jsonMap);
//        }
        return "";
    }

    @GetMapping("/check-subscription")
    public ResponseEntity<List<SubscribeLog>> checkSubscription(
            @RequestHeader("X-WX-OPENID") String openid,
            @RequestParam("templateIds") List<String> templateIds) {
        logger.info("检查模板订阅状态 - openid: {}, templateIds: {}", openid, templateIds);
        List<SubscribeLog> subscriptions = wxEventService.checkTodaySubscriptions(openid, templateIds);
        return ResponseEntity.ok(subscriptions);
    }
}
