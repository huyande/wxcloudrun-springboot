package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.MemberRequest;
import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("member")
public class MemberController {

    final MemberService memberService;
    final WxuserService wxuserService;
    final Logger logger;

    public MemberController(MemberService memberService,WxuserService wxuserService){
        this.memberService = memberService;
        this.wxuserService = wxuserService;
        this.logger =  LoggerFactory.getLogger(MemberController.class);
    }

    @PostMapping("/create")
    public ApiResponse create(@RequestBody MemberRequest memberRequest){
        Member member = memberService.insert(memberRequest);
        return ApiResponse.ok(member);
    }
}
