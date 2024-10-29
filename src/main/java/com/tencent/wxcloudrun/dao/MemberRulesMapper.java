package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.dto.MemberRuleRequest;
import com.tencent.wxcloudrun.model.MemberRules;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberRulesMapper {

    MemberRules getRuleByNameAndMid(@Param("name") String name, @Param("mid") Integer mid);
    List<MemberRules> getRuleByNameAndMids(@Param("name") String name, @Param("mid") Integer mid);
    void insertOne(MemberRules memberRules);
    List<MemberRules> getRulesByMid(@Param("mId") Integer mId);
    int delete(@Param("id") Integer id);

    MemberRules getRuleById(@Param("id") Integer id);

    void updateRuleById(MemberRules memberRules);
}
