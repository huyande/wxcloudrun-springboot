package com.tencent.wxcloudrun.controller;

import cn.hutool.json.JSONObject;
import com.tencent.wxcloudrun.service.WxEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
        logger.info("事件响应：{}",jsonMap);
        String msgType = (String) jsonMap.get("MsgType");
        if(msgType.equals("event")) {
            wxEventService.eventHandel(jsonMap);
        }
        return "";
    }
}
