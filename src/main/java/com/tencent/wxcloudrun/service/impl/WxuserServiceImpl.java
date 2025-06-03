package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.FamilyMapper;
import com.tencent.wxcloudrun.dao.MemberMapper;
import com.tencent.wxcloudrun.dao.MemberRelasMapper;
import com.tencent.wxcloudrun.dao.WxuserMapper;
import com.tencent.wxcloudrun.dto.FamilyRequest;
import com.tencent.wxcloudrun.dto.VipDto;
import com.tencent.wxcloudrun.model.*;
import com.tencent.wxcloudrun.service.MemberService;
import com.tencent.wxcloudrun.service.WxuserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;

@Service
public class WxuserServiceImpl implements WxuserService {

    final WxuserMapper wxuserMapper;
    final FamilyMapper familyMapper;
    final MemberMapper memberMapper;
    final MemberRelasMapper memberRelasMapper;
    //构造函数注入
    public WxuserServiceImpl(@Autowired WxuserMapper wxuserMapper, @Autowired FamilyMapper familyMapper, @Autowired MemberMapper memberMapper, @Autowired MemberRelasMapper memberRelasMapper) {
        this.memberMapper = memberMapper;
        this.wxuserMapper = wxuserMapper;
        this.familyMapper = familyMapper;
        this.memberRelasMapper = memberRelasMapper;
    }

    @Override
    public Optional<WxUser> getUser(String openid) {
        return Optional.ofNullable(wxuserMapper.getUser(openid));
    }

    @Override
    public void upsertUser(WxUser wxUser) {
        wxuserMapper.upsertUser(wxUser);
    }

    @Override
    public List<WxUser> getUsersByFamilyCode(String code) {
        return wxuserMapper.getUsersByFamilyCode(code);
    }

    @Override
    public Optional<WxUser> getUserByFamilyCode(String code) {
        return Optional.ofNullable(wxuserMapper.getUserByFamilyCode(code));
    }

    @Override
    public void setFailyRela(FamilyRequest familyRequest) {
        Family _family = familyMapper.getOneByCodeAndUid(familyRequest.getCode(), familyRequest.getUid());
        if(_family ==null){//说明没有被绑定过
            Family family = new Family();
            family.setCode(familyRequest.getCode());
            family.setUid(familyRequest.getUid());
            if(familyRequest.getBindMid()!=null){
                family.setBindMid(familyRequest.getBindMid());
            }
            familyMapper.insertOne(family);
            wxuserMapper.updateRoleUserById(familyRequest.getUid(),familyRequest.getRole());
        }
    }

    @Override
    public WxUser getUserById(Integer id) {
        return wxuserMapper.getUserById(id);
    }

    @Override
    public Family getFamilyByCodeAndUid(String code, Integer uid) {
        return familyMapper.getOneByCodeAndUid(code, uid);
    }

    @Override
    public void deleteFamilyRelas(FamilyRequest familyRequest) {
        familyMapper.deleteByCodeAndUid(familyRequest.getCode(), familyRequest.getUid()); //删除家庭()
    }

    @Override
    public WxCheckConfig getWxCheckConfig(String version) {
        return wxuserMapper.getWxCheckConfigs(version);

    }

    @Override
    public void updateUserSettings(Integer id, Integer soundEnabled, Integer animationEnabled) {
        wxuserMapper.updateUserSettings(id, soundEnabled, animationEnabled);
    }

    @Override
    public void updateAtUserById(Integer id) {
        wxuserMapper.updateAtUserById(id);
    }

