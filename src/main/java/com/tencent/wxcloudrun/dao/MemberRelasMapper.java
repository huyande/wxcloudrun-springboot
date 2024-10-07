package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Member;
import com.tencent.wxcloudrun.model.MemberRelas;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberRelasMapper {
    void insertOne(MemberRelas memberRelas);
}
