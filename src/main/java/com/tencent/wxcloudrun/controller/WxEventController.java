package com.tencent.wxcloudrun.controller;

import cn.hutool.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class WxEventController {

    @PostMapping("/handel")
    public String handel(@RequestBody JSONObject json) {
        System.out.printf(json.toString());
        return "ok";
    }
}
