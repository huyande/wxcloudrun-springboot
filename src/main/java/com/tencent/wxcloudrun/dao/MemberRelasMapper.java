package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.MemberRelas;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberRelasMapper {
    void insertOne(MemberRelas memberRelas);
}
