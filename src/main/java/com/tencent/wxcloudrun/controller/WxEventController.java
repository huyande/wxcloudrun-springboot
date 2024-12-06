package com.tencent.wxcloudrun.controller;

import cn.hutool.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class WxEventController {

    final Logger logger;

    public WxEventController() {
        this.logger = LoggerFactory.getLogger(WxEventController.class);;
    }

    @PostMapping("/handel")
    public String handel(@RequestBody JSONObject json) {
        logger.info("事件响应：",json.toString());
        return "";
    }
}
