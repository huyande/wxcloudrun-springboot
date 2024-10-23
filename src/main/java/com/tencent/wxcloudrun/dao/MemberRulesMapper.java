package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.MemberRules;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberRulesMapper {
    void insertOne(MemberRules memberRules);
    List<MemberRules> getRulesByMid(@Param("mId") Integer mId);
    void delete(@Param("id") Integer id);
}