    @Override
    public double updateVipExpiredAt(Integer id) {
        WxUser user = wxuserMapper.getUserById(id);
        if (user == null) {
            return 0;
        }

        // 获取配置参数
        double minDays = 0; // 最小天数,默认0
        double maxDays = 3; // 最大天数,默认3
        double highProbStart = 2; // 高概率区间起始值,默认2
        double highProbWeight = 0.5; // 高概率权重,默认0.7

        // 生成随机天数
        Random random = new Random();
        double randomNum = random.nextDouble();
        double days;
        if (randomNum < highProbWeight) {
            // 30%的概率生成2-3天
            days = highProbStart + (maxDays - highProbStart) * randomNum;
        } else {
            // 70%的概率生成0-2天
            days = minDays + (highProbStart - minDays) * randomNum;
        }
        // 保留1位小数
        days = Math.round(days * 10.0) / 10.0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = user.getVipExpiredAt();

        // 计算新的过期时间
        LocalDateTime newExpiredAt;
        if (expiredAt == null || expiredAt.isBefore(now)) {
            newExpiredAt = now.plusSeconds((long)(days * 24 * 60 * 60));
        } else {
            newExpiredAt = expiredAt.plusSeconds((long)(days * 24 * 60 * 60));
        }

        wxuserMapper.updateVipExpiredAt(id, newExpiredAt);
        return days; // 向上取整返回天数
    }

    @Override
    public void updateFamilyIsRead(String code, Integer uid, Integer isRead) {
        familyMapper.updateIsReadByCodeAndUid(code,uid,isRead);
    }

    @Override
    public boolean hasBindMid(FamilyRequest familyRequest) {
        if(familyRequest.getRole()!=7 || familyRequest.getBindMid()==null){
            return false;//不需进行绑定的角色 不是自己的角色
        }
        Family family = familyMapper.findByCodeAndBindMid(familyRequest.getCode(),familyRequest.getBindMid());
        if(family!=null){
            return true;
        }
        return false;
    }

    /**
     *
     * @param code 渠道code
     * @param day 奖励天数
     * @param sourceOpenid 来源的openid
     */
    @Override
    public void updateVipExpiredAtByFamilyCode(String code, int day,String sourceOpenid) {
        WxUser user = wxuserMapper.getUserByFamilyCode(code);
        //检查是否已经添加过奖励，不能重复新增
        VipConvertLog vipConvertLog = wxuserMapper.getVipLog(sourceOpenid, code, user.getOpenid());
        if(vipConvertLog!=null){
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = user.getVipExpiredAt();

        // 计算新的过期时间
        LocalDateTime newExpiredAt;
        if (expiredAt == null || expiredAt.isBefore(now)) {
            newExpiredAt = now.plusSeconds((long)(day * 24 * 60 * 60));
        } else {
            newExpiredAt = expiredAt.plusSeconds((long)(day * 24 * 60 * 60));
        }

        wxuserMapper.updateVipExpiredAt(user.getId(), newExpiredAt);
        VipConvertLog vipConvertLog_ = new VipConvertLog();
        vipConvertLog_.setSourceOpenid(sourceOpenid);
        vipConvertLog_.setChannel(code);
        vipConvertLog_.setTargetOpenid(user.getOpenid());
        wxuserMapper.insertVipLog(vipConvertLog_);
    }

    @Override
    public Integer getShareVipCount(String openid) {
        return wxuserMapper.getShareVipCount(openid);
    }

    @Override
    public void updateVipExpired(Integer id, LocalDateTime newExpiredAt) {
        wxuserMapper.updateVipExpiredAt(id, newExpiredAt);
    }

    @Override
    public WxUser updateVipDay(VipDto vipDto) {
        WxUser user = wxuserMapper.getUserByFamilyCode(vipDto.getCode());
        if(user==null){
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = user.getVipExpiredAt();

        // 计算新的过期时间
        LocalDateTime newExpiredAt;
        if (expiredAt == null || expiredAt.isBefore(now)) {
            newExpiredAt = now.plusSeconds((long)(vipDto.getDays() * 24 * 60 * 60));
        } else {
            newExpiredAt = expiredAt.plusSeconds((long)(vipDto.getDays() * 24 * 60 * 60));
        }

        wxuserMapper.updateVipExpiredAt(user.getId(), newExpiredAt);
        return wxuserMapper.getUser(user.getOpenid());
    }

}
